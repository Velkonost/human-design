package com.myhumandesignhd.ui.settings

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.myhumandesignhd.App
import com.myhumandesignhd.BuildConfig
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentSettingsBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.event.UpdateThemeEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.util.ext.observeOnce
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import org.greenrobot.eventbus.EventBus

class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>(
    R.layout.fragment_settings,
    SettingsViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        Amplitude.getInstance().logEvent("settings_screen_shown")
        baseViewModel.checkSubscription()
    }

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
        binding.manageSubTitle.text =
            App.resourcesProvider.getStringLocale(R.string.settings_manage_sub_title)
        binding.writeUs.text =
            App.resourcesProvider.getStringLocale(R.string.settings_write_us_title)
        binding.version.text =
            App.resourcesProvider.getStringLocale(R.string.settings_version_title) + " " + BuildConfig.VERSION_NAME

        binding.dayTheme.text =
            App.resourcesProvider.getStringLocale(R.string.settings_light_theme_title)
        binding.nightTheme.text =
            App.resourcesProvider.getStringLocale(R.string.settings_dark_theme_title)

        binding.manageSubscriptionTitle.text = App.resourcesProvider.getStringLocale(R.string.settings_manage_subscription)
        binding.manageAccountTitle.text = App.resourcesProvider.getStringLocale(R.string.settings_manage_account)
    }

    private val updateThemeDuration = 300L
    override fun updateThemeAndLocale(
        withAnimation: Boolean,
        withTextAnimation: Boolean
    ) {
        setupLocale()

        binding.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))


        binding.personalInfoArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.faqArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.manageAccountArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.manageSubscriptionArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))


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

        binding.notificationsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme || App.preferences.isPushAvailable) R.color.lightColor
                else R.color.darkColor
            )
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
//                binding.notificationsTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.themeTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.personalInfoTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.faqTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.version.setTextColor(it.animatedValue.toString().toInt())
                binding.writeUs.setTextColor(it.animatedValue.toString().toInt())
                binding.infoBlockSeparator.setBackgroundColor(it.animatedValue.toString().toInt())
                binding.footerBlockSeparator.setBackgroundColor(it.animatedValue.toString().toInt())
                binding.footerBlockSeparator2.setBackgroundColor(it.animatedValue.toString().toInt())
                binding.privacyPolicyTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.termsTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.manageSubTitle.setTextColor(it.animatedValue.toString().toInt())

                binding.nightTheme.setTextColor(it.animatedValue.toString().toInt())

                binding.manageSubscriptionTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.manageAccountTitle.setTextColor(it.animatedValue.toString().toInt())
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

                binding.manageSubscriptionBlock.backgroundTintList =
                    ColorStateList.valueOf(it.animatedValue.toString().toInt())
                binding.manageAccountBlock.backgroundTintList =
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

            binding.notificationsTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme || App.preferences.isPushAvailable) R.color.lightColor
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

            binding.manageSubscriptionBlock.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )

            binding.manageAccountBlock.backgroundTintList = ColorStateList.valueOf(
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

            binding.manageAccountTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            binding.manageSubscriptionTitle.setTextColor(
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

            binding.footerBlockSeparator2.setBackgroundColor(
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

            binding.manageSubTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
        }

        binding.notificationsSwitch.isChecked = App.preferences.isPushAvailable
        binding.notificationsSwitch.setOnCheckedChangeListener { view, isChecked ->
            App.preferences.isPushAvailable = isChecked

            if (!App.preferences.isPushAvailable) {
                YandexMetrica.reportEvent("Tab1userDisabledNotifications")
                Amplitude.getInstance().logEvent("settingsDisabledNotifications")

                binding.notificationBlockActive.background = null
                binding.notificationBlockActive.setBackgroundColor(ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                ))

                binding.notificationsTitle.setTextColor(ContextCompat.getColor(
                    requireContext(),
                    R.color.darkSettingsArrowTint
                ))
            } else {
                YandexMetrica.reportEvent("Tab1userAllowedNotifications")
                Amplitude.getInstance().logEvent("settingsAllowedNotifications")

                binding.notificationBlockActive.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_settings_notification_bg
                )

                binding.notificationsTitle.setTextColor(ContextCompat.getColor(
                    requireContext(),
                    R.color.lightColor
                ))
            }
        }

        if (!App.preferences.isPushAvailable) {
            binding.notificationBlockActive.background = null
            binding.notificationBlockActive.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))
        } else {
            binding.notificationBlockActive.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_settings_notification_bg
            )
        }
    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    inner class Handler {

        fun onManageSubscriptionClicked(v: View) {
            baseViewModel.subscriptionData.observeOnce(this@SettingsFragment) { data ->
                data?.let {
                    with(data) {
                        if (!data.isActive) {
                            router.navigateTo(Screens.paywallScreen(fromStart = false, source = "settings"))
                        } else {
                            router.navigateTo(Screens.manageSubscriptionScreen())
                        }
                    }
                }
            }

        }

        fun onManageAccountClicked(v: View) {
            router.navigateTo(Screens.manageAccountScreen())
        }

        fun onVersionClicked(v: View) {
//            router.navigateTo(Screens.paywallScreen())
        }

        fun onNightClicked(v: View) {
            YandexMetrica.reportEvent("Tab1UserSwitchedToDarkMode")
            Amplitude.getInstance().logEvent("userSwitchedToDarkMode")

            val identify = Identify()
            identify.set("mode", "dark")
            Amplitude.getInstance().identify(identify)

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
            YandexMetrica.reportEvent("Tab1UserSwitchedToWhiteMode")
            Amplitude.getInstance().logEvent("userSwitchedToWhiteMode")

            val identify = Identify()
            identify.set("mode", "light")
            Amplitude.getInstance().identify(identify)

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
            val url = "https://humdesign.info/terms-of-use.php"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        fun onPrivacyPolicyClicked(v: View) {
            val url = "https://humdesign.info/policy.php"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        fun onWriteUsClicked(v: View) {
            YandexMetrica.reportEvent("Tab1WriteToUsTapped")

            kotlin.runCatching {
                val email: Array<String> = arrayOf("humdesignhd@gmail.com")
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, email)
                    putExtra(Intent.EXTRA_SUBJECT,   App.resourcesProvider.getStringLocale(R.string.app_name))
                }
                startActivity(intent)
            }.onFailure {
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
        }

        fun onFaqClicked(v: View) {
            YandexMetrica.reportEvent("Tab1SettingsFAQTapped")
            Amplitude.getInstance().logEvent("settingsTappedFAQ")

            router.navigateTo(Screens.faqScreen())
        }

        fun onPersonalInfoClicked(v: View) {
            YandexMetrica.reportEvent("Tab1SettingsPersonalInfoTapped")
            Amplitude.getInstance().logEvent("settingsTappedChangeInfo")

            router.navigateTo(Screens.personalInfoScreen())
        }

        fun onBackClicked(v: View) {
            router.exit()
        }

        fun openPlaystoreAccount(v: View) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?package=com.myhumandesignhd")))
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}