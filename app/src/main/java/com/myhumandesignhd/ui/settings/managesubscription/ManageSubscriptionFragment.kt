package com.myhumandesignhd.ui.settings.managesubscription

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentManageSubscriptionBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.settings.SettingsViewModel
import com.myhumandesignhd.vm.BaseViewModel
import org.greenrobot.eventbus.EventBus
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ManageSubscriptionFragment : BaseFragment<SettingsViewModel, FragmentManageSubscriptionBinding>(
    R.layout.fragment_manage_subscription,
    SettingsViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    private var subType = "google"

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
    }

    override fun onViewModelReady(viewModel: SettingsViewModel) {
        super.onViewModelReady(viewModel)

//        baseViewModel.checkSubscription()
        baseViewModel.subscriptionData.observe(this) { data ->
            data?.let {
                with(data) {
                    if (type.isNotEmpty()) {
                        subType = type
                    }

                    when {
                        isTrial && isCanceled && isActive -> {
                            val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("GMT")
                            val date = formatter.parse(data.expiredAt)

                            val ddMMFormatter: DateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                            val dateStr = ddMMFormatter.format(date)

                            binding.subscribeText.text = App.resourcesProvider.getStringLocale(R.string.settings_trial_expires_at)
                            binding.subscribeText.text = binding.subscribeText.text.toString().replace("{date}", dateStr)

                            binding.cancelSubscriptionBtn.isVisible = false
                            binding.subscribeBtn.isVisible = false
                        }

                        isTrial && isActive -> {
                            binding.subscribeText.text = App.resourcesProvider.getStringLocale(R.string.settings_trial_active)

                            binding.cancelSubscriptionBtn.isVisible = true
                            binding.subscribeBtn.isVisible = false
                        }

                        isActive && isCanceled -> {
                            val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("GMT")
                            val date = formatter.parse(data.expiredAt)

                            val ddMMFormatter: DateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                            val dateStr = ddMMFormatter.format(date)

                            binding.subscribeText.text = App.resourcesProvider.getStringLocale(R.string.settings_subscription_expires_at)
                            binding.subscribeText.text = binding.subscribeText.text.toString().replace("{date}", dateStr)

                            binding.cancelSubscriptionBtn.isVisible = false
                            binding.subscribeBtn.isVisible = false
                        }

                        isActive -> {
                            binding.subscribeText.text = App.resourcesProvider.getStringLocale(R.string.settings_subscription_active)

                            binding.cancelSubscriptionBtn.isVisible = true
                            binding.subscribeBtn.isVisible = false
                        }

                        else -> {
                            binding.subscribeText.text = App.resourcesProvider.getStringLocale(R.string.settings_subscription_inactive)

                            binding.cancelSubscriptionBtn.isVisible = false
                            binding.subscribeBtn.isVisible = true
                        }

                    }
                }
            }


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

        binding.subscribeTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.subscribeText.setTextColor(
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
    }
    inner class Handler {

        fun onCancelSubscriptionClicked(v: View) {
            if (subType == "google") {
                val url = "https://play.google.com/store/account/subscriptions"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } else if (subType == "stripe") {
//                "https://webhumandesign.site"
                baseViewModel.cancelStripeSubscription()
            }

        }

        fun onSubscribeClicked(v: View) {
            router.navigateTo(Screens.paywallScreen(fromStart = false, source = "settings"))
        }

        fun onBackClicked(v: View) {
            router.exit()
        }
    }
}