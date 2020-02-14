package com.okurchenko.ecocity.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import java.util.*


class LocaleHelper(context: Context) {

    private val config = context.resources.configuration

    fun getLocale() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        getSystemLocaleLegacy()
    } else {
        getSystemLocale()
    }

    private fun getSystemLocaleLegacy(): Locale? {
        return config.locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun getSystemLocale(): Locale? {
        return config.locales.get(0)
    }
}