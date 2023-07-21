package com.example.market.data.pojo

data class GovernmentPojo(
    val msg: String? = null,
    val data: Data? = null,
    val error: Boolean? = null
)

data class Data(
    val name: String? = null,
    val iso2: String? = null,
    val iso3: String? = null,
    val states: List<StatesItem?>? = null
)

data class StatesItem(
    val name: String? = null,
    val stateCode: String? = null
)

data class Country(
    val country: String
)

