package com.example.market.ui.cart

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.*
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.Constants.CART_ID
import com.example.market.utils.Constants.CURRENCY_FROM_KEY
import com.example.market.utils.Constants.TITTLE
import com.example.market.utils.Constants.UserID
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var _cart: MutableLiveData<NetworkResult<List<LineItemsItem>>> = MutableLiveData()
    val cart: LiveData<NetworkResult<List<LineItemsItem>>> = _cart
    private var _cartList = ArrayList<LineItemsItem>()
    private val _conversionResult: MutableLiveData<NetworkResult<Double?>> =
        MutableLiveData(NetworkResult.Loading())
    val conversionResult: LiveData<NetworkResult<Double?>> = _conversionResult

    fun getCartItems() {

        _cart.value = NetworkResult.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val cartID: Long = sharedPreferences.getString(CART_ID, "0")!!.toLong()

            try {
                val cartList = repository.getCart(cartID)
                if (cartList.isSuccessful) {
                    cartList.body()?.let {
                        _cartList = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
                        _cart.postValue(NetworkResult.Success(_cartList.filter { item ->
                            !item.title.equals(TITTLE)
                        }))
                    }
                }
                Log.i("FILTERED", cartList.toString())
            } catch (e: Exception) {
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>>" + e.toString())
            }
        }
    }

    fun deleteCartItem(listItem: LineItemsItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cartId = sharedPreferences.getString(CART_ID, "0")!!.toLong()
                Log.i("CART", cartId.toString())
                val draftResponse = repository.getCart(
                    cartId
                )
                if (draftResponse.isSuccessful) {
                    draftResponse.body()?.let {

                        Log.i("CART", it.toString())
                        val fetchedList = it.draftOrder?.lineItems as MutableList
                        fetchedList.remove(listItem)
                        repository.modifyCart(
                            cartId ?: 0,
                            DraftOrderResponse(DraftOrder(lineItems = fetchedList))
                        )
                        getCartItems()
                    }
                }
            } catch (e: Exception) {
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>>" + e.toString())
            }
        }
    }

    fun convertCurrency() {
        val to = sharedPreferences.getString(CURRENCY_FROM_KEY, "").toString()
        Log.i("DRAFT", "errrrrrrrrrrorr>>>>> " + to)

        viewModelScope.launch {
            val response = repository.convertCurrency("EGP", to, 1.0)
            Log.i("DRAFT", "errrrrrrrrrrorr>>>>> " + response)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("DRAFT", "errrrrrrrrrrorr>>>>> " + it.result)

                    _conversionResult.postValue(NetworkResult.Success(it.result))
                }
            } else {
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>> " + response)

                _conversionResult.postValue(NetworkResult.Error("error"))
            }
        }
    }
}