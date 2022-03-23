package ru.get.hd.ui.bodygraph.second

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.item_bodygraph_centers.view.*
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.*
import kotlinx.android.synthetic.main.item_bodygraph_gates.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphFirstBinding
import ru.get.hd.databinding.FragmentBodygraphSecondBinding
import ru.get.hd.model.TransitionChannel
import ru.get.hd.model.TransitionGate
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel
import ru.get.hd.ui.bodygraph.adapter.CentersAdapter
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

    override fun updateThemeAndLocale() {
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
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = getViewPagerAdapter()
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> selectAbout()
                    1 -> selectCenters()
                    2 -> selectGates()
                    else -> selectChannels()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
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

    private fun getViewPagerAdapter(): PagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return 4
        }

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int) =
            when(position) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.item_bodygraph_centers, null)
                    container.addView(view)
                    view
                }
                1 -> {
                    val view = layoutInflater.inflate(R.layout.item_bodygraph_centers, null)

                    view.activeCentersTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.active_centers_title))
                    view.activeCentersDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.active_centers_text))
                    view.inactiveCentersTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.inactive_centers_title))
                    view.inactiveCentersDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.inactive_centers_text))


                    view.activeCentersTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.inactiveCentersTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.activeCentersDesc.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.inactiveCentersDesc.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    val activeCentersAdapter = CentersAdapter()
                    val inactiveCentersAdapter = CentersAdapter()

                    view.activeCentersRecycler.adapter = activeCentersAdapter
                    view.inactiveCentersRecycler.adapter = inactiveCentersAdapter

                    baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                        activeCentersAdapter.createList(it.activeCentres)
                        inactiveCentersAdapter.createList(it.inactiveCentres)
                    }

                    container.addView(view)
                    view
                }
                2 -> {
                    val view = layoutInflater.inflate(R.layout.item_bodygraph_gates, null)

                    view.activeGatesTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.active_gates_title))
                    view.activeGatesDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.active_gates_text))

                    view.activeGatesTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.activeGatesDesc.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    val gatesAdapter = GatesAdapter()
                    view.gatesRecycler.adapter = gatesAdapter

                    baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                        val gates: MutableList<TransitionGate> = mutableListOf()

                        it.description.gates.keys.forEach { number ->
                            gates.add(
                                TransitionGate(
                                    number = number,
                                    title = it.description.gatesTitles[number]!!,
                                    description = it.description.gates[number]!!
                                )
                            )
                        }
                        gatesAdapter.createList(gates)
                    }

                    container.addView(view)
                    view
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.item_bodygraph_channels, null)

                    view.activeChannelTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.active_channels_title))
                    view.activeChannelDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.active_channels_text))

                    view.activeChannelTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.activeChannelDesc.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    val channelsAdapter = ChannelsAdapter()
                    view.channelsRecycler.adapter = channelsAdapter

                    baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                        val channels: MutableList<TransitionChannel> = mutableListOf()

                        it.description.channels.keys.forEach { number ->
                            channels.add(
                                TransitionChannel(
                                    number = number,
                                    title = it.description.channelsTitles[number]!!,
                                    description = it.description.channels[number]!!
                                )
                            )
                        }
                        channelsAdapter.createList(channels)
                    }

                    container.addView(view)
                    view
                }
            }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
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