package com.example.heartpoint

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.EventRecordAdapter
import com.example.heartpoint.models.EventRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventRecordActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private val eventRecordList = mutableListOf<EventRecord>()
    private lateinit var adapter: EventRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_record)

        val btnBack: ImageView = findViewById(R.id.btn_back)
        btnBack.setOnClickListener { finish() }

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.rv_event_record)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventRecordAdapter(eventRecordList)
        recyclerView.adapter = adapter

        // Fetch event records
        fetchEventRecords()
    }

    private fun fetchEventRecords() {
        if (currentUserId != null) {
            db.collection("users")
                .document(currentUserId!!)
                .collection("event_records")
                .get()
                .addOnSuccessListener { result ->
                    eventRecordList.clear()
                    for (document in result) {
                        val record = document.toObject(EventRecord::class.java)
                        eventRecordList.add(record)
                    }
                    // Notify adapter about data change
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "載入活動記錄失敗：${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "尚未登入，無法載入活動記錄", Toast.LENGTH_SHORT).show()
        }
    }
}
