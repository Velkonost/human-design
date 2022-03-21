package ru.get.hd.util

import android.content.Context
import android.content.SharedPreferences
import ru.get.hd.App

class Preferences(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        if (context !is App) {
            throw IllegalArgumentException("Expected application context")
        }
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }


    var locale: String
        set(value) = sharedPreferences.edit().putString(LOCALE, value).apply()
        get() = sharedPreferences.getString(LOCALE, "ru") ?: "ru"

    var isDarkTheme: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_DARK_THEME, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_DARK_THEME, true)

    var isFirstLaunch: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)

    var isPushAvailable: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_PUSH_AVAILABLE, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_PUSH_AVAILABLE, true)

    var currentUserId: String?
        set(value) = sharedPreferences.edit().putString(USER_ID, value).apply()
        get() = sharedPreferences.getString(USER_ID, null)


    companion object {
        const val PREF_FILE_NAME = "cv_prefs_upgrade"

        const val USER_ID = "user_id"
        const val LOCALE = "locale"
        const val IS_FIRST_LAUNCH = "is_first_launch"

        const val IS_DARK_THEME = "is_dark_theme"

        const val IS_PUSH_AVAILABLE = "is_push_available"
    }
}