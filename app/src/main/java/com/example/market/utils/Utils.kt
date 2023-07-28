package com.example.market.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun setLocale(language: String, context: Context) {
        val myLocale = Locale(language)
        Locale.setDefault(myLocale)
        val res: Resources = context.resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        conf.setLayoutDirection(myLocale)
        res.updateConfiguration(conf, dm)
    }

    fun formatDate(date: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate: Date = format.parse(date) as Date
        val stringFormat = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
        return stringFormat.format(formattedDate)
    }

    fun showErrorSnackbar(view: View, errorMessage: String) {
        val snackbar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
        df.roundingMode = RoundingMode.CEILING

        val formattedNumber = df.format(number)
        return formattedNumber.toDouble()
    }
}