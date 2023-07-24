package com.example.market.ui.product

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.*
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants.CART_ID
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var _favourites = ArrayList<LineItemsItem>()
    private val _product: MutableLiveData<NetworkResult<ProductResponse>> = MutableLiveData()
    val product: LiveData<NetworkResult<ProductResponse>> = _product

    fun getProduct(productId: Long) {
        _product.value = NetworkResult.Loading()
        viewModelScope.launch {
            val productsResponse = repository.getSingleProduct(productId)
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    it.product?.let { product ->
                        getFavourites(product)
                    }
                    _product.postValue(NetworkResult.Success(it))
                }
            } else {
                _product.postValue(NetworkResult.Error("error"))
            }
        }
    }


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


    private suspend fun getFavourites(product: Product) {
        val draftResponse = repository.getFavourites(
            sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()
        )
        if (draftResponse.isSuccessful) {
            draftResponse.body()?.let {
                _favourites = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
                for (favorite in _favourites) {
                    if (product.id!! == favorite.sku?.toLong()) {
                        product.isFavourite = true
                    }
                }
            }
        }
    }

    fun addFavourite(product: Product) {
        val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()
        viewModelScope.launch {
            Log.d("addFavourite", "product id = ${product.id}")
            val fav = LineItemsItem(
                title = product.title,
                price = product.variants?.get(0)?.price,
                quantity = 1,
                sku = product.id.toString(),
                properties = listOf(Property("productImage", product.image?.src))
            )
            _favourites.add(fav)
            repository.modifyFavourites(
                favouritesId ?: 0,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
        }
    }

    fun deleteFavourite(product: Product) {
        val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()
        viewModelScope.launch {
            _favourites =
                _favourites.filter { !it.title.equals(product.title) } as ArrayList<LineItemsItem>
            repository.modifyFavourites(
                favouritesId ?: 0,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
        }
    }

}
