package com.jery.feedchart.util

import android.content.Context
import android.content.res.Configuration
import com.jery.feedchart.R
import java.util.Locale

class LocaleUtils(private val context: Context) {

    fun availableLanguages(): List<String> {
        return context.resources.getStringArray(R.array.languages).toList()
    }

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.createConfigurationContext(config)
    }
}