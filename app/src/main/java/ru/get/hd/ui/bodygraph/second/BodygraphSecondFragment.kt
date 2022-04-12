package ru.get.hd.ui.bodygraph.second

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_faq_detail.*
import kotlinx.android.synthetic.main.item_bodygraph_centers.view.*
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.*
import kotlinx.android.synthetic.main.item_bodygraph_gates.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphFirstBinding
import ru.get.hd.databinding.FragmentBodygraphSecondBinding
import ru.get.hd.model.AboutItem
import ru.get.hd.model.AboutType
import ru.get.hd.model.TransitionChannel
import ru.get.hd.model.TransitionGate
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel
import ru.get.hd.ui.bodygraph.adapter.CentersAdapter
import ru.get.hd.ui.bodygraph.first.BodygraphFirstFragment
import ru.get.hd.ui.bodygraph.second.adapter.ColumnsAdapter
import ru.get.hd.ui.transit.adapter.ChannelsAdapter
import ru.get.hd.ui.transit.adapter.GatesAdapter
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07
import ru.get.hd.vm.BaseViewModel

class BodygraphSecondFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphSecondBinding>(
    ru.get.hd.R.layout.fragment_bodygraph_second,
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

        binding.aboutTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.about_title))
        binding.centersTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.centers_title))
        binding.gatesTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.gates_title))
        binding.channelsTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.channels_title))

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
                    name = it.strategy.name?: "",
                    description = it.strategy.description?: "",
                    type = AboutType.STRATEGY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    name = it.injury.name?: "",
                    description = it.injury.description?: "",
                    type = AboutType.INJURY
                )
            )

            columnsAdapter.createList(
                activeCenters = it.activeCentres,
                inactiveCenters = it.inactiveCentres,
                gates = gates,
                channels = channels,
                aboutItems = aboutItemsList
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
            R.drawable.ic_affirmation_bg
        )

        binding.centersTitle.background = null
        binding.gatesTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectCenters() {
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
            R.drawable.ic_affirmation_bg
        )

        binding.aboutTitle.background = null
        binding.gatesTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectGates() {
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
            R.drawable.ic_affirmation_bg
        )

        binding.aboutTitle.background = null
        binding.centersTitle.background = null
        binding.channelsTitle.background = null
    }

    private fun selectChannels() {
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
            R.drawable.ic_affirmation_bg
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