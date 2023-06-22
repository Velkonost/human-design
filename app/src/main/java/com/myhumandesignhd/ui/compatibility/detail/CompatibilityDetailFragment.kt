package com.myhumandesignhd.ui.compatibility.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentCompatibilityDetailNewBinding
import com.myhumandesignhd.event.OpenCompatibilityAboutItemEvent
import com.myhumandesignhd.event.OpenCompatibilityChannelEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.compatibility.CompatibilityViewModel
import com.myhumandesignhd.ui.compatibility.detail.adapter.CompatibilityDetailsAdapter
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import org.greenrobot.eventbus.Subscribe
import java.util.Locale

class CompatibilityDetailFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityDetailNewBinding>(
    R.layout.fragment_compatibility_detail_new,
    CompatibilityViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
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

    private val sheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.aboutBottomSheet.bottomSheetContainer)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        val sheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    binding.blur.isVisible = false
//                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    binding.blur.isVisible = true
//                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
                }
            }
        }
        sheetBehavior.addBottomSheetCallback(sheetCallback)
    }

    override fun updateThemeAndLocale() {
        super.updateThemeAndLocale()

        binding.icInfo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.container.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.title.text = App.resourcesProvider.getStringLocale(R.string.compatibility_detail_title)
        binding.title.setTextColor(ContextCompat.getColor(
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

        binding.selectionBlock.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_selection_block_dark
            else R.drawable.bg_selection_block_light
        )

        selectAbout()
        setupViewPager()

        with(binding.aboutBottomSheet) {
            sheetTitle.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_sheet_header_dark
                else R.drawable.bg_sheet_header_light
            )
            sheetTitle.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            sheetText.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            icSheetCross.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            sheetContainer.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            ))

            bodygraphTitle.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    private fun showSheet(title: String, desc: String = "") {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        with(binding.aboutBottomSheet) {
            bottomSheetContainer.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            ))

            backSheet.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_sheet_header_dark
                else R.drawable.bg_sheet_header_light
            )

            closeSheetBtn.setOnClickListener {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            sheetScroll.fullScroll(View.FOCUS_UP)
            sheetTitle.text = title
            sheetText.text = desc
        }
    }

    @Subscribe
    fun onOpenCompatibilityChannelEvent(e: OpenCompatibilityChannelEvent) {
        showSheet(e.item.title, e.item.description)
    }

    @Subscribe
    fun onOpenCompatibilityAboutItemEvent(e: OpenCompatibilityAboutItemEvent) {
        showSheet(e.item.title, e.item.description.first().text)
    }

    private fun selectAbout() {
        YandexMetrica.reportEvent("Tab4AdultsAbout")
        Amplitude.getInstance().logEvent("tab4PartnerGeneral")

        App.preferences.isCompatibilityDetailChannelsAddedNow = false

        binding.icInfo.isVisible = false
        binding.aboutTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.lightColor
            ))

        binding.channelsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            ))

        binding.profilesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            ))

        binding.aboutTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
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
                requireContext(), R.color.lightColor
            ))

        binding.aboutTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            ))

        binding.channelsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            ))

        binding.profilesTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
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
            requireContext(), R.color.lightColor
        ))

        binding.aboutTitle.setTextColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor0_5
            else R.color.darkColor0_5
        ))

        binding.profilesTitle.setTextColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor0_5
            else R.color.darkColor0_5
        ))

        binding.channelsTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
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