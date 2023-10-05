package com.myhumandesignhd.ui.compatibility.detail

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentCompatibilityDetailBinding
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.compatibility.CompatibilityViewModel
import com.myhumandesignhd.ui.compatibility.detail.adapter.CompatibilityDetailsAdapter
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import java.util.Locale

class CompatibilityDetailFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityDetailBinding>(
    R.layout.fragment_compatibility_detail,
    CompatibilityViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    private val secondUserName: String by lazy {
        arguments?.getString("name")!!
    }

    private val secondUserTitle: String by lazy {
        arguments?.getString("title")!!
    }

    private val secondUserChartResId: Int by lazy {
        arguments?.getInt("chartResId")!!
    }

    private val detailsAdapter: CompatibilityDetailsAdapter by lazy {
        CompatibilityDetailsAdapter()
    }

    override fun updateThemeAndLocale() {
        super.updateThemeAndLocale()

        binding.icInfo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.compatibilityContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.compatibilityTitle.text = App.resourcesProvider.getStringLocale(R.string.compatibility_detail_title)
        binding.compatibilityTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.aboutTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.about_title))
        binding.profilesTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.profiles_title))
        binding.channelsTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.channels_title))

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.profilesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.selectionCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        binding.selectionLinear.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        selectAbout()
        setupViewPager()
    }

    private fun selectAbout() {
        YandexMetrica.reportEvent("Tab4AdultsAbout")
        Amplitude.getInstance().logEvent("tab4PartnerGeneral")

        App.preferences.isCompatibilityDetailChannelsAddedNow = false

        binding.icInfo.isVisible = false
        binding.aboutTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

        binding.channelsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            ))

        binding.profilesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            ))

        binding.aboutTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.channelsTitle.background = null
        binding.profilesTitle.background = null
    }

    private fun selectProfiles() {
        YandexMetrica.reportEvent("Tab4AdultsProfile")
        Amplitude.getInstance().logEvent("tab4PartnerProfile")

        App.preferences.isCompatibilityDetailChannelsAddedNow = false
        binding.icInfo.isVisible = false
        binding.profilesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

        binding.aboutTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            ))

        binding.channelsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            ))

        binding.profilesTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.aboutTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectChannels() {
        YandexMetrica.reportEvent("Tab4AdultsChannels")
        Amplitude.getInstance().logEvent("tab4PartnerChannels")

        App.preferences.isCompatibilityDetailChannelsAddedNow = true
        binding.icInfo.isVisible = true
        binding.channelsTitle.setTextColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.aboutTitle.setTextColor(
            ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.profilesTitle.setTextColor(
            ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.channelsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.aboutTitle.background = null
        binding.profilesTitle.background = null
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = detailsAdapter

        baseViewModel.currentCompatibility.observe(viewLifecycleOwner) {
            detailsAdapter.createList(
                firstTitle =
                "${if (App.preferences.locale == "ru") baseViewModel.currentUser.subtitle1Ru 
                else baseViewModel.currentUser.subtitle1En} • ${baseViewModel.currentUser.subtitle2}",
                firstName = baseViewModel.currentUser.name,
                secondName = secondUserName,
                secondTitle = secondUserTitle,
                compatibility = it,
                chart2ResId = secondUserChartResId,
                chart1ResId =
                if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "проектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_dark
                    else R.drawable.ic_chart_proektor_light
                } else if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "рефлектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_dark
                    else R.drawable.ic_chart_reflector_light
                } else if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_dark
                    else R.drawable.ic_chart_generator_light
                } else if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_dark
                    else R.drawable.ic_chart_mangenerator_light
                } else {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_dark
                    else R.drawable.ic_chart_manifestor_light
                },
                context = requireContext(),
                descs = it.newDescriptions
            )
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> selectAbout()
                    1 -> selectProfiles()
                    else -> selectChannels()
                }
            }
        })
    }

    inner class Handler {
        fun onBackClicked(v: View) {
            router.exit()
        }

        fun onAboutClicked(v: View) {
            binding.viewPager.setCurrentItem(0, true)
            selectAbout()
        }

        fun onProfilesClicked(v: View) {
            binding.viewPager.setCurrentItem(1, true)
            selectProfiles()
        }

        fun onChannelsClicked(v: View) {
            binding.viewPager.setCurrentItem(2, true)
            selectChannels()
        }

        fun onFaqClicked(v: View) {
            router.navigateTo(Screens.compatibilityDetailInfoScreen())
        }
    }
}