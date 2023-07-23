package com.example.market.ui.product

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var allProducts = ArrayList<Product>()
    private var _favourites = ArrayList<LineItemsItem>()

    private val _products: MutableLiveData<NetworkResult<List<Product>>> = MutableLiveData()
    val products: LiveData<NetworkResult<List<Product>>> = _products

    private val _product: MutableLiveData<NetworkResult<ProductResponse>> = MutableLiveData()
    val product: LiveData<NetworkResult<ProductResponse>> = _product

    fun getProduct(productId: Long){
        _product.value = NetworkResult.Loading()
        viewModelScope.launch {
            val productsResponse = repository.getSingleProduct(productId)
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    _product.postValue(NetworkResult.Success(it))
                }
            } else {
                _product.postValue(NetworkResult.Error("error"))
            }
        }
    }


    fun setInCart(productDetails: ProductDetails){
        viewModelScope.launch {
            //repository.addInCart()
        }
    }

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
                        if (products[i].id!! == it.draftOrder.lineItems[j].sku?.toLong()) {
                            Log.d("getFavourites", "getFavourites: ${products[i].title!!}")
                            products[i].isFavourite = true
                        }
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
                favouritesId?:0,
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
                favouritesId?:0,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
        }
    }

}
