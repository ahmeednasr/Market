package com.example.market.ui.account


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.Currencies
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.OrderResponse

import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    init {
        getCurrencies()
    }

    private var _favourites = ArrayList<LineItemsItem>()

    private val _products: MutableLiveData<NetworkResult<List<LineItemsItem>>> = MutableLiveData()
    val products: LiveData<NetworkResult<List<LineItemsItem>>> = _products

    private val _orders: MutableLiveData<NetworkResult<OrderResponse>> = MutableLiveData()
    val orders: LiveData<NetworkResult<OrderResponse>> = _orders


    private val _currencies: MutableLiveData<NetworkResult<Currencies>> =
        MutableLiveData(NetworkResult.Loading())
    val currencies: LiveData<NetworkResult<Currencies>> = _currencies

    private val _conversionResult: MutableLiveData<NetworkResult<Double?>> =
        MutableLiveData(NetworkResult.Loading())
    val conversionResult: LiveData<NetworkResult<Double?>> = _conversionResult

    private fun getCurrencies() {
        viewModelScope.launch {
            val productsResponse = repository.getCurrencies()
            if (productsResponse.isSuccessful) {
                productsResponse.body()?.let {
                    _currencies.postValue(NetworkResult.Success(it))
                }
            } else {
                _currencies.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun convertCurrency(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            val response = repository.convertCurrency(from, to, amount)
            Log.i("MINA", "===$response")

            if (response.isSuccessful) {
                Log.i("MINA", "===$response")
                response.body()?.let {
                    Log.i("MINA", "===${it.result}")
                    _conversionResult.postValue(NetworkResult.Success(it.result))
                }
            } else {
                _conversionResult.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun getFavourites() {
//        _products.value = NetworkResult.Loading()
//        viewModelScope.launch {
//            val draftResponse = repository.getFavourites(
//                sharedPreferences.getString(Constants.FAVOURITE_ID, "0")?.toLong() ?: 0L
//            )
//            if (draftResponse.isSuccessful) {
//                draftResponse.body()?.let {
//                    _favourites = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
//                    _products.postValue(
//                        NetworkResult.Success(
//                            _favourites.filter { product ->
//                                !product.title.equals(Constants.TITTLE)
//                            }
//                        )
//                    )
//                }
//            } else {
//                _products.postValue(NetworkResult.Error("error"))
//            }
//        }
    }

    fun getOrders() {
        _orders.value = NetworkResult.Loading()
        viewModelScope.launch {
            val id = sharedPreferences.getString(Constants.UserID, "0")
            val ordersResponse = repository.getCustomerOrders(id?.toLong() ?: 0L)
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