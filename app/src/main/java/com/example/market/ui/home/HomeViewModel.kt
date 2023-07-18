package com.example.market.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.BrandResponse
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
}