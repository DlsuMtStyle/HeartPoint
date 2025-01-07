package com.example.heartpoint

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.adapters.EventAdapter
import com.example.heartpoint.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventsList = mutableListOf<Event>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化視圖
        searchEditText = view.findViewById(R.id.et_search)
        recyclerView = view.findViewById(R.id.rv_search_results)
        val editText = view.findViewById<EditText>(R.id.et_search)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search)
        drawable?.setBounds(0, 0, 48, 48) // 設置圖片大小（寬高 48px）
        editText.setCompoundDrawables(drawable, null, null, null) // 左邊添加圖片
        val searchEditText = view.findViewById<EditText>(R.id.et_search)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_search_results)

        // 設置 RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EventAdapter(eventsList) { event ->
            // 點擊搜索結果的處理邏輯
            val intent = Intent(requireContext(), EventDetailsActivity::class.java)
            intent.putExtra("eventId", event.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // 搜索框文字改變監聽
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchEvents(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchEvents(query) // 執行查詢邏輯
                } else {
                    Toast.makeText(requireContext(), "請輸入關鍵字", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    // 搜索活動
    private fun searchEvents(keyword: String) {
        if (keyword.isEmpty()) {
            eventsList.clear()
            adapter.notifyDataSetChanged()
            return
        }

        db.collection("events")
            .whereGreaterThanOrEqualTo("title", keyword)
            .whereLessThanOrEqualTo("title", keyword + "\uf8ff")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                eventsList.clear()
                for (document in result) {
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
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun updateRecyclerView(events: List<Event>) {
        val adapter = EventAdapter(events) { event ->
            // 點擊事件處理
            val intent = Intent(requireContext(), EventDetailsActivity::class.java)
            intent.putExtra("eventId", event.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}


