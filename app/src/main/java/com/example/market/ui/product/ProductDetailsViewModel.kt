package com.example.market.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {
    fun setInCart(productDetails: ProductDetails){
        viewModelScope.launch {
            //repository.addInCart()
        }
    }
}