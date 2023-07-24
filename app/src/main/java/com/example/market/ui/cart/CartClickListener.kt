package com.example.market.ui.cart

interface CartClickListener {
    fun addProduct()
    fun deleteProduct()
    fun removeCartItem(cartId: Long)
}