package com.example.market.data.pojo

data class Currencies(
    val currencies: List<Currency>
)
data class Currency(
    val currency: String,
    val enabled: Boolean,
    val rate_updated_at: String
)