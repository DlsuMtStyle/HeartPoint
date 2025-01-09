package com.example.heartpoint.models

data class product(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val startdate: String = "",
    val enddate: String = "",
    val image: String = "",
    val point: Long = 0,
    val location: String = "",
    val quantity: Int = 0
) {
    // 無參數構造函數（默認的 Kotlin data class 已支持）
    constructor() : this("", "", "", "", "", "", 0, "", 0)
}

