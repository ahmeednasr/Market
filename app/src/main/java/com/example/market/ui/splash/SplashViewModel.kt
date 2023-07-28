package com.example.market.ui.splash

import android.content.SharedPreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.market.data.repo.Repository
import com.example.market.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val repository: Repository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun convertCurrency(from: String, to: String, q: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.convertCurrency(from, to, q)
            if (response.isSuccessful) {
                response.body()?.result?.let {
                    editor.putFloat(Constants.Exchange_Value, it.toFloat())
                    editor.apply()
                }
            }
        }
    }
}