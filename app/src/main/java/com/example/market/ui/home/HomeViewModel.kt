package com.example.market.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.BrandResponse
import com.example.market.data.pojo.DiscountResponse
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _brands: MutableLiveData<NetworkResult<BrandResponse>> = MutableLiveData()
    val brands: LiveData<NetworkResult<BrandResponse>> = _brands

    private val _discountCodes: MutableLiveData<NetworkResult<DiscountResponse>> = MutableLiveData()
    val discountCodes: LiveData<NetworkResult<DiscountResponse>> = _discountCodes

    fun getBrands() {
        _brands.value = NetworkResult.Loading()
        viewModelScope.launch {
            val brandsResponse = repository.getBrands()
            if (brandsResponse.isSuccessful) {
                brandsResponse.body()?.let {
                    _brands.postValue(NetworkResult.Success(it))
                }
            } else {
                _brands.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun getDiscountCodes() {
        _discountCodes.value = NetworkResult.Loading()
        viewModelScope.launch {
            val discountResponse = repository.getDiscountCodes()
            Log.i("MYTAG", "call" + discountResponse.toString())

            if (discountResponse.isSuccessful) {
                discountResponse.body()?.let {
                    Log.i("MYTAG", "sussess" + it.toString())
                    _discountCodes.postValue(NetworkResult.Success(it))
                }
            } else {
                _discountCodes.postValue(NetworkResult.Error("error"))
            }
        }
    }

}