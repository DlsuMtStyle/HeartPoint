package com.example.heartpoint.models

data class Event(
    val title: String,   // 活動標題
    val location: String, // 活動地點
    val date: String,     // 活動日期
    val image: String,  // 活動照片
    val category: String,  //活動類型
    val point: Long  // 可得點數
)

