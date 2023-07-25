package com.example.market.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.market.R
import com.example.market.databinding.ActivityMainBinding
import com.example.market.databinding.ActivitySplashBinding
import com.example.market.ui.MainActivity
import com.example.market.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        Handler().postDelayed({
            editor = sharedPreferences.edit()

            val currentCurrency = sharedPreferences.getString(Constants.CURRENCY_TO_KEY, "")
            if (currentCurrency != null && currentCurrency.isEmpty()) {
                editor.putString(Constants.CURRENCY_TO_KEY, "EGP")
                editor.apply()

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