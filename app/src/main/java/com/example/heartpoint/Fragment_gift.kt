package com.example.heartpoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.GiftAdapter
import com.example.heartpoint.models.Gift
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_gift : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GiftAdapter
    private val giftList: MutableList<Gift> = mutableListOf()// 初始化為空的 MutableList
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 實例

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加載 Fragment 的 XML 佈局
        return inflater.inflate(R.layout.fragment_gift_box, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.rv_gift) // 確保 rv_gift 存在於 XML
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 初始化 Adapter
        adapter = GiftAdapter(giftList)
        recyclerView.adapter = adapter

        // 設定篩選按鈕
        setupFilterButtons(view)

        // 加載資料
        fetchGiftsFromDatabase()
    }

    // 設定篩選按鈕的點擊事件
    private fun setupFilterButtons(view: View) {
        val btnType: Button = view.findViewById(R.id.btn_type)
        val btnPoints: Button = view.findViewById(R.id.btn_points)
        val btnTime: Button = view.findViewById(R.id.btn_time)

        btnType.setOnClickListener {
            // 根據類型篩選資料
            filterGiftsByType()
        }

        btnPoints.setOnClickListener {
            // 根據點數篩選資料
            filterGiftsByPoints()
        }

        btnTime.setOnClickListener {
            // 根據時間篩選資料
            filterGiftsByTime()
        }
    }

    // 根據類型篩選資料
    private fun filterGiftsByType() {
        val filteredList = giftList.filter { it.category == "飲料" }
        adapter.updateData(filteredList)
    }


    // 根據點數篩選資料
    private fun filterGiftsByPoints() {
        val filteredList = giftList.sortedByDescending { it.love_count } // 按點數排序
        adapter.updateData(filteredList) // 更新 RecyclerView
    }

    // 根據時間篩選資料
    private fun filterGiftsByTime() {
        val filteredList = giftList.sortedBy { it.date } // 按時間排序
        adapter.updateData(filteredList) // 更新 RecyclerView
    }

    // 從 Firebase Firestore 獲取數據
    private fun fetchGiftsFromDatabase() {
        db.collection("gift_box") // 資料庫中的集合名稱為 "gift_box"
            .get()
            .addOnSuccessListener { documents ->
                giftList.clear() // 清空舊資料，避免重複
                for (document in documents) {
                    val title = document.getString("title") ?: "未知標題"
                    val category = document.getString("category") ?: "未知類別"
                    val date = document.getString("date") ?: "未知日期"
                    val image = document.getString("image") ?: "未知圖片"
                    val love_count = document.getLong("love_count")?.toInt() ?: 0
                    val love_image = document.getString("love_image") ?: "未知愛心圖片"

                    giftList.add(Gift(title, category, date, image, love_count, love_image))
                }
                adapter.updateData(giftList) // 更新完整資料到 RecyclerView
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}
