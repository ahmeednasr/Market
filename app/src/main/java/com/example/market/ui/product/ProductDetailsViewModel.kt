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
import com.example.market.utils.Constants.UserID
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var _favourites = ArrayList<LineItemsItem>()
    private var _cart = ArrayList<LineItemsItem>()

    private val _product: MutableLiveData<NetworkResult<ProductResponse>> = MutableLiveData()
    val product: LiveData<NetworkResult<ProductResponse>> = _product
    private val _conversionResult: MutableLiveData<Double> = MutableLiveData()
    val conversionResult: LiveData<Double> = _conversionResult

    fun getProduct(productId: Long) {
        _product.value = NetworkResult.Loading()
        viewModelScope.launch {
            val productsResponse = repository.getSingleProduct(productId)
            if (productsResponse.isSuccessful) {
                val currencyTo = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "")
                convertCurrency("EGP", currencyTo!!, 1.0)
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

    fun saveToCart(product: Product, variantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val cartId = sharedPreferences.getString(CART_ID, "0")!!.toLong()
            Log.i("CART", cartId.toString())
            val draftResponse = repository.getCart(
                cartId
            )
            if (draftResponse.isSuccessful) {
                draftResponse.body()?.let { it ->
                    _cart = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
                    Log.i("CART", _cart.toString())
                    val exist = _cart.filter {
                        it.variantId == variantId && it.productId == product.id
                    }.isNotEmpty()
                    if (!exist) {
                        val savedItem = LineItemsItem(
                            title = product.title,
                            price = product.variants?.get(0)?.price,
                            quantity = 1,
                            sku = product.id.toString(),
                            variantId = variantId,
                            properties = listOf(
                                Property("productImage", product.image?.src),
                                Property(
                                    "maxQuantity",
                                    "${product.variants?.get(0)?.inventory_quantity}"
                                )
                            )
                        )
                        _cart.add(savedItem)
                        Log.i("CART", _cart.toString())
                        repository.modifyCart(
                            cartId ?: 0,
                            DraftOrderResponse(DraftOrder(lineItems = _cart))
                        )
                    }
                }
            }
        }
    }

    fun convertCurrency(from: String, to: String, q: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.convertCurrency(from, to, q)
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    _conversionResult.postValue(it)
                }
            }
        }
    }
}
