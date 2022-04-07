package ru.get.hd.ui.compatibility.detail

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.selects.select
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentCompatibilityBinding
import ru.get.hd.databinding.FragmentCompatibilityDetailBinding
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.compatibility.CompatibilityFragment
import ru.get.hd.ui.compatibility.CompatibilityViewModel
import ru.get.hd.ui.compatibility.detail.adapter.CompatibilityDetailsAdapter
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel

class CompatibilityDetailFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityDetailBinding>(
    ru.get.hd.R.layout.fragment_compatibility_detail,
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
        App.preferences.isCompatibilityDetailChannelsAddedNow = false

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
            R.drawable.ic_affirmation_bg
        )

        binding.channelsTitle.background = null
        binding.profilesTitle.background = null
    }

    private fun selectProfiles() {
        App.preferences.isCompatibilityDetailChannelsAddedNow = false

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
            R.drawable.ic_affirmation_bg
        )

        binding.aboutTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectChannels() {
        App.preferences.isCompatibilityDetailChannelsAddedNow = true

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
            R.drawable.ic_affirmation_bg
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
                if (baseViewModel.currentUser.subtitle1Ru?.toLowerCase() == "проектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_dark
                    else R.drawable.ic_chart_proektor_light
                } else if (baseViewModel.currentUser.subtitle1Ru?.toLowerCase() == "рефлектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_dark
                    else R.drawable.ic_chart_reflector_light
                } else if (baseViewModel.currentUser.subtitle1Ru?.toLowerCase() == "генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_dark
                    else R.drawable.ic_chart_generator_light
                } else if (baseViewModel.currentUser.subtitle1Ru?.toLowerCase() == "манифестирующий генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_dark
                    else R.drawable.ic_chart_mangenerator_light
                } else {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_dark
                    else R.drawable.ic_chart_manifestor_light
                },
                context = requireContext()
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
    }
}