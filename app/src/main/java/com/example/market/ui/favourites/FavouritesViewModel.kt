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



    fun getFavourites() {
        _products.value = NetworkResult.Loading()
        viewModelScope.launch {
            val draftResponse = repository.getFavourites(
                sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()
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
        viewModelScope.launch {
            val favouritesId = sharedPreferences.getString(Constants.FAVOURITE_ID, "0")!!.toLong()
            _favourites =
                _favourites.filter { !it.title.equals(product.title) } as ArrayList<LineItemsItem>
            repository.modifyFavourites(
                favouritesId,
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