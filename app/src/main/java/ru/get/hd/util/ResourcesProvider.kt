package ru.get.hd.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import ru.get.hd.App
import java.util.*
import javax.inject.Inject


class ResourcesProvider @Inject constructor(
    private val context: Context
) {
    fun getString(@StringRes stringResId: Int): String {

        return context.getString(stringResId)
    }

    fun getStringLocale(
        @StringRes stringResId: Int,
        locale: String = App.preferences.locale
    ): String {
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(locale))
        return context.createConfigurationContext(config).getText(stringResId).toString()
    }
}