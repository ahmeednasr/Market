package com.example.market.ui.payment

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.DraftOrder
import com.example.market.data.pojo.DraftOrderResponse
import com.example.market.data.pojo.PriceRule
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.Constants.CURRENCY_FROM_KEY
import com.example.market.utils.Constants.CURRENCY_TO_KEY
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    private val _discountCodes: MutableLiveData<NetworkResult<PriceRule?>> = MutableLiveData()
    val discountCodes: LiveData<NetworkResult<PriceRule?>> = _discountCodes

    private var _cart: MutableLiveData<NetworkResult<DraftOrderResponse>> = MutableLiveData()
    val cart: LiveData<NetworkResult<DraftOrderResponse>> = _cart

    private val discountCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _discountCodes.postValue(NetworkResult.Error("error"))
    }

    fun getDiscountCodes(discountCode: String) {
        _discountCodes.value = NetworkResult.Loading()
        viewModelScope.launch(discountCoroutineExceptionHandler) {
            val discountResponse = repository.getDiscountCodes()
            if (discountResponse.isSuccessful) {
                discountResponse.body()?.let { result ->
                    var copoun = result.price_rules?.find { it.title == discountCode }
                    _discountCodes.postValue(NetworkResult.Success(copoun))
                }
            } else {
                _discountCodes.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun addDiscountToView(value: Double) {
        var draft = _cart.value?.data
        var data = _cart.value?.data?.draftOrder
        if (data != null && draft != null) {
            var subTotal = data.subtotalPrice?.toDouble()
            if (subTotal != null) {
                subTotal += (subTotal * (value / 100))
                var totalPrice = (subTotal + data.totalTax!!.toDouble()).toString()
                var current = data

                //_cart.value=NetworkResult.Success(DraftOrderResponse(DraftOrder(id = data.id,)))
            }
        }
    }

    fun getCartItems() {
        _cart.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val cartID: Long = sharedPreferences.getString(Constants.CART_ID, "0")!!.toLong()
            try {
                val cartList = repository.getCart(cartID)
                if (cartList.isSuccessful) {
                    cartList.body()?.let {
                        _cart.postValue(NetworkResult.Success(it))
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun convertCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.convertCurrency(
                CURRENCY_FROM_KEY,
                sharedPreferences.getString(CURRENCY_TO_KEY, "") ?: CURRENCY_FROM_KEY,
                1.0
            )
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    editor.putFloat(Constants.Exchange_Value, it.toFloat())
                    editor.apply()
                }
            }
        }
    }
}