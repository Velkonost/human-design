package com.myhumandesignhd.ui.compatibility.child

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentCompatibilityChildBinding
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.compatibility.CompatibilityViewModel
import com.myhumandesignhd.ui.compatibility.child.adapter.CompatibilityChildAdapter
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class CompatibilityChildFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityChildBinding>(
    R.layout.fragment_compatibility_child,
    CompatibilityViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val childId: Long by lazy {
        arguments?.getLong("childId")!!
    }

    private val columnsAdapter: CompatibilityChildAdapter by lazy {
        CompatibilityChildAdapter()
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

        binding.parentTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.compatibility_parent_title))
        binding.childTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.compatibility_child_title))

        binding.parentTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.childTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.selectionBlock.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_selection_block_dark
            else R.drawable.bg_selection_block_light
        )

        selectChild()
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

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = columnsAdapter

        GlobalScope.launch(Dispatchers.Main) {
            val child = baseViewModel.getAllChildren().find { it.id == childId }!!

            requireActivity().runOnUiThread {
                columnsAdapter.createList(
                    childTitle =
                    if (App.preferences.locale == "ru") child.subtitle1Ru!!
                    else child.subtitle1En!!,
                    childDesc =
                    if (App.preferences.locale == "ru") child.kidDescriptionRu!!
                    else child.kidDescriptionEn!!,
                    parentTitle =
                    if (App.preferences.locale == "ru") baseViewModel.currentUser.subtitle1Ru!!
                    else baseViewModel.currentUser.subtitle1En!!,
                    parentDesc =
                    baseViewModel.currentUser.parentDescription!!,
                    childrenTitles = child.titles,
                    childrenDescriptions = child.descriptions,
                    chart1ResId =
                    if (child.subtitle1Ru?.lowercase(Locale.getDefault()) == "проектор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_child_dark
                        else R.drawable.ic_chart_proektor_child_light
                    } else if (child.subtitle1Ru?.lowercase(Locale.getDefault()) == "рефлектор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_child_dark
                        else R.drawable.ic_chart_reflector_child_light
                    } else if (child.subtitle1Ru?.lowercase(Locale.getDefault()) == "генератор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_child_dark
                        else R.drawable.ic_chart_generator_child_light
                    } else if (child.subtitle1Ru?.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_child_dark
                        else R.drawable.ic_chart_mangenerator_child_light
                    } else {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_child_dark
                        else R.drawable.ic_chart_manifestor_child_light
                    },
                    chart2ResId =
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
                    }
                )
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> selectChild()
                    else -> selectParent()
                }
            }
        })
    }

    private fun selectParent() {
        YandexMetrica.reportEvent("Tab4ChildrenParent")
        Amplitude.getInstance().logEvent("tab4TappedFamilyParent")

        binding.parentTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.lightColor
            ))

        binding.childTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            ))

        binding.parentTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
        )

        binding.childTitle.background = null
    }

    private fun selectChild() {
        YandexMetrica.reportEvent("Tab4ChildrenChild")
        Amplitude.getInstance().logEvent("tab4TappedFamilyChild")

        binding.childTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.lightColor
            ))

        binding.parentTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            ))

        binding.childTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
        )

        binding.parentTitle.background = null
    }

    inner class Handler {
        fun onBackClicked(v: View) {
            App.preferences.isCompatibilityFromChild = true
            router.exit()
        }

        fun onParentClicked(v: View) {
            binding.viewPager.setCurrentItem(1, true)
            selectParent()
        }

        fun onChildClicked(v: View) {
            binding.viewPager.setCurrentItem(0, true)
            selectChild()

        }
    }
}