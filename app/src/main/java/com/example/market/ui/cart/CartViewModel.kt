package com.example.market.ui.cart

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.*
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants.CART_ID
import com.example.market.utils.Constants.Exchange_Value
import com.example.market.utils.Constants.TITTLE
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    private var _cart: MutableLiveData<NetworkResult<List<LineItemsItem>>> = MutableLiveData()
    val cart: LiveData<NetworkResult<List<LineItemsItem>>> = _cart

    private var _cartList = ArrayList<LineItemsItem>()

    private val _conversionResult: MutableLiveData<Double> = MutableLiveData()
    val conversionResult: LiveData<Double> = _conversionResult

    private var _subtotal: MutableLiveData<Double> = MutableLiveData()
    val subtotal: LiveData<Double> = _subtotal

    fun getCartItems() {
        _cart.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val cartID: Long = sharedPreferences.getString(CART_ID, "0")!!.toLong()
            try {
                val cartList = repository.getCart(cartID)
                if (cartList.isSuccessful) {
                    cartList.body()?.let {
                        Log.i("SUBTOTAL", it.draftOrder?.subtotalPrice!!)
                        _subtotal.postValue(it.draftOrder.subtotalPrice.toDouble())
                        _cartList = it.draftOrder.lineItems as ArrayList<LineItemsItem>

                        val filterd = _cartList.filter { item ->
                            !item.title.equals(TITTLE)
                        }
                        _cart.postValue(NetworkResult.Success(filterd))
                    }
                }

            } catch (e: Exception) {
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>>" + e.toString())
            }
        }
    }

    fun deleteCartItem(listItem: LineItemsItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cartId = sharedPreferences.getString(CART_ID, "0")!!.toLong()
                _cartList =
                    _cartList.filter { it.id != listItem.id } as ArrayList<LineItemsItem>
                repository.modifyCart(
                    cartId,
                    DraftOrderResponse(DraftOrder(lineItems = _cartList))
                )

                _cart.postValue(NetworkResult.Success(
                    _cartList.filter { item ->
                        !item.title.equals(TITTLE)
                    }
                ))
            } catch (e: Exception) {
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>>" + e.toString())
            }
        }
    }

    fun convertCurrency(from: String, to: String, q: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.convertCurrency(from, to, q)
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    _conversionResult.postValue(it)
                }
            }
        }
    }

    fun removeQuantityFromCart(lineItemsItem: LineItemsItem) {

        viewModelScope.launch(Dispatchers.IO) {
            var index = _cartList.indexOf(lineItemsItem)
            Log.d("INDEX", "size ${_cartList.size}")
            Log.d("INDEX", "size $index")
            val q = _cartList[index].quantity
            if (q != null) {
                Log.d("INDEX", "q1= ${_cartList[index].quantity}")
                _cartList[index].quantity = q - 1
                Log.d("INDEX", "q2= ${_cartList[index].quantity}")
            }
            val cartId = sharedPreferences.getString(CART_ID, "0")!!.toLong()
            repository.modifyCart(
                cartId,
                DraftOrderResponse(DraftOrder(lineItems = _cartList))
            )
        }
    }

    fun addNewQuantityToCart(lineItemsItem: LineItemsItem) {

        viewModelScope.launch(Dispatchers.IO) {
            var index =
                _cartList.indexOfFirst { (it.variantId == lineItemsItem.variantId) && ((it.productId == lineItemsItem.productId)) }
//            var index = _cartList.indexOf(lineItemsItem)
            val q = _cartList[index].quantity
            if (q != null) {
                _cartList[index].quantity = q + 1
            }
            val cartId = sharedPreferences.getString(CART_ID, "0")!!.toLong()
            Log.d("LIST", _cartList.size.toString())
            Log.d("LIST", _cartList.toString())
            repository.modifyCart(
                cartId,
                DraftOrderResponse(DraftOrder(lineItems = _cartList))
            )
        }
    }
}