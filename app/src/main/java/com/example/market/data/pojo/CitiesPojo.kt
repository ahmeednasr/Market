package com.example.market.data.pojo

data class CitiesPojo(
	val msg: String? = null,
	val data: List<String?>? = null,
	val error: Boolean? = null
)
data class CitiesRequest(
	val country: String,
	val state: String
)
