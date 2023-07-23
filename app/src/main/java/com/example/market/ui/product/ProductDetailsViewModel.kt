package com.example.market.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.*
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants.CART_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    fun setInCart(product: Product, userID: Long) {
        viewModelScope.launch {

            val cart = DraftOrderResponse(
                DraftOrder(
                    lineItems = mutableListOf(
                        LineItemsItem(
                            title = product.title,
                            price = product.variants?.get(0)?.price,
                            variantId = product.variants?.get(0)?.id,
                            quantity = 1,
                            product = product,
                            productId = product.id,
                            properties = listOf(
                                Property(
                                    "productImage",
                                    product.images?.get(0)?.src
                                )
                            )
                        )
                    ), customer = Customer(id = userID), tags = CART_ID
                )
            )
            repository.createCartDraftOrder(cart)
        }
    }
}