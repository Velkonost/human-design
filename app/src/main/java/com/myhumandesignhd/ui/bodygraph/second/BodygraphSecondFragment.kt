package com.myhumandesignhd.ui.bodygraph.second

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentBodygraphSecondBinding
import com.myhumandesignhd.event.UpdateCurrentUserInjurySettingsEvent
import com.myhumandesignhd.model.AboutItem
import com.myhumandesignhd.model.AboutType
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.bodygraph.BodygraphViewModel
import com.myhumandesignhd.ui.bodygraph.second.adapter.ColumnsAdapter
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import org.greenrobot.eventbus.Subscribe

class BodygraphSecondFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphSecondBinding>(
    R.layout.fragment_bodygraph_second,
    BodygraphViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val columnsAdapter: ColumnsAdapter by lazy {
        ColumnsAdapter()
    }

    @Subscribe
    fun onUpdateCurrentUserInjurySettingsEvent(e: UpdateCurrentUserInjurySettingsEvent) {
        baseViewModel.updateUser()
    }

    override fun updateThemeAndLocale() {
        binding.bodygraphContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.descriptionTitle.text = App.resourcesProvider.getStringLocale(R.string.menu_second_title)
        binding.descriptionTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.aboutTitle.text = App.resourcesProvider.getStringLocale(R.string.about_title)
        binding.centersTitle.text = App.resourcesProvider.getStringLocale(R.string.centers_title)
        binding.gatesTitle.text = App.resourcesProvider.getStringLocale(R.string.gates_title)
        binding.channelsTitle.text = App.resourcesProvider.getStringLocale(R.string.channels_title)

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.centersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
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
        isFirstFragmentLaunch = false
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = columnsAdapter

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {

            val gates: MutableList<TransitionGate> = mutableListOf()
            val channels: MutableList<TransitionChannel> = mutableListOf()

            if (!it.description.gates.isNullOrEmpty()) {
                it.description.gates.keys.forEach { number ->
                    gates.add(
                        TransitionGate(
                            number = number,
                            title = it.description.gatesTitles[number]!!,
                            description = it.description.gates[number]!!
                        )
                    )
                }
            }

            if (!it.description.channels.isNullOrEmpty()) {
                it.description.channels.keys.forEach { number ->
                    channels.add(
                        TransitionChannel(
                            number = number,
                            title = it.description.channelsTitles[number]!!,
                            description = it.description.channels[number]!!
                        )
                    )
                }
            }

            val aboutItemsList: MutableList<AboutItem> = mutableListOf()

            aboutItemsList.add(
                AboutItem(
                    name = it.description.typeTitle,
                    description = it.description.type,
                    type = AboutType.TYPE
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.description.profileTitle,
                    description = it.description.profile,
                    type = AboutType.PROFILE
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.authority.name,
                    description = it.authority.description,
                    type = AboutType.AUTHORITY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.injury.name?: "",
                    description = it.injury.description?: "",
                    type = AboutType.INJURY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.strategy.name?: "",
                    description = it.strategy.description?: "",
                    type = AboutType.STRATEGY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.nutrition.name?: "",
                    description = it.nutrition.description?: "",
                    type = AboutType.NUTRITION
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.environment.name?: "",
                    description = it.environment.description?: "",
                    type = AboutType.ENVIRONMENT
                )
            )

            columnsAdapter.createList(
                activeCenters = it.activeCentres,
                inactiveCenters = it.inactiveCentres,
                gates = gates,
                channels = channels,
                aboutItems = aboutItemsList,
                currentUser = baseViewModel.currentUser
            )
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> selectAbout()
                    1 -> selectCenters()
                    2 -> selectGates()
                    else -> selectChannels()
                }
            }
        })
    }

    private fun selectAbout() {
        YandexMetrica.reportEvent("Tab2AboutTapped")

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.centersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.aboutTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.centersTitle.background = null
        binding.gatesTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectCenters() {
        YandexMetrica.reportEvent("Tab2CentersTapped")

        binding.centersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.centersTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.aboutTitle.background = null
        binding.gatesTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectGates() {
        YandexMetrica.reportEvent("Tab2GatesTapped")

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.centersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.gatesTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.aboutTitle.background = null
        binding.centersTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectChannels() {
        YandexMetrica.reportEvent("Tab2ChannelsTapped")

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.centersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.channelsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.aboutTitle.background = null
        binding.gatesTitle.background = null
        binding.centersTitle.background = null
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BodygraphSecondFragment? = null

        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK) {
            instance ?: buildFragment()
                .also { instance = it }
        }

        fun reset() {
            instance = null
        }

        private fun buildFragment() = BodygraphSecondFragment()
    }

    inner class Handler {

        fun onAboutClicked(v: View) {
            binding.viewPager.setCurrentItem(0, true)
            selectAbout()
        }

        fun onCentersClicked(v: View) {
            binding.viewPager.setCurrentItem(1, true)
            selectCenters()
        }

        fun onGatesClicked(v: View) {
            binding.viewPager.setCurrentItem(2, true)
            selectGates()
        }

        fun onChannelsClicked(v: View) {
            binding.viewPager.setCurrentItem(3, true)
            selectChannels()
        }

    }
}