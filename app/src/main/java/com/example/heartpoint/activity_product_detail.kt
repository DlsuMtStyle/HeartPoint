package com.example.productdetail

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.heartpoint.R

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvNote: TextView
    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var tvQuantity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_product_detail)

        // 初始化 UI 元件
        initializeUI()

        // 設定數量控制邏輯
        setupQuantityButtons()
    }

    private fun initializeUI() {
        tvTitle = findViewById(R.id.tv_title)
        tvDescription = findViewById(R.id.tv_description)
        tvNote = findViewById(R.id.tv_note)
        btnIncrease = findViewById(R.id.btn_increase)
        btnDecrease = findViewById(R.id.btn_decrease)
        tvQuantity = findViewById(R.id.tv_quantity)

        // 設定初始顯示值
        tvTitle.text = "7-11 大冰美限時兌換"
        tvDescription.text = "兌換時間：2024-12-01 ~ 2025-02-01\n兌換地點：7-Eleven"
        tvNote.text = "注意事項：\n1. 兌換後兌換碼不得退還。"
        tvQuantity.text = "1"  // 設定預設數量為 1
    }

    private fun setupQuantityButtons() {
        // 增加數量按鈕
        btnIncrease.setOnClickListener {
            val quantity = tvQuantity.text.toString().toInt()
            tvQuantity.text = (quantity + 1).toString()
        }

        // 減少數量按鈕
        btnDecrease.setOnClickListener {
            val quantity = tvQuantity.text.toString().toInt()
            if (quantity > 1) {  // 防止數量小於1
                tvQuantity.text = (quantity - 1).toString()
            } else {
                Toast.makeText(this, "數量不能小於 1", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
