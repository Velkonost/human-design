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

    var currentUserId: Long
        set(value) = sharedPreferences.edit().putLong(USER_ID, value).apply()
        get() = sharedPreferences.getLong(USER_ID, -1)

    var newUserName: String?
        set(value) = sharedPreferences.edit().putString(NEW_USER_NAME, value).apply()
        get() = sharedPreferences.getString(NEW_USER_NAME, null)

    var newUserDate: String?
        set(value) = sharedPreferences.edit().putString(NEW_USER_DATE, value).apply()
        get() = sharedPreferences.getString(NEW_USER_DATE, null)

    var newUserTime: String?
        set(value) = sharedPreferences.edit().putString(NEW_USER_TIME, value).apply()
        get() = sharedPreferences.getString(NEW_USER_TIME, null)

    var newUserPlace: String?
        set(value) = sharedPreferences.edit().putString(NEW_USER_PLACE, value).apply()
        get() = sharedPreferences.getString(NEW_USER_PLACE, null)

    var lastLoginPageId: Int
        set(value) = sharedPreferences.edit().putInt(LAST_LOGIN_PAGE_ID, value).apply()
        get() = sharedPreferences.getInt(LAST_LOGIN_PAGE_ID, 0)

    fun clearNewUserTemps() {
        newUserName = null
        newUserDate = null
        newUserTime = null
        newUserPlace = null
    }

    companion object {
        const val PREF_FILE_NAME = "cv_prefs_upgrade"

        const val USER_ID = "user_id"
        const val LOCALE = "locale"
        const val IS_FIRST_LAUNCH = "is_first_launch"

        const val IS_DARK_THEME = "is_dark_theme"

        const val IS_PUSH_AVAILABLE = "is_push_available"

        const val NEW_USER_NAME = "new_user_name"
        const val NEW_USER_DATE = "new_user_date"
        const val NEW_USER_TIME = "new_user_time"
        const val NEW_USER_PLACE = "new_user_place"

        const val LAST_LOGIN_PAGE_ID = "last_login_page_id"

    }
}