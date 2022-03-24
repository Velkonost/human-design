package ru.get.hd.ui.transit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.item_transit_channels.view.*
import kotlinx.android.synthetic.main.item_transit_gates.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentTransitBinding
import ru.get.hd.repo.AppDatabase
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.transit.adapter.ChannelsAdapter
import ru.get.hd.ui.transit.adapter.GatesAdapter
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel

class TransitFragment : BaseFragment<TransitViewModel, FragmentTransitBinding>(
    R.layout.fragment_transit,
    TransitViewModel::class,
    Handler::class
) {

    companion object {
        @Volatile
        private var instance: TransitFragment? = null

        private val LOCK = Any()

        operator fun invoke() = TransitFragment.instance ?: synchronized(LOCK) {
            TransitFragment.instance ?: TransitFragment.buildTransitFragment()
                .also { TransitFragment.instance = it }
        }

        private fun buildTransitFragment() = TransitFragment()
    }

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.instance.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun updateThemeAndLocale() {
        binding.transitContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.transitTitle.text = App.resourcesProvider.getStringLocale(R.string.daily_transits_title)
        binding.transitTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.gatesTitle.text = App.resourcesProvider.getStringLocale(R.string.gates_title)
        binding.channelsTitle.text = App.resourcesProvider.getStringLocale(R.string.channels_title)

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

        selectGates()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = getViewPagerAdapter()
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> selectGates()
                    else -> selectChannels()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun selectGates() {
        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.gatesTitle.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_affirmation_bg
        )

        binding.channelsTitle.background = null
    }

    private fun selectChannels() {
        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.channelsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_affirmation_bg
        )

        binding.gatesTitle.background = null
    }

    private fun getViewPagerAdapter(): PagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return 2
        }

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return when (position) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_gates, null)

                    val gatesAdapter = GatesAdapter()
                    view.gatesRecycler.adapter = gatesAdapter

                    view.transitTitle.text = App.resourcesProvider.getStringLocale(R.string.transit_title)
                    view.designTitle.text = App.resourcesProvider.getStringLocale(R.string.design_title)

                    view.nextTransitTitle.text = App.resourcesProvider.getStringLocale(R.string.next_transit_text)
                    view.nextTransitTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    baseViewModel.currentTransit.observe(viewLifecycleOwner) {
                        gatesAdapter.createList(it.onlyCurrentGates)


                        view.leftZnak1.setTextAnimation("${it.birthDesignPlanets[0].gate}.${it.birthDesignPlanets[0].line}") {
                            view.designTitle.alpha1(500)
                            view.znak1Red.alpha1(500)
                        }

                        view.leftZnak2.setTextAnimation("${it.birthDesignPlanets[1].gate}.${it.birthDesignPlanets[1].line}") {
                            view.znak2Red.alpha1(500)
                        }

                        view.leftZnak3.setTextAnimation("${it.birthDesignPlanets[2].gate}.${it.birthDesignPlanets[2].line}") {
                            view.znak3Red.alpha1(500)
                        }

                        view.leftZnak4.setTextAnimation("${it.birthDesignPlanets[3].gate}.${it.birthDesignPlanets[3].line}") {
                            view.znak4Red.alpha1(500)
                        }

                        view.leftZnak5.setTextAnimation("${it.birthDesignPlanets[4].gate}.${it.birthDesignPlanets[4].line}") {
                            view.znak5Red.alpha1(500)
                        }

                        view.leftZnak6.setTextAnimation("${it.birthDesignPlanets[5].gate}.${it.birthDesignPlanets[5].line}") {
                            view.znak6Red.alpha1(500)
                        }

                        view.leftZnak7.setTextAnimation("${it.birthDesignPlanets[6].gate}.${it.birthDesignPlanets[6].line}") {
                            view.znak7Red.alpha1(500)
                        }

                        view.leftZnak8.setTextAnimation("${it.birthDesignPlanets[7].gate}.${it.birthDesignPlanets[7].line}") {
                            view.znak8Red.alpha1(500)
                        }

                        view.leftZnak9.setTextAnimation("${it.birthDesignPlanets[8].gate}.${it.birthDesignPlanets[8].line}") {
                            view.znak9Red.alpha1(500)
                        }

                        view.leftZnak10.setTextAnimation("${it.birthDesignPlanets[9].gate}.${it.birthDesignPlanets[9].line}") {
                            view.znak10Red.alpha1(500)
                        }

                        view.leftZnak11.setTextAnimation("${it.birthDesignPlanets[10].gate}.${it.birthDesignPlanets[10].line}") {
                            view.znak11Red.alpha1(500)
                        }

                        view.leftZnak12.setTextAnimation("${it.birthDesignPlanets[11].gate}.${it.birthDesignPlanets[11].line}") {
                            view.znak12Red.alpha1(500)
                        }

                        view.leftZnak13.setTextAnimation("${it.birthDesignPlanets[12].gate}.${it.birthDesignPlanets[12].line}") {
                            view.znak13Red.alpha1(500)
                        }

                        view.rightZnak1.setTextAnimation("${it.currentDesignPlanets[0].gate}.${it.currentDesignPlanets[0].line}")
                        view.rightZnak2.setTextAnimation("${it.currentDesignPlanets[1].gate}.${it.currentDesignPlanets[1].line}")
                        view.rightZnak3.setTextAnimation("${it.currentDesignPlanets[2].gate}.${it.currentDesignPlanets[2].line}")
                        view.rightZnak4.setTextAnimation("${it.currentDesignPlanets[3].gate}.${it.currentDesignPlanets[3].line}")
                        view.rightZnak5.setTextAnimation("${it.currentDesignPlanets[4].gate}.${it.currentDesignPlanets[4].line}")
                        view.rightZnak6.setTextAnimation("${it.currentDesignPlanets[5].gate}.${it.currentDesignPlanets[5].line}")
                        view.rightZnak7.setTextAnimation("${it.currentDesignPlanets[6].gate}.${it.currentDesignPlanets[6].line}")
                        view.rightZnak8.setTextAnimation("${it.currentDesignPlanets[7].gate}.${it.currentDesignPlanets[7].line}")
                        view.rightZnak9.setTextAnimation("${it.currentDesignPlanets[8].gate}.${it.currentDesignPlanets[8].line}")
                        view.rightZnak10.setTextAnimation("${it.currentDesignPlanets[9].gate}.${it.currentDesignPlanets[9].line}")
                        view.rightZnak11.setTextAnimation("${it.currentDesignPlanets[10].gate}.${it.currentDesignPlanets[10].line}")
                        view.rightZnak12.setTextAnimation("${it.currentDesignPlanets[11].gate}.${it.currentDesignPlanets[11].line}")
                        view.rightZnak13.setTextAnimation("${it.currentDesignPlanets[12].gate}.${it.currentDesignPlanets[12].line}")

                        view.blueZnak1.setTextAnimation("${it.currentPersonalityPlanets[0].gate}.${it.currentPersonalityPlanets[0].line}") {
                            view.transitTitle.alpha1(500)
                            view.znak1Blue.alpha1(500)
                        }

                        view.blueZnak2.setTextAnimation("${it.currentPersonalityPlanets[1].gate}.${it.currentPersonalityPlanets[1].line}") {
                            view.znak2Blue.alpha1(500)
                        }

                        view.blueZnak3.setTextAnimation("${it.currentPersonalityPlanets[2].gate}.${it.currentPersonalityPlanets[2].line}") {
                            view.znak3Blue.alpha1(500)
                        }

                        view.blueZnak4.setTextAnimation("${it.currentPersonalityPlanets[3].gate}.${it.currentPersonalityPlanets[3].line}") {
                            view.znak4Blue.alpha1(500)
                        }

                        view.blueZnak5.setTextAnimation("${it.currentPersonalityPlanets[4].gate}.${it.currentPersonalityPlanets[4].line}") {
                            view.znak5Blue.alpha1(500)
                        }

                        view.blueZnak6.setTextAnimation("${it.currentPersonalityPlanets[5].gate}.${it.currentPersonalityPlanets[5].line}") {
                            view.znak6Blue.alpha1(500)
                        }

                        view.blueZnak7.setTextAnimation("${it.currentPersonalityPlanets[6].gate}.${it.currentPersonalityPlanets[6].line}") {
                            view.znak7Blue.alpha1(500)
                        }

                        view.blueZnak8.setTextAnimation("${it.currentPersonalityPlanets[7].gate}.${it.currentPersonalityPlanets[7].line}") {
                            view.znak8Blue.alpha1(500)
                        }

                        view.blueZnak9.setTextAnimation("${it.currentPersonalityPlanets[8].gate}.${it.currentPersonalityPlanets[8].line}") {
                            view.znak9Blue.alpha1(500)
                        }

                        view.blueZnak10.setTextAnimation("${it.currentPersonalityPlanets[9].gate}.${it.currentPersonalityPlanets[9].line}") {
                            view.znak10Blue.alpha1(500)
                        }

                        view.blueZnak11.setTextAnimation("${it.currentPersonalityPlanets[10].gate}.${it.currentPersonalityPlanets[10].line}") {
                            view.znak11Blue.alpha1(500)
                        }

                        view.blueZnak12.setTextAnimation("${it.currentPersonalityPlanets[11].gate}.${it.currentPersonalityPlanets[11].line}") {
                            view.znak12Blue.alpha1(500)
                        }

                        view.blueZnak13.setTextAnimation("${it.currentPersonalityPlanets[12].gate}.${it.currentPersonalityPlanets[12].line}") {
                            view.znak13Blue.alpha1(500)
                        }
                    }


                    val rotate = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate.repeatCount = Animation.INFINITE
                    rotate.fillAfter = true
                    rotate.duration = 100000
                    rotate.interpolator = LinearInterpolator()

                    val rotateNegative = RotateAnimation(
                        0f,
                        -360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotateNegative.repeatCount = Animation.INFINITE
                    rotateNegative.fillAfter = true
                    rotateNegative.duration = 100000
                    rotateNegative.interpolator = LinearInterpolator()

                    val rotate3 = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate3.repeatCount = Animation.INFINITE
                    rotate3.fillAfter = true
                    rotate3.duration = 100000
                    rotate3.interpolator = LinearInterpolator()

//                    view.circle1.startAnimation(rotate)
//                    view.circle2.startAnimation(rotateNegative)
                    view.circle3.startAnimation(rotate3)
//                    view.circle4.startAnimation(rotateNegative)
//                    view.circle5.startAnimation(rotate)
//                    view.circle6.startAnimation(rotate)
//                    view.circle7.startAnimation(rotateNegative)
//                    view.circle8.startAnimation(rotate)


                    container.addView(view)
                    view
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_channels, null)

                    val channelsAdapter = ChannelsAdapter()
                    view.channelsRecycler.adapter = channelsAdapter

                    baseViewModel.currentTransit.observe(viewLifecycleOwner) {
                        channelsAdapter.createList(it.onlyCurrentChannels)
                    }

                    container.addView(view)
                    view
                }
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
    }

    inner class Handler {

        fun onGatesClicked(v: View) {
            selectGates()
            binding.viewPager.setCurrentItem(0, true)
        }

        fun onChannelsClicked(v: View) {
            selectChannels()
            binding.viewPager.setCurrentItem(1, true)
        }

    }

}