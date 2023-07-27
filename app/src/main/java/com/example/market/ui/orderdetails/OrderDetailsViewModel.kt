package com.example.market.ui.orderdetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.OrderResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _orders: MutableLiveData<NetworkResult<OrderResponse>> = MutableLiveData()
    val orders: LiveData<NetworkResult<OrderResponse>> = _orders

    private val coroutineExceptionHandler= CoroutineExceptionHandler { _, throwable ->
        _orders.postValue(NetworkResult.Error("error"))
        Log.e("TAG", ": "+throwable.message)
    }

    fun getOrder(orderId :Long) {
        _orders.value = NetworkResult.Loading()
        viewModelScope.launch(coroutineExceptionHandler) {
            val ordersResponse = repository.getSingleOrder(orderId)
            if (ordersResponse.isSuccessful) {
                ordersResponse.body()?.let {
                    _orders.postValue(NetworkResult.Success(it))
                }
            } else {
                _orders.postValue(NetworkResult.Error("error"))
            }
        }
    }
}