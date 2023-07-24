package com.example.market.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.CartResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    private val _cart: MutableLiveData<NetworkResult<CartResponse>> = MutableLiveData()
    val cart: LiveData<NetworkResult<CartResponse>> = _cart

    fun getCartItems(userId: Long) {
        viewModelScope.launch {
            try {
                val cartList = repository.getDraftOrders().body()
                Log.i("FILTERED", cartList.toString())

                val filtered = cartList?.draft_orders?.filter {
                    it.customer?.id == userId && it.tags == "cart"
                }
                Log.i("FILTERED", "issss>>>>>" + filtered)
                _cart.postValue(NetworkResult.Success(CartResponse(filtered!!)))
            } catch (e: Exception) {
                Log.i("DRAFT", "errrrrrrrrrrorr>>>>>" + e.toString())
            }
        }
    }
}