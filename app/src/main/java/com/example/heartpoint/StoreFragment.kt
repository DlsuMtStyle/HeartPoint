package com.example.heartpoint

import StoreAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartpoint.models.product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StoreFragment : Fragment() {

    private lateinit var spinnerType: Spinner
    private lateinit var btnPoints: Button
    private lateinit var btnTime: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoreAdapter
    private val db = FirebaseFirestore.getInstance()
    private val productList = mutableListOf<product>()

    private var isPointDescending = true
    private var isTimeDescending = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerType = view.findViewById(R.id.spinner_type)
        btnPoints = view.findViewById(R.id.btn_points)
        btnTime = view.findViewById(R.id.btn_time)
        recyclerView = view.findViewById(R.id.rv_store)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StoreAdapter(productList) { product ->
            // 點擊事件處理
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("productId", product.id) // 只要把 productId 放進 intent 就好
            intent.putExtra("title", product.title)
            intent.putExtra("category", product.category)
            intent.putExtra("startdate", product.startdate)
            intent.putExtra("enddate", product.enddate)
            intent.putExtra("image", product.image)
            intent.putExtra("point", product.point)
            intent.putExtra("location", product.location)
            intent.putExtra("quantity", product.quantity)
            startActivity(intent)

        }

        recyclerView.adapter = adapter

        setupSpinner()
        fetchAllStores()

        btnPoints.setOnClickListener {
            fetchStoresSortedByPoints(isPointDescending)
            isPointDescending = !isPointDescending
        }
        btnTime.setOnClickListener {
            fetchStoresSortedByTime(isTimeDescending)
            isTimeDescending = !isTimeDescending
        }
    }

    private fun setupSpinner() {
        val categories = listOf("所有類型", "優惠券", "飲料")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = spinnerAdapter

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                filterStoresByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterStoresByCategory(category: String) {
        val query = if (category == "所有類型") {
            db.collection("products")
        } else {
            db.collection("products").whereEqualTo("category", category)
        }

        query.get()
            .addOnSuccessListener { result ->
                val filteredList = mutableListOf<product>()
                for (document in result) {
                    val store = document.toObject(product::class.java)
                    filteredList.add(store)
                }
                adapter.updateData(filteredList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "篩選失敗，請稍後再試！", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAllStores() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val store = document.toObject(product::class.java)
                    productList.add(store)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "獲取產品失敗，請稍後再試！", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchStoresSortedByPoints(descending: Boolean) {
        val query = if (descending) {
            db.collection("products").orderBy("point", Query.Direction.DESCENDING)
        } else {
            db.collection("products").orderBy("point", Query.Direction.ASCENDING)
        }

        query.get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val store = document.toObject(product::class.java)
                    productList.add(store)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "無法獲取排序後的產品，請稍後再試！", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchStoresSortedByTime(descending: Boolean) {
        val query = if (descending) {
            db.collection("products").orderBy("enddate", Query.Direction.DESCENDING)
        } else {
            db.collection("products").orderBy("enddate", Query.Direction.ASCENDING)
        }

        query.get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val store = document.toObject(product::class.java)
                    productList.add(store)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "無法獲取排序後的產品，請稍後再試！", Toast.LENGTH_SHORT).show()
            }
    }
}
