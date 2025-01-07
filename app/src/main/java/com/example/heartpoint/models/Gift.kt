package com.example.heartpoint.models

data class Gift(
    val title: String,        // 禮物名稱
    val category: String,     // 禮物類別
    val date: String,         // 時間
    val image: String,        // 禮物圖片 URL
    val love_count: Int,      // 點數或愛心數量
    val love_image: String    // 愛心圖片 URL
)


