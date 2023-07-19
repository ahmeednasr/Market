package com.example.market.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.Currencies
import com.example.market.data.pojo.ProductResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    init {
        getCurrencies()
    }

    private val _currencies: MutableLiveData<NetworkResult<Currencies>> =
        MutableLiveData(NetworkResult.Loading())
    val currencies: LiveData<NetworkResult<Currencies>> = _currencies
    private fun getCurrencies() {
//        _currencies.value = NetworkResult.Loading()
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
}