package com.myhumandesignhd.ui.transit

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentTransitBinding
import com.myhumandesignhd.event.SelectNavItemEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.transit.adapter.CyclesAdapter
import com.myhumandesignhd.ui.transit.adapter.TransitAdapter
import com.myhumandesignhd.util.convertDpToPx
import com.myhumandesignhd.vm.BaseViewModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.item_transit_advice.view.*
import kotlinx.android.synthetic.main.item_transit_cycles.view.*
import kotlinx.android.synthetic.main.item_transit_gates.view.*
import org.greenrobot.eventbus.EventBus

class TransitFragment : BaseFragment<TransitViewModel, FragmentTransitBinding>(
    R.layout.fragment_transit,
    TransitViewModel::class,
    Handler::class
) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: TransitFragment? = null

        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK) {
            instance ?: buildTransitFragment()
                .also { instance = it }
        }

        fun reset() {
            instance = null
        }

        private fun buildTransitFragment() = TransitFragment()
    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().post(SelectNavItemEvent(itemPosition = 2))
        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.instance.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        Amplitude.getInstance().logEvent("tab3_screen_shown")
    }

    private fun showTransitHelp() {
        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(0.9f)
            .setTextGravity(Gravity.CENTER)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_transit))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setIsVisibleOverlay(true)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                App.preferences.transitHelpShown = true
            }
            .build()


        balloon.showAlignBottom(
            binding.icInfo,
            yOff = requireContext().convertDpToPx(10f).toInt(),
            xOff = -requireContext().convertDpToPx(110f).toInt()
        )
    }

    override fun updateThemeAndLocale() {
        binding.icInfo.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.transitContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.transitTitle.text =
            App.resourcesProvider.getStringLocale(R.string.daily_transits_title)
        binding.transitTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.transitsTitle.text = App.resourcesProvider.getStringLocale(R.string.transits_title)
        binding.cyclesTitle.text = App.resourcesProvider.getStringLocale(R.string.cycles_title)
        binding.adviceTitle.text = App.resourcesProvider.getStringLocale(R.string.advice_title)

        binding.selectionCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            )
        )

        binding.selectionLinear.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            )
        )

        selectTransits()
        setupViewPager()

        if (!App.preferences.transitHelpShown)
            showTransitHelp()
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
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
                    0 -> selectTransits()
                    1 -> selectCycles()
                    else -> selectAdvice()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun selectTransits() {
        YandexMetrica.reportEvent("Tab3TransitsTapped")
        Amplitude.getInstance().logEvent("tab3TappedTransits")

        binding.icInfo.isVisible = true

        binding.transitsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.cyclesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            )
        )

        binding.adviceTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            )
        )

        binding.transitsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.cyclesTitle.background = null
        binding.adviceTitle.background = null
    }

    private fun selectCycles() {
        YandexMetrica.reportEvent("Tab3CyclesTapped")
        Amplitude.getInstance().logEvent("tab3TappedCycles")

        binding.icInfo.isVisible = false

        binding.cyclesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.transitsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            )
        )

        binding.adviceTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            )
        )

        binding.cyclesTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.transitsTitle.background = null
        binding.adviceTitle.background = null
    }

    private fun selectAdvice() {
        YandexMetrica.reportEvent("Tab3AdviceTapped")
        Amplitude.getInstance().logEvent("tab3TappedDailyAdvice")

        binding.icInfo.isVisible = false

        binding.adviceTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.transitsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            )
        )

        binding.cyclesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.unselectText
            )
        )

        binding.adviceTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.transitsTitle.background = null
        binding.cyclesTitle.background = null
    }

    private fun getViewPagerAdapter(): PagerAdapter = object : PagerAdapter() {

        override fun getCount(): Int {
            return 3
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return when (position) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_gates, null)

                    val transitAdapter = TransitAdapter()
                    view.channelsRecycler.adapter = transitAdapter

                    baseViewModel.currentTransit.observe(viewLifecycleOwner) {
                        transitAdapter.createList(
                            it.data.onlyCurrentGates.sortedBy { it.number },
                            it.data.onlyCurrentChannels,
                            view.channelsRecycler,
                            it.data.birthDesignPlanets,
                            it.data.birthPersonalityPlanets,
                            it.data.currentDesignPlanets,
                            it.data.currentPersonalityPlanets,
                        )
                    }

                    container.addView(view)
                    view
                }

                1 -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_cycles, null)

                    val cyclesAdapter = CyclesAdapter()
                    view.cyclesRecycler.adapter = cyclesAdapter

                    baseViewModel.currentCycles.observe(viewLifecycleOwner) {
                        cyclesAdapter.createList(it, view.cyclesRecycler)
                    }

                    container.addView(view)
                    view
                }

                else -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_advice, null)

                    baseViewModel.currentDailyAdvice.observe(viewLifecycleOwner) {
                        view.adviceTitle.text = it.title

                        view.adviceText.text = it.text
                    }

                    view.adviceSubtitle.text =
                        App.resourcesProvider.getStringLocale(R.string.daily_advice_subtitle)

                    view.adviceBlock.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_daily_advice_dark
                        else R.drawable.bg_daily_advice_light
                    )

                    view.adviceTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    view.adviceText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    view.adviceSubtitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    view.adviceIcon.setImageResource(
                        if (App.preferences.isDarkTheme) R.drawable.ic_advice_icon_dark
                        else R.drawable.ic_advice_icon_light
                    )

                    view.bigCircle.setImageResource(
                        if (App.preferences.isDarkTheme) R.drawable.ic_circle_big_dark
                        else R.drawable.ic_circle_big_light
                    )

                    view.midCircle.setImageResource(
                        if (App.preferences.isDarkTheme) R.drawable.ic_circle_mid_dark
                        else R.drawable.ic_circle_mid_light
                    )

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

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

                        view.bigCircle.startAnimation(rotate)
                        view.midCircle.startAnimation(rotateNegative)
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

        fun onTransitsClicked(v: View) {
            selectTransits()
            binding.viewPager.setCurrentItem(0, true)
        }

        fun onCyclesClicked(v: View) {
            selectCycles()
            binding.viewPager.setCurrentItem(1, true)
        }

        fun onAdviceClicked(v: View) {
            selectAdvice()
            binding.viewPager.setCurrentItem(2, true)
        }

        fun onFaqClicked(v: View) {
            YandexMetrica.reportEvent("Tab3FAQTapped")

            router.navigateTo(
                Screens.faqDetailScreen(
                    title = baseViewModel.faqsList[7].title,
                    desc = baseViewModel.faqsList[7].text
                )
            )
        }

    }

}