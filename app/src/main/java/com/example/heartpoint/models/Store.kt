package com.example.heartpoint.models

data class Store(
    val title: String,        // 商店標題
    val category: String,     // 商店類別
    val date: String,         // 時間或有效期限
    val image: String,        // 商店圖片 URL
    val love_count: Int,      // 愛心數量（數值類型）
    val love_image: String    // 愛心圖片 URL
)

