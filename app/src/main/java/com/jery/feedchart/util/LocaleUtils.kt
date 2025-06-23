package com.jery.feedchart.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.jery.feedchart.R
import java.util.Locale

class LocaleUtils(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)

    fun availableLanguages(): List<String> {
        return context.resources.getStringArray(R.array.languages).toList()
    }

    fun setLocale(languageCode: String) {
        prefs.edit().putString("selected_language", languageCode).apply()
        applyLocale(languageCode)
    }

    fun getPersistedLocale(): String? {
        return prefs.getString("selected_language", null)
    }

    fun applyLocale(languageCode: String?) {
        if (languageCode == null) return
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    companion object {
        fun wrapContext(context: Context): Context {
            val utils = LocaleUtils(context)
            val lang = utils.getPersistedLocale()
            if (lang != null) {
                val locale = Locale(lang)
                Locale.setDefault(locale)
                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                return context.createConfigurationContext(config)
            }
            return context
        }
    }
}