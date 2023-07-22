package com.example.market.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.Product
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.math.BigDecimal
import java.math.RoundingMode

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    var allProducts = ArrayList<Product>()
    private var _filterProducts = ArrayList<Product>()

    private val _products: MutableLiveData<NetworkResult<List<Product>>> = MutableLiveData()
    val products: LiveData<NetworkResult<List<Product>>> = _products

    fun getProducts() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
            val productsResponse = repository.getProducts()
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    allProducts = it.products as ArrayList<Product>
                    _products.postValue(NetworkResult.Success(it.products))
                }
            } else {
                _products.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun filterProductsByTittle(text: String) {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
            _filterProducts = allProducts.filter { it.title?.toLowerCase()?.contains(text, true) ?: false } as ArrayList<Product>
            _products.postValue(NetworkResult.Success(_filterProducts))
        }
    }

    fun filterProductsByPrice(value: Float) {
        _products.value = NetworkResult.Loading()
        val lowerBound = roundToOneDecimalPlace(value) - 5.00
        val upperBound = roundToOneDecimalPlace(value) + 5.00
        viewModelScope.launch {
            _filterProducts = allProducts.filter { product -> product.variants!![0].price!!.toFloat() in lowerBound..upperBound } as ArrayList<Product>
            _products.postValue(NetworkResult.Success(_filterProducts))
        }
    }

    private fun roundToOneDecimalPlace(value: Float): Float {
        return BigDecimal(value.toDouble()).setScale(1, RoundingMode.HALF_EVEN).toFloat()
    }
}