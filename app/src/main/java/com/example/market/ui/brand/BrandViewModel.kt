package com.example.market.ui.brand

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.Product
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _products: MutableLiveData<NetworkResult<ProductResponse>> = MutableLiveData()
    val products: LiveData<NetworkResult<ProductResponse>> = _products

    private val coroutineExceptionHandler= CoroutineExceptionHandler { _, throwable ->
        _products.postValue(NetworkResult.Error("error"))
        Log.e("TAG", ": "+throwable.message)
    }

    fun getProducts(vendor: String) {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch(coroutineExceptionHandler) {
            val productsResponse = repository.getBrandProducts(vendor)
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    _products.postValue(NetworkResult.Success(it))
                }
            } else {
                _products.postValue(NetworkResult.Error("error"))
            }
        }
    }
}