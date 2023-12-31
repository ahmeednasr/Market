package com.example.market.ui.search

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.*
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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

    private val coroutineExceptionHandler= CoroutineExceptionHandler { _, throwable ->
        _products.postValue(NetworkResult.Error("error"))
        Log.e("TAG", ": "+throwable.message)
    }

    fun getProducts() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch(coroutineExceptionHandler) {
            val productsResponse = repository.getProducts()
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    allProducts = it.products as ArrayList<Product>
                    getFavourites(allProducts)
                    _products.postValue(NetworkResult.Success(allProducts))
                }
            } else {
                _products.postValue(NetworkResult.Error("error"))
            }
        }
    }

    private suspend fun getFavourites(products: List<Product>) {
        if (sharedPreferences.getBoolean(Constants.IS_Logged, false)){
            val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")?.toLong()
            val draftResponse = repository.getFavourites(
                favouritesId ?: 0L
            )
            if (draftResponse.isSuccessful) {
                draftResponse.body()?.let {
                    _favourites = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
                    for (j in 1 until it.draftOrder.lineItems.size) {
                        for (i in products.indices) {
                            if (products[i].id!! == it.draftOrder.lineItems[j].sku?.toLong()) {
                                Log.d("getFavourites", "getFavourites: ${products[i].title!!}")
                                products[i].isFavourite = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun addFavourite(product: Product) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")?.toLong()
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
        viewModelScope.launch(coroutineExceptionHandler) {
            val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")?.toLong()
            _favourites =
                _favourites.filter { !it.title.equals(product.title) } as ArrayList<LineItemsItem>
            repository.modifyFavourites(
                favouritesId ?: 0,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
        }
    }

    fun filterProductsByTittle(text: String) {
        _pro ducts.value = NetworkResult.Loading()
        viewModelScope.launch (coroutineExceptionHandler){
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
        viewModelScope.launch (coroutineExceptionHandler){
            _filterProducts =
                allProducts.filter { product -> product.variants!![0].price!!.toFloat() in lowerBound..upperBound } as ArrayList<Product>
            _products.postValue(NetworkResult.Success(_filterProducts))
        }
    }

    private fun roundToOneDecimalPlace(value: Float): Float {
        return BigDecimal(value.toDouble()).setScale(1, RoundingMode.HALF_EVEN).toFloat()
    }
}