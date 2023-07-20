package com.example.market.product

import androidx.lifecycle.ViewModel
import com.example.market.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

}