package com.example.market.ui.payment

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.DraftOrderResponse
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.PriceRule
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private val _discountCodes: MutableLiveData<NetworkResult<PriceRule?>> = MutableLiveData()
    val discountCodes: LiveData<NetworkResult<PriceRule?>> = _discountCodes
init {
    getCartItems()
}
    private var _cart: MutableLiveData<NetworkResult<DraftOrderResponse>> = MutableLiveData()
    val cart: LiveData<NetworkResult<DraftOrderResponse>> = _cart

    private val discountCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _discountCodes.postValue(NetworkResult.Error("error"))
        Log.e("TAG", ": " + throwable.message)
    }

    fun getDiscountCodes(discountCode: String) {
        _discountCodes.value = NetworkResult.Loading()
        viewModelScope.launch(discountCoroutineExceptionHandler) {
            val discountResponse = repository.getDiscountCodes()
            Log.i("FIND", discountResponse.toString())
            if (discountResponse.isSuccessful) {
                discountResponse.body()?.let { result ->
                    var copoun = result.price_rules.find { it.title == discountCode }
                    Log.i("FIND", copoun.toString())
                    _discountCodes.postValue(NetworkResult.Success(copoun))
                }
            } else {
                _discountCodes.postValue(NetworkResult.Error("error"))
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
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>>" + e.toString())
            }
        }
    }
}