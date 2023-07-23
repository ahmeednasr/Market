package com.example.market.ui.search

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.DraftOrder
import com.example.market.data.pojo.DraftOrderResponse
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.math.BigDecimal
import java.math.RoundingMode

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    var allProducts = ArrayList<Product>()
    private var _filterProducts = ArrayList<Product>()
    private var _favourites = ArrayList<LineItemsItem>()

    private val _products: MutableLiveData<NetworkResult<List<Product>>> = MutableLiveData()
    val products: LiveData<NetworkResult<List<Product>>> = _products

    private val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()

    fun getProducts() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
            val productsResponse = repository.getProducts()
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    allProducts = it.products as ArrayList<Product>
                    getFavourites(allProducts)
                    _products.postValue(NetworkResult.Success(it.products))
                }
            } else {
                _products.postValue(NetworkResult.Error("error"))
            }
        }
    }

    private suspend fun getFavourites(products: List<Product>) {
        val draftResponse = repository.getFavourites(
            sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()
        )
        if (draftResponse.isSuccessful) {
            draftResponse.body()?.let {
                _favourites = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
                for (j in 1 until it.draftOrder.lineItems.size) {
                    for (i in products.indices) {
                        if (products[i].title.equals(it.draftOrder.lineItems[j].title))
                            products[i].isFavourite = true
                    }
                }
            }
        }
    }

    fun addFavourite(product: Product) {
        viewModelScope.launch {
            Log.d("addFavourite", "product id = ${product.id}")
            val fav = LineItemsItem(
                title = product.title,
                price = product.variants?.get(0)?.price,
                productId = product.id,
                quantity = 1,
                sku = product.id.toString()
            )
            _favourites.add(fav)
            repository.modifyFavourites(
                favouritesId?:0,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
        }
    }

    fun deleteFavourite(product: Product) {
        viewModelScope.launch {
            _favourites =
                _favourites.filter { !it.title.equals(product.title)} as ArrayList<LineItemsItem>
            repository.modifyFavourites(
                favouritesId?:0,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
        }
    }

    fun filterProductsByTittle(text: String) {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
            _filterProducts = allProducts.filter {
                it.title?.toLowerCase()?.contains(text, true) ?: false
            } as ArrayList<Product>
            _products.postValue(NetworkResult.Success(_filterProducts))
        }
    }

    fun filterProductsByPrice(value: Float) {
        _products.value = NetworkResult.Loading()
        val lowerBound = roundToOneDecimalPlace(value) - 5.00
        val upperBound = roundToOneDecimalPlace(value) + 5.00
        viewModelScope.launch {
            _filterProducts =
                allProducts.filter { product -> product.variants!![0].price!!.toFloat() in lowerBound..upperBound } as ArrayList<Product>
            _products.postValue(NetworkResult.Success(_filterProducts))
        }
    }

    private fun roundToOneDecimalPlace(value: Float): Float {
        return BigDecimal(value.toDouble()).setScale(1, RoundingMode.HALF_EVEN).toFloat()
    }
}