package com.example.heartpoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.EventAdapter
import com.example.heartpoint.models.Event
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventsList = mutableListOf<Event>()
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 實例

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加載 Fragment 的 XML 佈局
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.rv_events)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 初始化 Adapter
        adapter = EventAdapter(eventsList)
        recyclerView.adapter = adapter

        // 加載資料
        fetchEventsFromDatabase()
    }

    // 從 Firebase Firestore 獲取數據
    private fun fetchEventsFromDatabase() {
        db.collection("events") // 假設資料庫中的集合名稱為 "events"
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: "未知活動"
                    val location = document.getString("location") ?: "未知地點"
                    val date = document.getString("date") ?: "未知日期"
                    val image = document.getString("image") ?: "未知圖片"
                    eventsList.add(Event(title, location, date, image))
                }
                adapter.notifyDataSetChanged() // 通知 RecyclerView 更新
            }
            .addOnFailureListener { exception ->
                // 這裡處理數據加載錯誤
                exception.printStackTrace()
            }
    }
}
