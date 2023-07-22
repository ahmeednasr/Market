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

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private var _allProducts = ArrayList<Product>()

    private val _products: MutableLiveData<NetworkResult<List<Product>>> = MutableLiveData()
    val products: LiveData<NetworkResult<List<Product>>> = _products

    fun getProducts() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
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
}