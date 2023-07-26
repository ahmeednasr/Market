package com.example.market.ui.favourites

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.DraftOrder
import com.example.market.data.pojo.DraftOrderResponse
import com.example.market.data.pojo.LineItemsItem
import com.example.market.data.pojo.Product
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.Constants.TITTLE
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.math.BigDecimal
import java.math.RoundingMode

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var _favourites = ArrayList<LineItemsItem>()

    private val _products: MutableLiveData<NetworkResult<List<LineItemsItem>>> = MutableLiveData()
    val products: LiveData<NetworkResult<List<LineItemsItem>>> = _products

    private val _conversionResult: MutableLiveData<Double> = MutableLiveData()
    val conversionResult: LiveData<Double> = _conversionResult

    private val coroutineExceptionHandler= CoroutineExceptionHandler { _, throwable ->
        _products.postValue(NetworkResult.Error("error"))
        Log.e("TAG", ": "+throwable.message)
    }

    fun convertCurrency(from: String, to: String,amount:Double) {
        viewModelScope.launch {
            val response = repository.convertCurrency(from, to,amount)
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    _conversionResult.postValue(it)
                }
            }
        }
    }

    fun getFavourites() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch(coroutineExceptionHandler) {
            val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")?.toLong()
            val draftResponse = repository.getFavourites(
                favouritesId ?: 0L
            )
            if (draftResponse.isSuccessful) {
                draftResponse.body()?.let {
                    _favourites = it.draftOrder?.lineItems as ArrayList<LineItemsItem>
                    _products.postValue(
                        NetworkResult.Success(
                            _favourites.filter { product ->
                                !product.title.equals(TITTLE)
                            }
                        )
                    )
                }
            } else {
                _products.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun deleteFavourite(product: LineItemsItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")?.toLong()
            _favourites =
                _favourites.filter { !it.title.equals(product.title) } as ArrayList<LineItemsItem>
            repository.modifyFavourites(
                favouritesId ?: 0L,
                DraftOrderResponse(DraftOrder(lineItems = _favourites))
            )
            _products.postValue(
                NetworkResult.Success(
                    _favourites.filter { product ->
                        !product.title.equals(TITTLE)
                    }
                )
            )
        }
    }

}