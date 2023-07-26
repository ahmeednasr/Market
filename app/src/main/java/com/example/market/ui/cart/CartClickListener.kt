package com.example.market.ui.cart

import com.example.market.data.pojo.LineItemsItem

interface CartClickListener {
    fun addProduct(lineItemsItem: LineItemsItem, max: Int, current: Int)
    fun deleteProduct(lineItemsItem: LineItemsItem)
    fun removeCartItem(lineItemsItem: LineItemsItem)
}