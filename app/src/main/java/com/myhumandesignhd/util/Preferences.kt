package com.myhumandesignhd.util

import android.content.Context
import android.content.SharedPreferences
import com.myhumandesignhd.App
import java.util.Locale

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
        get() =
            if (Locale.getDefault().language == "en" || Locale.getDefault().language == "ru" )//|| Locale.getDefault().language == "es"
                Locale.getDefault().language
            else "en"

    var isDarkTheme: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_DARK_THEME, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_DARK_THEME, true)

    var isNextYearShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_next_year_shown", value)
            .apply()
        get() = sharedPreferences.getBoolean("is_next_year_shown", false)

    var isFirstLaunch: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)

    var isPushAvailable: Boolean
        set(value) = sharedPreferences.edit().putBoolean(IS_PUSH_AVAILABLE, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_PUSH_AVAILABLE, true)

    var pushNumber: Int
        set(value) = sharedPreferences.edit().putInt(PUSH_NUMBER, value).apply()
        get() = sharedPreferences.getInt(PUSH_NUMBER, 0)

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

    var amountPaywallsShown: Int
        set(value) = sharedPreferences.edit().putInt("amount_paywalls_shown", value).apply()
        get() = sharedPreferences.getInt("amount_paywalls_shown", 0)

    var lastKnownLocation: String?
        set(value) = sharedPreferences.edit().putString("last_known_location", value).apply()
        get() = sharedPreferences.getString("last_known_location", null)

    var lastKnownLocationLat: String?
        set(value) = sharedPreferences.edit().putString("last_known_location_lat", value).apply()
        get() = sharedPreferences.getString("last_known_location_lat", null)

    var lastKnownLocationLon: String?
        set(value) = sharedPreferences.edit().putString("last_known_location_lon", value).apply()
        get() = sharedPreferences.getString("last_known_location_lon", null)

    var isPremiun: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_premium", value).apply()
        get() = sharedPreferences.getBoolean("is_premium", false)

    var uniqueUserId: String?
        set(value) = sharedPreferences.edit().putString("unique_user_id", value).apply()
        get() = sharedPreferences.getString("unique_user_id", null)

    fun clearNewUserTemps() {
        newUserName = null
        newUserDate = null
        newUserTime = null
        newUserPlace = null
    }

//    helps
    var bodygraphToDecryptionHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(BODYGRAPH_TO_DECRYPTION_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(BODYGRAPH_TO_DECRYPTION_HELP_SHOWN, false)

    var bodygraphAddDiagramHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(BODYGRAPH_ADD_DIAGRAM_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(BODYGRAPH_ADD_DIAGRAM_HELP_SHOWN, false)

    var bodygraphCentersHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(BODYGRAPH_CENTERS_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(BODYGRAPH_CENTERS_HELP_SHOWN, false)

    var transitHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(TRANSIT_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(TRANSIT_HELP_SHOWN, false)

    var compatibilityChannelsHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(COMPATIBILITY_CHANNELS_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(COMPATIBILITY_CHANNELS_HELP_SHOWN, false)

    var affirmationHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(AFFIRMATION_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(AFFIRMATION_HELP_SHOWN, false)

    var isCompatibilityDetailChannelsAddedNow: Boolean
        set(value) = sharedPreferences.edit().putBoolean(
            IS_COMPATIBILITY_DETAIL_CHANNELS_IS_ADDED_NOW, value)
            .apply()
        get() = sharedPreferences.getBoolean(IS_COMPATIBILITY_DETAIL_CHANNELS_IS_ADDED_NOW, false)

    var isCompatibilityPartnersHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(COMPATIBILITY_PARTNERS_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(COMPATIBILITY_PARTNERS_HELP_SHOWN, false)

    var isCompatibilityChildrenHelpShown: Boolean
        set(value) = sharedPreferences.edit().putBoolean(COMPATIBILITY_CHILDREN_HELP_SHOWN, value)
            .apply()
        get() = sharedPreferences.getBoolean(COMPATIBILITY_CHILDREN_HELP_SHOWN, false)

    var isInvokeNewTransits: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_invoke_new_transits", value)
            .apply()
        get() = sharedPreferences.getBoolean("is_invoke_new_transits", false)

    var isInvokeNewDescription: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_invoke_new_description", value)
            .apply()
        get() = sharedPreferences.getBoolean("is_invoke_new_description", false)

    var isInvokeNewCompatibility: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_invoke_new_compatibility", value)
            .apply()
        get() = sharedPreferences.getBoolean("is_invoke_new_compatibility", false)

    var isInvokeNewInsights: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_invoke_new_insights", value)
            .apply()
        get() = sharedPreferences.getBoolean("is_invoke_new_insights", false)

    var isCompatibilityFromChild: Boolean
        set(value) = sharedPreferences.edit().putBoolean("is_compatibility_from_child", value)
            .apply()
        get() = sharedPreferences.getBoolean("is_compatibility_from_child", false)

    var lastPaywall: Int
        set(value) = sharedPreferences.edit().putInt("last_paywall", value).apply()
        get() = sharedPreferences.getInt("last_paywall", 0)

    var userNameFromStart: String?
        set(value) = sharedPreferences.edit().putString("user_name_from_start", value).apply()
        get() = sharedPreferences.getString("user_name_from_start", null)

    companion object {
        const val PREF_FILE_NAME = "cv_prefs_upgrade"

        const val USER_ID = "user_id"
        const val LOCALE = "locale"
        const val IS_FIRST_LAUNCH = "is_first_launch"

        const val IS_DARK_THEME = "is_dark_theme"

        const val IS_PUSH_AVAILABLE = "is_push_available"
        const val PUSH_NUMBER = "push_number"

        const val NEW_USER_NAME = "new_user_name"
        const val NEW_USER_DATE = "new_user_date"
        const val NEW_USER_TIME = "new_user_time"
        const val NEW_USER_PLACE = "new_user_place"

        const val LAST_LOGIN_PAGE_ID = "last_login_page_id"

        const val BODYGRAPH_TO_DECRYPTION_HELP_SHOWN = "bodygraph_to_decryption_help_shown"
        const val BODYGRAPH_ADD_DIAGRAM_HELP_SHOWN = "bodygraph_add_diagram_help_shown"
        const val BODYGRAPH_CENTERS_HELP_SHOWN = "bodygraph_centers_help_shown"
        const val TRANSIT_HELP_SHOWN = "transit_help_shown"

        const val COMPATIBILITY_CHANNELS_HELP_SHOWN = "compatibility_channels_help_shown"
        const val COMPATIBILITY_PARTNERS_HELP_SHOWN = "compatibility_partners_help_shown"
        const val COMPATIBILITY_CHILDREN_HELP_SHOWN = "compatibility_children_help_shown"

        const val AFFIRMATION_HELP_SHOWN = "affirmation_help_shown"

        const val IS_COMPATIBILITY_DETAIL_CHANNELS_IS_ADDED_NOW =
            "is_compatibility_detail_channels_is_added_now"

    }
}