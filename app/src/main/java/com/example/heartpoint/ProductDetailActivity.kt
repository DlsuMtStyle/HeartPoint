package com.example.heartpoint

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // 返回按鈕設置
        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // 返回上一頁
        }

        val tvTitle: TextView = findViewById(R.id.tv_title)

        // 獲取並顯示使用者點數
        fetchUserPoints(tvTitle)

        // 獲取從上一頁傳遞過來的商品 ID
        val productId = intent.getStringExtra("productId")
        if (productId != null) {
            fetchProductDetails(productId)
        } else {
            Toast.makeText(this, "無法載入商品詳情", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 兌換按鈕處理
        val btnRegisterEvent: Button = findViewById(R.id.btn_register_event)
        btnRegisterEvent.setOnClickListener {
            if (currentUserId != null && productId != null) {
                showRedeemDialog(productId)
            } else {
                Toast.makeText(this, "尚未登入，請先登入後操作", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserPoints(tvTitle: TextView) {
        if (currentUserId != null) {
            db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val point = document.getLong("point") ?: 0
                        tvTitle.text = point.toString()
                    } else {
                        tvTitle.text = "0"
                    }
                }
                .addOnFailureListener {
                    tvTitle.text = "無法獲取您的點數"
                    Toast.makeText(this, "點數獲取失敗，請稍後再試", Toast.LENGTH_SHORT).show()
                }
        } else {
            tvTitle.text = "尚未登入"
            Toast.makeText(this, "尚未登入，請先登入", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProductDetails(productId: String) {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val title = document.getString("title") ?: "未提供標題"
                    val imageUrl = document.getString("image") ?: ""
                    val startdate = document.getString("startdate") ?: "未提供開始時間"
                    val enddate = document.getString("enddate") ?: "未提供結束時間"
                    val location = document.getString("location") ?: "未提供兌換通路"
                    val point = document.getLong("point")?.toInt() ?: 0

                    // 更新 UI
                    findViewById<TextView>(R.id.tv_product_title).text = title
                    findViewById<TextView>(R.id.tv_change_date_range).text = "兌換時間：$startdate ~ $enddate"
                    findViewById<TextView>(R.id.tv_change_location_name).text = "兌換通路：$location"
                    findViewById<TextView>(R.id.tv_product_point).text = point.toString()

                    val imgProductDetail = findViewById<ImageView>(R.id.img_product_detail)
                    Glide.with(this)
                        .load(imageUrl) // 從 URL 加載圖片
                        .into(imgProductDetail)
                } else {
                    Toast.makeText(this, "無法找到商品詳情", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(this, "載入商品詳情失敗", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    @SuppressLint("MissingInflatedId")
    private fun showRedeemDialog(productId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_redeem, null)
        val etRedeemQuantity = dialogView.findViewById<EditText>(R.id.et_redeem_quantity)

        AlertDialog.Builder(this)
            .setTitle("請輸入兌換數量")
            .setView(dialogView)
            .setPositiveButton("兌換") { _, _ ->
                val quantityStr = etRedeemQuantity.text.toString()
                if (quantityStr.isNotEmpty()) {
                    val quantity = quantityStr.toIntOrNull()
                    if (quantity != null && quantity > 0) {
                        redeemProduct(productId, quantity)
                    } else {
                        Toast.makeText(this, "請輸入有效的數量", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "請輸入兌換數量", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun redeemProduct(productId: String, quantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "無法獲取用戶資訊", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = db.collection("users").document(userId)
        val productRef = db.collection("products").document(productId)

        // 讀取用戶和商品資訊
        userRef.get().addOnSuccessListener { userSnapshot ->
            val userPoints = userSnapshot.getLong("point") ?: 0

            productRef.get().addOnSuccessListener { productSnapshot ->
                if (productSnapshot.exists()) {
                    val productPoints = productSnapshot.getLong("point") ?: 0
                    val productTitle = productSnapshot.getString("title") ?: "未提供標題"
                    val productImageUrl = productSnapshot.getString("image") ?: ""

                    // 計算兌換所需總點數
                    val totalCost = productPoints * quantity

                    if (userPoints >= totalCost) {
                        // 用戶點數足夠，執行扣除點數
                        val updatedPoints = userPoints - totalCost

                        userRef.update("point", updatedPoints).addOnSuccessListener {
                            Toast.makeText(this, "兌換成功！", Toast.LENGTH_SHORT).show()
                            findViewById<TextView>(R.id.tv_title).text = "$updatedPoints"

                            // 記錄兌換的禮物
                            recordRedeemedGift(
                                userId = userId,
                                giftId = productId,
                                title = productTitle,
                                imageUrl = productImageUrl,
                                quantity = quantity
                            )

                            // 記錄兌換歷史
                            recordRedeemHistory(
                                userId = userId,
                                title = productTitle
                            )

                        }.addOnFailureListener {
                            Toast.makeText(this, "更新用戶點數失敗", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // 用戶點數不足
                        Toast.makeText(this, "點數不足，無法兌換", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "無法找到商品", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "讀取商品資訊失敗", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "讀取用戶點數失敗", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recordRedeemHistory(userId: String, title: String) {
        val historyData = mapOf(
            "title" to title,
            "date" to System.currentTimeMillis() // 當前時間戳
        )

        db.collection("users")
            .document(userId) // 使用者的文件 ID
            .collection("redeem_records") // 子集合
            .add(historyData) // 自動生成文件 ID
            .addOnSuccessListener {
                Toast.makeText(this, "兌換記錄已成功新增！", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "記錄兌換歷史失敗：${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun recordRedeemedGift(userId: String, giftId: String, title: String, imageUrl: String, quantity: Int) {
        val giftData = mapOf(
            "title" to title,
            "id" to giftId,
            "image" to imageUrl,
            "date" to System.currentTimeMillis(), // 使用當前時間戳記錄兌換時間
            "quantity" to quantity // 兌換的數量
        )

        db.collection("users")
            .document(userId) // 使用者的文件 ID
            .collection("redeemed_gifts") // 子集合
            .document(giftId) // 每個兌換的禮物文件 ID
            .set(giftData)
            .addOnSuccessListener {
                Toast.makeText(this, "禮物已成功記錄到禮物盒！", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "記錄禮物兌換失敗：${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


}