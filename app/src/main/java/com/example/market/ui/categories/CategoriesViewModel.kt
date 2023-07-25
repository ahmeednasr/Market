package com.example.market.ui.categories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.Product
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private var _allProducts = ArrayList<Product>()

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
                    _allProducts = it.products as ArrayList<Product>
                    _products.postValue(NetworkResult.Success(it.products))
                }
            } else {
                _products.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun filterProducts(mainCategory: String, subCategory: String) {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch(coroutineExceptionHandler) {
            var filter = _allProducts.filter { it.tags?.contains(mainCategory, true) ?: false }
            if (subCategory.isNotBlank()) {
                filter = filter.filter { it.product_type.equals(subCategory, true) }
            }
            _products.postValue(NetworkResult.Success(filter))
        }
    }
}
