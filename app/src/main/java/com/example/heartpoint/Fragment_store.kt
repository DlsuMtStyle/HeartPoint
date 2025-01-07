package com.example.heartpoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        // 設定篩選按鈕
        setupFilterButtons(view)

        // 加載資料
        fetchStoresFromDatabase()
    }

    // 設定篩選按鈕的點擊事件
    private fun setupFilterButtons(view: View) {
        val btnType: Button = view.findViewById(R.id.btn_type)
        val btnPoints: Button = view.findViewById(R.id.btn_points)
        val btnTime: Button = view.findViewById(R.id.btn_time)

        btnType.setOnClickListener {
            // 根據類型篩選資料
            filterStoresByType()
        }

        btnPoints.setOnClickListener {
            // 根據點數篩選資料
            filterStoresByPoints()
        }

        btnTime.setOnClickListener {
            // 根據時間篩選資料
            filterStoresByTime()
        }
    }

    // 根據類型篩選商店資料
    private fun filterStoresByType() {
        val filteredList = storeList.filter { it.category == "飲料" } // 篩選類型為 "飲料"
        adapter.updateData(filteredList)
    }

    // 根據點數篩選商店資料
    private fun filterStoresByPoints() {
        val filteredList = storeList.sortedByDescending { it.love_count } // 按點數排序
        adapter.updateData(filteredList)
    }

    // 根據時間篩選商店資料
    private fun filterStoresByTime() {
        val filteredList = storeList.sortedBy { it.date } // 按時間排序
        adapter.updateData(filteredList)
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
                    val love_count = document.getLong("love_count")?.toInt() ?: 0
                    val love_image = document.getString("love_image") ?: "未知愛心圖片"

                    storeList.add(Store(title, category, date, image, love_count, love_image))
                }
                adapter.updateData(storeList) // 加載完整資料
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}

