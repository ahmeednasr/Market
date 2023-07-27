package com.example.market.ui.cart

import com.example.market.data.pojo.LineItemsItem

interface CartClickListener {
    fun addProduct(lineItemsItem: LineItemsItem, max: Int, current: Int, currentPrice: Double)
    fun deleteProduct(lineItemsItem: LineItemsItem, currentPrice: Double)
    fun removeCartItem(lineItemsItem: LineItemsItem)
}