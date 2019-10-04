package com.kangmicin.hotmovie

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*

abstract class AppActivity: AppCompatActivity() {

    private var language: String? = null
    private var isChangeLanguage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = appTitle()
    }

    override fun onStart() {
        val manager = PreferenceManager(this)
        val language = manager.sharedPreferences.getString("language", Locale.getDefault().language)
        isChangeLanguage = getAppLanguage(this) != language
        super.onStart()
    }

    protected abstract fun appTitle(): String?


    override fun attachBaseContext(newBase: Context?) {
        if (newBase is Context) {
            super.attachBaseContext(updateBaseContextLocal(newBase))
        }else {
            super.attachBaseContext(newBase)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAppLanguage(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.resources.configuration.locales.get(0).language
        }
        return context.resources.configuration.locale.language
    }

    private fun updateBaseContextLocal(context: Context): Context {
        val manager = PreferenceManager(context)
        val language = manager.sharedPreferences.getString("language", Locale.getDefault().language)

        isChangeLanguage = getAppLanguage(context) != language


        val mConfiguration = context.resources.configuration
        val locale = Locale(language)
        Locale.setDefault(locale)
        mConfiguration.setLocale(locale)
        return context.createConfigurationContext(mConfiguration)
    }

    override fun onResume() {
        if (isChangeLanguage) {
            recreate()
        }
        super.onResume()
    }
}