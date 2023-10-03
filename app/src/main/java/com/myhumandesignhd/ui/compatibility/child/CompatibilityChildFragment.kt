package com.myhumandesignhd.ui.compatibility.child

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentCompatibilityChildBinding
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.compatibility.CompatibilityViewModel
import com.myhumandesignhd.ui.compatibility.child.adapter.CompatibilityChildAdapter
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import java.util.Locale

class CompatibilityChildFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityChildBinding>(
    R.layout.fragment_compatibility_child,
    CompatibilityViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    private val childId: Long by lazy { arguments?.getLong("childId")!! }

    private val columnsAdapter: CompatibilityChildAdapter by lazy { CompatibilityChildAdapter() }

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

        selectChild()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = columnsAdapter

        baseViewModel.childrenData.observe(this) { children ->
            val child = children.find { it.id == childId }!!

            baseViewModel.currentBodygraph.observe(this) { parent ->
                columnsAdapter.createList(
                    childTitle = child.type,
                    childDesc = child.kidDescription,
                    parentTitle = parent.type,
                    parentDesc = parent.parentDescription,
                    childrenTitles = child.childrenDescriptions.titles,
                    childrenDescriptions = child.childrenDescriptions.descriptions,
                    chart1ResId =
                    if (child.type.lowercase(Locale.getDefault()) == "проектор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_child_dark
                        else R.drawable.ic_chart_proektor_child_light
                    } else if (child.type.lowercase(Locale.getDefault()) == "рефлектор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_child_dark
                        else R.drawable.ic_chart_reflector_child_light
                    } else if (child.type.lowercase(Locale.getDefault()) == "генератор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_child_dark
                        else R.drawable.ic_chart_generator_child_light
                    } else if (child.type.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_child_dark
                        else R.drawable.ic_chart_mangenerator_child_light
                    } else {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_child_dark
                        else R.drawable.ic_chart_manifestor_child_light
                    },
                    chart2ResId =
                    if (parent.type.lowercase(Locale.getDefault()) == "проектор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_dark
                        else R.drawable.ic_chart_proektor_light
                    } else if (parent.type.lowercase(Locale.getDefault()) == "рефлектор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_dark
                        else R.drawable.ic_chart_reflector_light
                    } else if (parent.type.lowercase(Locale.getDefault()) == "генератор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_dark
                        else R.drawable.ic_chart_generator_light
                    } else if (parent.type.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_dark
                        else R.drawable.ic_chart_mangenerator_light
                    } else {
                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_dark
                        else R.drawable.ic_chart_manifestor_light
                    }
                )
            }

//            requireActivity().runOnUiThread {
//                columnsAdapter.createList(
//                    childTitle = child.type,
//                    childDesc = child.description.profile,
//                    parentTitle =
//                    if (App.preferences.locale == "ru") baseViewModel.currentUser.subtitle1Ru!!
//                    else baseViewModel.currentUser.subtitle1En!!,
//                    parentDesc =
//                    baseViewModel.currentUser.parentDescription!!,
//                    childrenTitles = child.description.channelsTitles.values.toList(),
//                    childrenDescriptions = child.description.channels.values.toList(),
//                    chart1ResId =
//                    if (child.type.lowercase(Locale.getDefault()) == "проектор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_child_dark
//                        else R.drawable.ic_chart_proektor_child_light
//                    } else if (child.type.lowercase(Locale.getDefault()) == "рефлектор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_child_dark
//                        else R.drawable.ic_chart_reflector_child_light
//                    } else if (child.type.lowercase(Locale.getDefault()) == "генератор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_child_dark
//                        else R.drawable.ic_chart_generator_child_light
//                    } else if (child.type.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_child_dark
//                        else R.drawable.ic_chart_mangenerator_child_light
//                    } else {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_child_dark
//                        else R.drawable.ic_chart_manifestor_child_light
//                    },
//                    chart2ResId =
//                    if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "проектор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_dark
//                        else R.drawable.ic_chart_proektor_light
//                    } else if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "рефлектор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_dark
//                        else R.drawable.ic_chart_reflector_light
//                    } else if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "генератор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_dark
//                        else R.drawable.ic_chart_generator_light
//                    } else if (baseViewModel.currentUser.subtitle1Ru?.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_dark
//                        else R.drawable.ic_chart_mangenerator_light
//                    } else {
//                        if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_dark
//                        else R.drawable.ic_chart_manifestor_light
//                    }
//                )
//            }
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
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

        binding.childTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            ))

        binding.parentTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.childTitle.background = null
    }

    private fun selectChild() {
        YandexMetrica.reportEvent("Tab4ChildrenChild")
        Amplitude.getInstance().logEvent("tab4TappedFamilyChild")

        binding.childTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

        binding.parentTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            ))

        binding.childTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
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