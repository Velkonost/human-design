package com.myhumandesignhd.ui.start.ext

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.ui.start.StartFragment
import com.myhumandesignhd.ui.start.StartPage
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersBgIcon
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersFooter
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersIndicator1
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersIndicator2
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersIndicator3
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersIndicatorsContainer
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersSubtitle
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersTitle

fun StartFragment.setupOldUsers1() {
    currentStartPage = StartPage.OLD_USERS_1
    App.preferences.lastLoginPageId = StartPage.OLD_USERS_1.pageId

    binding.backBtn.isVisible = false
    with(binding.oldUsersView) {
        isVisible = true

        oldUsersIndicatorsContainer.isVisible = false
        oldUsersFooter.isVisible = true

        oldUsersBgIcon.setImageResource(R.drawable.ic_old_users_1)
        oldUsersTitle.text = getString(R.string.old_users_first_title)
        oldUsersSubtitle.text = getString(R.string.old_users_first_text)
        oldUsersFooter.text = getString(R.string.old_users_first_footer)
    }
}

fun StartFragment.setupOldUsers2() {
    binding.backBtn.isVisible = true

    currentStartPage = StartPage.OLD_USERS_2
    App.preferences.lastLoginPageId = StartPage.OLD_USERS_2.pageId

    with(binding.oldUsersView) {
        oldUsersIndicatorsContainer.isVisible = true

        unselectAllOldUsersIndicators()
        oldUsersIndicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        oldUsersBgIcon.setImageResource(R.drawable.ic_old_users_2)
        oldUsersTitle.text = getString(R.string.old_users_second_title)
        oldUsersSubtitle.text = getString(R.string.old_users_second_text)
        oldUsersFooter.isVisible = false
    }
}

fun StartFragment.setupOldUsers3() {
    currentStartPage = StartPage.OLD_USERS_3
    App.preferences.lastLoginPageId = StartPage.OLD_USERS_3.pageId

    with(binding.oldUsersView) {
        unselectAllOldUsersIndicators()
        oldUsersIndicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        oldUsersBgIcon.setImageResource(R.drawable.ic_old_users_3)
        oldUsersTitle.text = getString(R.string.old_users_third_title)
        oldUsersSubtitle.text = getString(R.string.old_users_third_text)

        binding.startBtnText.text = App.resourcesProvider.getStringLocale(R.string.next)
    }
}

fun StartFragment.setupOldUsers4() {
    currentStartPage = StartPage.OLD_USERS_4
    App.preferences.lastLoginPageId = StartPage.OLD_USERS_4.pageId

    with(binding.oldUsersView) {
        unselectAllOldUsersIndicators()
        oldUsersIndicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        oldUsersBgIcon.setImageResource(R.drawable.ic_old_users_4)
        oldUsersTitle.text = getString(R.string.old_users_forth_title)
        oldUsersSubtitle.text = getString(R.string.old_users_forth_text)

        binding.startBtnText.text = App.resourcesProvider.getStringLocale(R.string.create_account)
    }
}

fun StartFragment.unselectAllOldUsersIndicators() {
    with(binding.oldUsersView) {
        oldUsersIndicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        oldUsersIndicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        oldUsersIndicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )
    }
}