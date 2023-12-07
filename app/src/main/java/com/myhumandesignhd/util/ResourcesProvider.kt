package com.myhumandesignhd.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import java.util.Locale
import javax.inject.Inject


class ResourcesProvider @Inject constructor(
    private val context: Context
) {
    fun getString(@StringRes stringResId: Int): String {

        return context.getString(stringResId)
    }

    fun getStringLocale(
        @StringRes stringResId: Int,
//        locale: String = App.preferences.locale
    ): String {
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale.getDefault())
        return context.createConfigurationContext(config).getText(stringResId).toString()
    }

    fun getStringArray() {
    }


    fun getDimen(
        @DimenRes dimenResId: Int
    ): Float {
        return context.resources.getDimension(dimenResId)
    }
}