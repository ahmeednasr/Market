package com.example.market.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _products: MutableLiveData<NetworkResult<ProductResponse>> = MutableLiveData()
    val products: LiveData<NetworkResult<ProductResponse>> = _products

    fun getProducts() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
            val productsResponse = repository.getProducts()
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