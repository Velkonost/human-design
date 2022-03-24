package ru.get.hd.ui.settings

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.BuildConfig
import ru.get.hd.R
import ru.get.hd.databinding.FragmentSettingsBinding
import ru.get.hd.event.UpdateThemeEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment


class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>(
    ru.get.hd.R.layout.fragment_settings,
    SettingsViewModel::class,
    Handler::class
) {

    private fun setupLocale() {
        binding.title.text = App.resourcesProvider.getStringLocale(R.string.settings_title)
        binding.notificationsTitle.text =
            App.resourcesProvider.getStringLocale(R.string.settings_notifications_title)
        binding.themeTitle.text =
            App.resourcesProvider.getStringLocale(R.string.settings_theme_title)
        binding.personalInfoTitle.text =
            App.resourcesProvider.getStringLocale(R.string.settings_personal_info_title)
        binding.faqTitle.text = App.resourcesProvider.getStringLocale(R.string.settings_faq_title)
        binding.privacyPolicyTitle.text =
            App.resourcesProvider.getStringLocale(R.string.settings_privace_policy_title)
        binding.termsTitle.text =
            App.resourcesProvider.getStringLocale(R.string.settings_terms_conditions_title)
        binding.writeUs.text =
            App.resourcesProvider.getStringLocale(R.string.settings_write_us_title)
        binding.version.text =
            App.resourcesProvider.getStringLocale(R.string.settings_version_title) + " " + BuildConfig.VERSION_NAME

        binding.dayTheme.text =
            App.resourcesProvider.getStringLocale(R.string.settings_light_theme_title)
        binding.nightTheme.text =
            App.resourcesProvider.getStringLocale(R.string.settings_dark_theme_title)
    }

    private val updateThemeDuration = 300L
    override fun updateThemeAndLocale(
        withAnimation: Boolean,
        withTextAnimation: Boolean
    ) {
        setupLocale()

        binding.dayTheme.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.ic_bg_theme_day_inactive
            else R.drawable.ic_bg_theme_day
        )

        binding.nightTheme.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.ic_bg_theme_night
            else R.drawable.ic_bg_theme_night_inactive
        )

        if (withAnimation) {
            val settingsContainerAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkColor
                    else R.color.lightColor
                )
            )
            settingsContainerAnimation.duration = updateThemeDuration
            settingsContainerAnimation.addUpdateListener {
                binding.settingsContainer.setBackgroundColor(it.animatedValue.toString().toInt())
            }
            settingsContainerAnimation.start()

            val textColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkColor
                    else R.color.lightColor
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            textColorAnimation.duration = updateThemeDuration
            textColorAnimation.addUpdateListener {
                binding.title.setTextColor(it.animatedValue.toString().toInt())
                binding.themeTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.personalInfoTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.faqTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.version.setTextColor(it.animatedValue.toString().toInt())
                binding.writeUs.setTextColor(it.animatedValue.toString().toInt())
                binding.infoBlockSeparator.setBackgroundColor(it.animatedValue.toString().toInt())
                binding.footerBlockSeparator.setBackgroundColor(it.animatedValue.toString().toInt())
                binding.privacyPolicyTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.termsTitle.setTextColor(it.animatedValue.toString().toInt())

                binding.nightTheme.setTextColor(it.animatedValue.toString().toInt())
            }
            textColorAnimation.start()

            val cardTintAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightSettingsCard
                    else R.color.darkSettingsCard
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )
            cardTintAnimation.duration = updateThemeDuration
            cardTintAnimation.addUpdateListener {
                binding.notificationBlock.backgroundTintList =
                    ColorStateList.valueOf(it.animatedValue.toString().toInt())
                binding.themeBlock.backgroundTintList =
                    ColorStateList.valueOf(it.animatedValue.toString().toInt())
                binding.infoBlock.backgroundTintList =
                    ColorStateList.valueOf(it.animatedValue.toString().toInt())

            }
            cardTintAnimation.start()

            binding.writeUs.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_write_us_dark
                else R.drawable.bg_write_us_light
            )
        } else {

            binding.nightTheme.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.settingsContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkColor
                    else R.color.lightColor
                )
            )

            binding.title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.themeTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.notificationBlock.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )

            binding.themeBlock.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )

            binding.infoBlock.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )

            binding.personalInfoTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.faqTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.infoBlockSeparator.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.footerBlockSeparator.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.version.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.writeUs.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.writeUs.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_write_us_dark
                else R.drawable.bg_write_us_light
            )

            binding.privacyPolicyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.termsTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
        }
    }

    inner class Handler {

        fun onNightClicked(v: View) {
            if (!App.preferences.isDarkTheme) {
                App.preferences.isDarkTheme = true
                EventBus.getDefault().post(
                    UpdateThemeEvent(
                        isDarkTheme = true,
                        withAnimation = true,
                        withTextAnimation = true
                    )
                )
            }
        }

        fun onDayClicked(v: View) {
            if (App.preferences.isDarkTheme) {
                App.preferences.isDarkTheme = false
                EventBus.getDefault().post(
                    UpdateThemeEvent(
                        isDarkTheme = false,
                        withAnimation = true,
                        withTextAnimation = true
                    )
                )
            }
        }

        fun onTermsClicked(v: View) {
            val url = "http://humdesign.tilda.ws/terms"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        fun onPrivacyPolicyClicked(v: View) {
            val url = "http://humdesign.tilda.ws/policy"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        fun onWriteUsClicked(v: View) {
            val email = "humdesignhd@gmail.com"
            val uri = Uri.parse("mailto:$email")
                .buildUpon()
                .appendQueryParameter(
                    "subject",
                    App.resourcesProvider.getStringLocale(R.string.app_name)
                )
                .appendQueryParameter(
                    "body",
                    App.resourcesProvider.getStringLocale(R.string.app_name)
                )
                .build()

            val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(
                Intent.createChooser(
                    emailIntent,
                    App.resourcesProvider.getStringLocale(R.string.app_name)
                )
            )
        }

        fun onFaqClicked(v: View) {
            router.navigateTo(Screens.faqScreen())
//            Navigator.settingsToFaq(this@SettingsFragment)
        }
    }
}