package com.example.heartpoint

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.heartpoint.adapters.GiftAdapter
import com.example.heartpoint.models.Gift

class GiftActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val giftList = mutableListOf<Gift>()
    private lateinit var adapter: GiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift)

        // 初始化 RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.rv_gift)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GiftAdapter(giftList)
        recyclerView.adapter = adapter

        // 讀取禮物資料
        fetchGifts()

        val btnBack: ImageView = findViewById(R.id.iv_back)
        btnBack.setOnClickListener {
            finish() // 返回到上一頁
        }
    }

    private fun fetchGifts() {
        if (currentUserId != null) {
            db.collection("users")
                .document(currentUserId)
                .collection("redeemed_gifts")
                .get()
                .addOnSuccessListener { result ->
                    giftList.clear()
                    for (document in result) {
                        val gift = document.toObject(Gift::class.java).copy(id = document.id)
                        giftList.add(gift)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "載入禮物失敗：${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "無法獲取用戶資訊", Toast.LENGTH_SHORT).show()
        }
    }
}
