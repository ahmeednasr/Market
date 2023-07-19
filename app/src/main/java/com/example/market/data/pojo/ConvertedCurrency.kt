package com.example.market.data.pojo

data class ConvertedCurrency(
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)

data class Query(
    val amount: Int,
    val from: String,
    val to: String
)

data class Info(
    val quote: Double,
    val timestamp: Int
)