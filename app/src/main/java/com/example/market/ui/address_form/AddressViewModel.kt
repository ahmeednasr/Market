package com.example.market.ui.address_form

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.*
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _governmentsResult: MutableLiveData<NetworkResult<GovernmentPojo?>> =
        MutableLiveData(NetworkResult.Loading())
    val governmentsResult: LiveData<NetworkResult<GovernmentPojo?>> = _governmentsResult
    private val _citiesResult: MutableLiveData<NetworkResult<CitiesPojo?>> =
        MutableLiveData(NetworkResult.Loading())
    val citiesResult: LiveData<NetworkResult<CitiesPojo?>> = _citiesResult
    private val _addresses: MutableLiveData<NetworkResult<List<CustomerAddress>>> =
        MutableLiveData(NetworkResult.Loading())
    val address: LiveData<NetworkResult<List<CustomerAddress>>> = _addresses
    private val _customer: MutableLiveData<NetworkResult<CustomerResponse>> =
        MutableLiveData(NetworkResult.Loading())
    val costumer: LiveData<NetworkResult<CustomerResponse>> = _customer

    fun getGovernments(country: String) {
        viewModelScope.launch {
            val response = repository.getGovernment(country)
            Log.i("ADDRESS", response.toString())
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("ADDRESS", it.toString())
                    _governmentsResult.postValue(NetworkResult.Success(it))
                }
            } else {
                _governmentsResult.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun getCities(country: String, government: String) {
        viewModelScope.launch {
            val response = repository.getCities(country, government)
            Log.i("ADDRESS", response.toString())
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i("ADDRESS", it.toString())
                    _citiesResult.postValue(NetworkResult.Success(it))
                }
            } else {
                _citiesResult.postValue(NetworkResult.Error("error"))
            }
        }
    }

    fun getCustomerAddresses() {
        viewModelScope.launch {
            val id = sharedPreferences.getString(Constants.UserID,"0")?.toLong()
            val response = repository.getCustomer(id?:0)
            if(response.isSuccessful){
                response.body()?.let {
                    _customer.postValue(NetworkResult.Success(it))
                    _addresses.postValue(NetworkResult.Success(it.customer.addresses!!))
                }
            } else {
                _addresses.postValue(NetworkResult.Error("Error"))
                _customer.postValue(NetworkResult.Error("Error"))
            }
        }

    }

    fun modifyCustomerAddress(address: CustomerResponse){
        viewModelScope.launch {
            val id = sharedPreferences.getString(Constants.UserID,"0")?.toLong()
            repository.addAddressToUser(id?:0,address)
        }
    }

}