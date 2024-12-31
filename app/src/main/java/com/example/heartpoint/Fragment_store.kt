package com.example.heartpoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.StoreAdapter
import com.example.heartpoint.models.Store
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_store : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoreAdapter
    private val storeList = mutableListOf<Store>()
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 實例

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加載 Fragment 的 XML 佈局
        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.rv_store)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 初始化 Adapter
        adapter = StoreAdapter(storeList)
        recyclerView.adapter = adapter

        // 加載資料
        fetchStoresFromDatabase()
    }

    // 從 Firebase Firestore 獲取數據
    private fun fetchStoresFromDatabase() {
        db.collection("store") // 資料庫中的集合名稱為 "store"
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: "未知標題"
                    val category = document.getString("category") ?: "未知類別"
                    val date = document.getString("date") ?: "未知日期"
                    val image = document.getString("image") ?: "未知圖片"
                    val loveCount = document.getString("love_count") ?: "❤️ 0"

                    storeList.add(Store(title, category, date, image, loveCount))
                }
                adapter.notifyDataSetChanged() // 通知 RecyclerView 更新
            }
            .addOnFailureListener { exception ->
                // 這裡處理數據加載錯誤
                exception.printStackTrace()
            }
    }
}
