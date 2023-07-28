package com.example.market.app

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarketApp : Application() {
    var clintId = "AVhqpMEV2wE5gi-A7vDkwh1c5X96_s2wF6k7I8IaCUzGxyPTtfdNF7T9h93ipu4SwBLBr1DNp-ZI8YcQ"
    val defaultUrl = "nativexo://paypalpay"
    val returnUrl = "com.devshiv.paypaltest://paypalpay"
    override fun onCreate() {
        super.onCreate()
        val config = CheckoutConfig(
            application = this,
            clientId = clintId,
            environment = Environment.SANDBOX,
            returnUrl = defaultUrl,
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true,
                showWebCheckout = false
            )
        )
        PayPalCheckout.setConfig(config)
    }

}