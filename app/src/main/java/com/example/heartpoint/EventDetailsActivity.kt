package com.example.heartpoint

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.heartpoint.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var btnRegisterEvent: Button
    private val db = FirebaseFirestore.getInstance() // Firestore 實例
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        btnRegisterEvent = findViewById(R.id.btn_register_event)

        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // 返回到上一頁
        }

        val eventId = intent.getStringExtra("eventId")
        if (eventId != null) {
            fetchEventDetails(eventId)
        } else {
            Toast.makeText(this, "無法載入活動詳情", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 報名按鈕點擊事件
        btnRegisterEvent.setOnClickListener {
            if (currentUserId != null && eventId != null) { // 檢查 eventId 和 currentUserId 是否為非空
                checkRegistrationStatus(eventId, currentUserId)
            } else {
                Toast.makeText(this, "無法獲取活動資訊，請稍後再試", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkRegistrationStatus(eventId: String, userId: String) {
        db.collection("events")
            .document(eventId)
            .collection("registrations")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 已報名
                    Toast.makeText(this, "已報名過，請勿重複報名", Toast.LENGTH_SHORT).show()
                } else {
                    // 未報名，顯示確認視窗
                    showConfirmationDialog(eventId, userId)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "檢查報名狀態失敗，請稍後再試", Toast.LENGTH_SHORT).show()
            }
    }

    // 顯示確認報名視窗
    private fun showConfirmationDialog(eventId: String, userId: String) {
        AlertDialog.Builder(this)
            .setTitle("確認報名")
            .setMessage("是否確認報名此活動？")
            .setPositiveButton("確認") { _, _ ->
                registerForEvent(eventId, userId)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // 報名活動
    private fun registerForEvent(eventId: String, userId: String) {
        val registrationData = mapOf(
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("events")
            .document(eventId)
            .collection("registrations")
            .document(userId)
            .set(registrationData)
            .addOnSuccessListener {
                Toast.makeText(this, "已成功報名！", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "報名失敗，請稍後再試", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchEventDetails(eventId: String) {
        db.collection("events").document(eventId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val title = document.getString("title") ?: ""
                    val image = document.getString("image") ?: ""
                    val dateRange = document.getString("date") ?: "未知"
                    val locationName = document.getString("location_detail") ?: "未知"
                    val address = document.getString("location") ?: "未知"
                    val time = document.getString("time") ?: "未知"
                    val point = document.getLong("point")?.toInt() ?: 0 ?: "未知"

                    // 更新 UI
                    findViewById<TextView>(R.id.tv_event_title).text = title
                    findViewById<TextView>(R.id.tv_event_date_range).text = "報名期限：$dateRange"
                    findViewById<TextView>(R.id.tv_event_location_name).text = "活動地點：$locationName"
                    findViewById<TextView>(R.id.tv_event_address).text = "活動地址：$address"
                    findViewById<TextView>(R.id.tv_event_time).text = "活動時間：$time"
                    findViewById<TextView>(R.id.tv_event_point).text = point.toString()
                    val img_event_detail = findViewById<ImageView>(R.id.img_event_detail)

                    Glide.with(this)
                        .load(image) // 從 URL 加載圖片
                        .into(img_event_detail) // imgEventDetail 是你的 ImageView 的 id
                } else {
                    Toast.makeText(this, "無法找到活動詳情", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(this, "載入活動詳情失敗", Toast.LENGTH_SHORT).show()
            }
    }
}
