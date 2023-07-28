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
import com.example.market.utils.Constants.COMPLETED_STATUS
import com.example.market.utils.Constants.Exchange_Value
import com.example.market.utils.Constants.OPEN_STATUS
import com.example.market.utils.Constants.TITTLE
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
            Log.i("SUBTOTAL", cartID.toString())

            try {
                val cartList = repository.getCart(cartID)
                if (cartList.isSuccessful) {
                    cartList.body()?.let {
                        Log.i("SUBTOTAL", it.draftOrder?.subtotalPrice!!)
                        _subtotal.postValue(it.draftOrder.subtotalPrice.toDouble())

                        if (it.draftOrder.status == OPEN_STATUS) {
                            _cartList = it.draftOrder.lineItems as ArrayList<LineItemsItem>
                            val filterd = _cartList.filter { item ->
                                !item.title.equals(TITTLE)
                            }
                            _cart.postValue(NetworkResult.Success(filterd))
                        }
                        else if (it.draftOrder.status == COMPLETED_STATUS) {
                            val id = sharedPreferences.getString(Constants.UserID, "0")?.toLong()
                            val customer = Customer(id = id)
                            val dummy = DraftOrderResponse(
                                DraftOrder(
                                    lineItems = mutableListOf(
                                        LineItemsItem(
                                            title = TITTLE,
                                            price = "0.0",
                                            quantity = 1,
                                            sku = "1"
                                        )
                                    ), customer = customer
                                )
                            )
                            val cartResponse = async { repository.createCartDraftOrder(dummy) }
                            if (cartResponse.await().isSuccessful) {
                                customer.multipass_identifier =
                                    cartResponse.await().body()?.draftOrder?.id.toString()
                                editor.putString(
                                    Constants.CART_ID,
                                    customer.multipass_identifier.toString()
                                )
                                editor.apply()
                                val response = repository.addAddressToUser(
                                    id ?: 0,
                                    CustomerResponse(customer)
                                )

                            }
                            getCartItems()
                        }
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
            val q = _cartList[index].quantity
            if (q != null) {
                _cartList[index].quantity = q - 1
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

    fun getUSDExchange() {
        viewModelScope.launch(Dispatchers.IO) {
            var to = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "EGP")!!
            val response = repository.convertCurrency(
                to,
                Constants.USD_VALUE,
                1.0
            )
            Log.d("menp", "$response")
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    editor.putFloat(Constants.USD_VALUE, it.toFloat())
                    editor.apply()
                    Log.d("menp", "${response.body()!!.result}")
                }
                Log.d("menp", "$response")
            }
        }
    }
}