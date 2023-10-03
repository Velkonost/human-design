package com.myhumandesignhd.ui.settings.manageaccount

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentManageAccountBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.settings.SettingsViewModel
import org.greenrobot.eventbus.EventBus

class ManageAccountFragment : BaseFragment<SettingsViewModel, FragmentManageAccountBinding>(
    R.layout.fragment_manage_account,
    SettingsViewModel::class,
    Handler::class
) {

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
    }

    override fun onViewModelReady(viewModel: SettingsViewModel) {
        super.onViewModelReady(viewModel)

        viewModel.deleteEvent.observe(this) {
            App.preferences.authToken = null
            App.preferences.uniqueUserId = null
            router.replaceScreen(Screens.startScreen())
        }
    }

    override fun updateThemeAndLocale() {
        binding.settingsContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.accountTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.accountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.faqBack.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.icBreakline.setImageResource(
            if (App.preferences.isDarkTheme)
                if (App.preferences.locale == "ru") R.drawable.ic_breakline_signup_ru
                else R.drawable.ic_breakline_signup
            else
                if (App.preferences.locale == "ru") R.drawable.ic_breakline_signup_light_ru
                else R.drawable.ic_breakline_signup_light
        )

        // confirm logout
        binding.confirmLogoutTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.confirmLogoutText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.icCrossLogout.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.confirmCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            )
        )

        binding.confirmLogoutBg.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.darkColor
            )
        )

        // confirm delete
        binding.confirmDeleteTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.confirmDeleteText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.icCrossDelete.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.confirmCardDelete.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            )
        )

        binding.confirmDeleteBg.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.darkColor
            )
        )
    }

    private fun showConfirmLogoutView() {
        binding.confirmLogoutBlock.isVisible = true
        binding.confirmCard.animate()
            .alpha(1f)
            .setDuration(300)
            .withEndAction { binding.confirmCard.alpha = 1f }

        binding.confirmLogoutBg.animate()
            .alpha(0.8f).duration = 300
    }

    private fun hideConfirmLogoutView() {
        binding.confirmCard.animate()
            .alpha(0f)
            .duration = 300

        binding.confirmLogoutBg.animate()
            .alpha(0f).setDuration(300)
            .withEndAction { binding.confirmLogoutBlock.isVisible = false }
    }

    private fun showConfirmDeleteView() {
        binding.confirmDeleteBlock.isVisible = true
        binding.confirmCardDelete.animate()
            .alpha(1f)
            .setDuration(300)
            .withEndAction { binding.confirmCardDelete.alpha = 1f }

        binding.confirmDeleteBg.animate()
            .alpha(0.8f).duration = 300
    }

    private fun hideConfirmDeleteView() {
        binding.confirmCardDelete.animate()
            .alpha(0f)
            .duration = 300

        binding.confirmDeleteBg.animate()
            .alpha(0f).setDuration(300)
            .withEndAction { binding.confirmDeleteBlock.isVisible = false }
    }

    inner class Handler {

        fun onBackClicked(v: View) {
            router.exit()
        }

        fun onLogoutClicked(v: View) {
            showConfirmLogoutView()
        }

        fun onDeleteClicked(v: View) {
            showConfirmDeleteView()
        }

        fun onCancelLogoutClicked(v: View) {
            hideConfirmLogoutView()
        }

        fun onConfirmLogoutClicked(v: View) {
            App.preferences.authToken = null
            App.preferences.uniqueUserId = null
            router.replaceScreen(Screens.startScreen())
        }

        fun onCancelDeleteClicked(v: View) {
            hideConfirmDeleteView()
        }

        fun onConfirmDeleteClicked(v: View) {
            binding.viewModel?.deleteAcc()
        }
    }
}