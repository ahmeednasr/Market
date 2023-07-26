package com.example.market.ui.cart

import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product

interface CartClickListener {
    fun addProduct(product: Product, variantId: Long)
    fun deleteProduct(product: Product, variantId: Long)
    fun removeCartItem(lineItemsItem: LineItemsItem)
}