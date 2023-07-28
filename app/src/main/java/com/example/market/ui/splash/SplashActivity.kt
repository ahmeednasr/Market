package com.example.market.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import com.example.market.databinding.ActivitySplashBinding
import com.example.market.ui.MainActivity
import com.example.market.utils.Constants.CURRENCY_FROM_KEY
import com.example.market.utils.Constants.CURRENCY_TO_KEY
import com.example.market.utils.Constants.ENGLISH
import com.example.market.utils.Constants.LANGUAGE_KEY
import com.example.market.utils.Utils.setLocale
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        viewModel.convertCurrency(
            CURRENCY_FROM_KEY,
            sharedPreferences.getString(CURRENCY_TO_KEY, "") ?: CURRENCY_FROM_KEY,
            1.0
        )
        Handler().postDelayed({
            editor = sharedPreferences.edit()
            val currentCurrency = sharedPreferences.getString(CURRENCY_TO_KEY, "")
            val language = sharedPreferences.getString(LANGUAGE_KEY, "")

            if (currentCurrency == null || currentCurrency.isEmpty()) {
                editor.putString(CURRENCY_TO_KEY, "EGP")
                editor.apply()
            }
            if (language == null || language.isEmpty()) {
                editor.putString(LANGUAGE_KEY, ENGLISH)
                editor.apply()
                setLocale(ENGLISH, this)
            } else {
                setLocale(language, this)
            }

            startMainActivity()
        }, 3000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}