package com.example.market.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.pojo.Customer
import com.example.market.data.pojo.NewUser
import com.example.market.data.repo.Repository
import com.example.market.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private var _customer: MutableLiveData<NetworkResult<Customer>> = MutableLiveData()
    var customer : LiveData<NetworkResult<Customer>> = _customer

    private var _customers: MutableLiveData<NetworkResult<List<Customer>>> = MutableLiveData()
    var customers: LiveData<NetworkResult<List<Customer>>> = _customers

    fun createUser(user: NewUser){
        _customer.value = NetworkResult.Loading()
        viewModelScope.launch {
            val customerResponse = repository.createUser(user)
            if(customerResponse.isSuccessful){
                customerResponse.body()?.let {
                    _customer.postValue(NetworkResult.Success(it.customer))
                }
            }else{
                _customer.postValue(NetworkResult.Error("Error"))
            }
        }
    }

    fun getAllCustomers(){
        _customers.value = NetworkResult.Loading()
        viewModelScope.launch{
            val customersResponse = repository.getAllCustomers()
            if(customersResponse.isSuccessful){
                customersResponse.body()?.let {
                    _customers.postValue(NetworkResult.Success(it.customers))
                }
            }else{
                _customers.postValue(NetworkResult.Error("Error"))
            }
        }
    }

}