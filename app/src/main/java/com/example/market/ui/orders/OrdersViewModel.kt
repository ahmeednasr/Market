package com.example.market.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.market.data.pojo.OrderResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _orders: MutableLiveData<NetworkResult<OrderResponse>> = MutableLiveData()
    val orders: LiveData<NetworkResult<OrderResponse>> = _orders

    fun getOrders() {
        _orders.value = NetworkResult.Loading()
//        viewModelScope.launch {
//            val brandsResponse = repository
//            if (brandsResponse.isSuccessful) {
//                brandsResponse.body()?.let {
//                    _orders.postValue(NetworkResult.Success(it))
//                }
//            } else {
//                _orders.postValue(NetworkResult.Error("error"))
//            }
//        }
    }
}