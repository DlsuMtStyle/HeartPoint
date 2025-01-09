package com.example.heartpoint

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.RedeemRecordAdapter
import com.example.heartpoint.models.RedeemRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GiftRedeemRecordActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val redeemRecordList = mutableListOf<RedeemRecord>()
    private lateinit var adapter: RedeemRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_redeem_record)

        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener { finish() }

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.rv_redeem_record)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RedeemRecordAdapter(redeemRecordList)
        recyclerView.adapter = adapter

        // Fetch redeem records
        fetchRedeemRecords()
    }

    private fun fetchRedeemRecords() {
        if (currentUserId != null) {
            db.collection("users")
                .document(currentUserId!!)
                .collection("redeem_records")
                .get()
                .addOnSuccessListener { result ->
                    redeemRecordList.clear()
                    for (document in result) {
                        val record = document.toObject(RedeemRecord::class.java)
                        redeemRecordList.add(record)
                    }
                    // Notify adapter about data change
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "載入兌換記錄失敗：${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "尚未登入，無法載入兌換記錄", Toast.LENGTH_SHORT).show()
        }
    }
}
