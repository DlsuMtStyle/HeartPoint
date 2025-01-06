package com.example.productdetail

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.heartpoint.R
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetail : AppCompatActivity() {
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvNote: TextView
    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var tvQuantity: TextView
    private lateinit var imgProduct: ImageView
    private lateinit var imgHeartCenter: ImageView
    private lateinit var tvLoveCount: TextView

    private var quantity: Int = 1 // 初始化數量
    private var productId: String = "product_1" // Firestore 文件 ID
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 實例

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_product_detail)

        // 初始化 UI 元件
        initializeUI()

        // 從 Firestore 加載數據
        loadDataFromFirestore()

        // 設定數量控制邏輯
        setupQuantityButtons()

        // 設定兌換按鈕邏輯
        setupRedeemButton()
    }

    private fun initializeUI() {
        tvTitle = findViewById(R.id.tv_title)
        tvDescription = findViewById(R.id.tv_description)
        tvNote = findViewById(R.id.tv_note)
        btnIncrease = findViewById(R.id.btn_increase)
        btnDecrease = findViewById(R.id.btn_decrease)
        tvQuantity = findViewById(R.id.tv_quantity)
        imgProduct = findViewById(R.id.img_product)
        imgHeartCenter = findViewById(R.id.img_heart_center)
        tvLoveCount = findViewById(R.id.tv_store_love_count_center)
    }

    private fun loadDataFromFirestore() {
        db.collection("products").document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    tvTitle.text = document.getString("name") ?: ""
                    tvDescription.text = document.getString("description") ?: ""
                    tvNote.text = document.getString("note") ?: ""
                    quantity = document.getLong("quantity")?.toInt() ?: 1
                    tvQuantity.text = quantity.toString()

                    tvLoveCount.text = document.getLong("love_count")?.toString() ?: "0"

                    val productImage = document.getString("image")
                    val loveImage = document.getString("love_image")
                    if (productImage != null) Glide.with(this).load(productImage).into(imgProduct)
                    if (loveImage != null) Glide.with(this).load(loveImage).into(imgHeartCenter)
                } else {
                    Toast.makeText(this, "未找到產品數據", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "數據加載失敗: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupQuantityButtons() {
        btnIncrease.setOnClickListener {
            quantity += 1
            updateQuantityDisplay()
            updateFirestoreQuantity()
        }

        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity -= 1
                updateQuantityDisplay()
                updateFirestoreQuantity()
            } else {
                Toast.makeText(this, "數量不能小於 1", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateQuantityDisplay() {
        tvQuantity.text = quantity.toString()
    }

    private fun updateFirestoreQuantity() {
        db.collection("products").document(productId)
            .update("quantity", quantity)
            .addOnSuccessListener {
                Toast.makeText(this, "數量已更新", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "更新數量失敗: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRedeemButton() {
        val btnRedeem: Button = findViewById(R.id.btn_redeem)
        btnRedeem.setOnClickListener {
            showRedeemConfirmationDialog()
        }
    }

    private fun showRedeemConfirmationDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("兌換確認")
        builder.setMessage("確定兌換該商品？")

        builder.setPositiveButton("確認") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "兌換成功！", Toast.LENGTH_SHORT).show()
            // 這裡可以加入兌換成功後的 Firestore 更新邏輯
        }

        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.purple_500, theme))
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(R.color.black, theme))
    }
}
