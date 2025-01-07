package com.example.heartpoint

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.EventAdapter
import com.example.heartpoint.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventsList = mutableListOf<Event>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.rv_events)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = EventAdapter(eventsList) { event ->
            // 點擊事件的處理邏輯
            val intent = Intent(requireContext(), EventDetailsActivity::class.java)
            intent.putExtra("eventId", event.id) // 傳遞活動的 ID
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // 加載所有資料
        fetchEventsFromDatabase()

        val spinnerType: Spinner = view.findViewById(R.id.spinner_type)
        val spinnerRegion: Spinner = view.findViewById(R.id.spinner_region)
        val imgFilter: ImageView = view.findViewById(R.id.img_filter)

        // 初始化類型下拉選單
        val typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("所有類型", "教學", "環境", "社區服務", "慈善活動")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        // 初始化地區下拉選單
        val regionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("所有地區", "桃園市中壢區", "臺北市大安區", "臺北市信義區", "臺中市沙屯區")
        )
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRegion.adapter = regionAdapter

        // 設置下拉選單的 Listener
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = parent?.getItemAtPosition(position).toString()
                performFilter(selectedType, spinnerRegion.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                performFilter("所有類型", spinnerRegion.selectedItem.toString())
            }
        }

        spinnerRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRegion = parent?.getItemAtPosition(position).toString()
                performFilter(spinnerType.selectedItem.toString(), selectedRegion)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                performFilter(spinnerType.selectedItem.toString(), "所有地區")
            }
        }

        imgFilter.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), imgFilter)
            popupMenu.menuInflater.inflate(R.menu.menu_filter, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.filter_point_desc -> performSort("point", false) // 點數大到小
                    R.id.filter_point_asc -> performSort("point", true)  // 點數小到大
                    R.id.filter_date_desc -> performSort("date", true)  // 時間近到遠
                    R.id.filter_date_asc -> performSort("date", false)   // 時間遠到近
                }
                true
            }
            popupMenu.show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchEventsFromDatabase() {
        db.collection("events") // 假設資料庫中的集合名稱為 "events"
            .get()
            .addOnSuccessListener { documents ->
                eventsList.clear()
                for (document in documents) {
                    val id = document.id
                    val title = document.getString("title") ?: "未知活動"
                    val location = document.getString("location") ?: "未知地點"
                    val locationDetail = document.getString("location_detail") ?: "未知詳細地址"
                    val date = document.getString("date") ?: "未知日期"
                    val time = document.getString("time") ?: "未知時間"
                    val applyDeadline = document.getString("apply_deadline") ?: "未知截止日期"
                    val point = document.getLong("point") ?: 0
                    val image = document.getString("image") ?: ""
                    val category = document.getString("category") ?: "未知類型"

                    // 更新 Event 的初始化
                    eventsList.add(
                        Event(
                            id = id,
                            title = title,
                            location = location,
                            location_detail = locationDetail,
                            date = date,
                            time = time,
                            apply_deadline = applyDeadline,
                            point = point,
                            image = image,
                            category = category
                        )
                    )
                }
                adapter.notifyDataSetChanged() // 通知 RecyclerView 更新
            }
            .addOnFailureListener { exception ->
                // 處理錯誤
                exception.printStackTrace()
            }
    }

    private fun performFilter(selectedType: String, selectedLocation: String) {
        var query: Query = db.collection("events")
        // 指向 events 集合

        // 根據類型篩選
        if (selectedType != "所有類型") {
            query = query.whereEqualTo("category", selectedType)
        }

        if (selectedLocation != "所有地區") {
            query = query.whereEqualTo("location", selectedLocation)
        }


        // 執行查詢
        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    updateRecyclerView(emptyList()) // 傳入空列表
                } else {
                    val filteredEvents = querySnapshot.documents.map { document ->
                        Event(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            location = document.getString("location") ?: "",
                            date = document.getString("date") ?: "",
                            category = document.getString("category") ?: "",
                            image = document.getString("image") ?: "",
                            point = document.getLong("point") ?: 0L,
                            location_detail = document.getString("location_detail") ?: "",
                            time = document.getString("time") ?: "",
                            apply_deadline = document.getString("apply_deadline") ?: ""
                        )
                    }
                    updateRecyclerView(filteredEvents)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace() // 錯誤處理
            }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(events: List<Event>) {
        eventsList.clear()
        eventsList.addAll(events)
        adapter.notifyDataSetChanged()
        if (events.isEmpty()) {
            Toast.makeText(requireContext(), "無篩選結果", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSort(field: String, ascending: Boolean) {
        var query: Query = db.collection("events")

        // 根據排序順序設定查詢
        query = if (ascending) {
            query.orderBy(field, Query.Direction.ASCENDING)
        } else {
            query.orderBy(field, Query.Direction.DESCENDING)
        }

        // 執行查詢
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val sortedEvents = querySnapshot.documents.map { document ->
                    Event(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        location = document.getString("location") ?: "",
                        date = document.getString("date") ?: "",
                        category = document.getString("category") ?: "",
                        image = document.getString("image") ?: "",
                        point = document.getLong("point") ?: 0L,
                        location_detail = document.getString("location_detail") ?: "",
                        time = document.getString("time") ?: "",
                        apply_deadline = document.getString("apply_deadline") ?: ""
                    )
                }
                // 更新 RecyclerView
                updateRecyclerView(sortedEvents)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace() // 錯誤處理
            }
    }


}