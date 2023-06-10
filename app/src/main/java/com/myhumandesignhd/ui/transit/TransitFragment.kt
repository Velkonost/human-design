package com.myhumandesignhd.ui.transit

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.amplitude.api.Amplitude
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentTransitNewBinding
import com.myhumandesignhd.event.OpenChannelItemEvent
import com.myhumandesignhd.event.OpenCycleItemEvent
import com.myhumandesignhd.event.OpenGateItemEvent
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
import org.greenrobot.eventbus.Subscribe

class TransitFragment : BaseFragment<TransitViewModel, FragmentTransitNewBinding>(
    R.layout.fragment_transit_new,
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

    private val sheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.aboutBottomSheet.bottomSheetContainer)
    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.instance.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        Amplitude.getInstance().logEvent("tab3_screen_shown")

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

        binding.container.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.transitsTitle.text = App.resourcesProvider.getStringLocale(R.string.transits_title)
        binding.cyclesTitle.text = App.resourcesProvider.getStringLocale(R.string.cycles_title)
        binding.adviceTitle.text = App.resourcesProvider.getStringLocale(R.string.advice_title)

        binding.selectionBlock.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_selection_block_dark
            else R.drawable.bg_selection_block_light
        )

        selectTransits()
        setupViewPager()

        if (!App.preferences.transitHelpShown)
            showTransitHelp()

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
    fun onOpenGateItemEvent(e: OpenGateItemEvent) {
        if (!isAdded) return

        showSheet(e.gate.title, e.gate.description)

        with(binding.aboutBottomSheet) {
            designBlock.isVisible = false
            transitBlock.isVisible = false
            bodygraphTitle.isVisible = false
            bodygraphView.isVisible = false
        }
    }

    @Subscribe
    fun onOpenChannelItemEvent(e: OpenChannelItemEvent) {
        if (!isAdded) return

        showSheet(e.channel.title, e.channel.description)

        with(binding.aboutBottomSheet) {
            designBlock.isVisible = false
            transitBlock.isVisible = false
            bodygraphTitle.isVisible = false
            bodygraphView.isVisible = false
        }
    }


    @Subscribe
    fun onOpenCycleItemEvent(e: OpenCycleItemEvent) {
        if (!isAdded) return

        showSheet(
            when (App.preferences.locale) {
                "ru" -> e.cycle.nameRu
                else -> e.cycle.nameEn
            },
            when (App.preferences.locale) {
                "ru" -> e.cycle.descriptionRu
                else -> e.cycle.descriptionEn
            }
        )

        with(binding.aboutBottomSheet) {
            designBlock.isVisible = false
            transitBlock.isVisible = false
            bodygraphTitle.isVisible = false
            bodygraphView.isVisible = false
        }
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

        binding.transitsTitle.setTextColor(ContextCompat.getColor(
                requireContext(), R.color.lightColor
        ))

        binding.cyclesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            )
        )

        binding.adviceTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            )
        )

        binding.transitsTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_transit
        )

        binding.cyclesTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_inactive_item_transit
        )
        binding.adviceTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_inactive_item_transit
        )
    }

    private fun selectCycles() {
        YandexMetrica.reportEvent("Tab3CyclesTapped")
        Amplitude.getInstance().logEvent("tab3TappedCycles")

        binding.icInfo.isVisible = false

        binding.cyclesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.lightColor
            )
        )

        binding.transitsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            )
        )

        binding.adviceTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            )
        )

        binding.cyclesTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_transit
        )

        binding.transitsTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_inactive_item_transit
        )
        binding.adviceTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_inactive_item_transit
        )
    }

    private fun selectAdvice() {
        YandexMetrica.reportEvent("Tab3AdviceTapped")
        Amplitude.getInstance().logEvent("tab3TappedDailyAdvice")

        binding.icInfo.isVisible = false

        binding.adviceTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.lightColor
            )
        )

        binding.transitsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            )
        )

        binding.cyclesTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor0_5
                else R.color.darkColor0_5
            )
        )

        binding.adviceTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_transit
        )

        binding.transitsTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_inactive_item_transit
        )
        binding.cyclesTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_inactive_item_transit
        )
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
                            it.onlyCurrentGates.sortedBy { it.number },
                            it.onlyCurrentChannels,
                            view.channelsRecycler,
                            it.birthDesignPlanets,
                            it.birthPersonalityPlanets,
                            it.currentDesignPlanets,
                            it.currentPersonalityPlanets,
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
                        view.adviceTitle.text =
                            when (App.preferences.locale) {
                                "ru" -> it.titleRu
                                "es" -> it.titleEs
                                else -> it.titleEn
                            }

                        view.adviceText.text =
                            when (App.preferences.locale) {
                                "ru" -> it.textRu
                                "es" -> it.textEs
                                else -> it.textEn
                            }

                        val paint = view.adviceTitle.paint
                        val width = paint.measureText(view.adviceTitle.text.toString())
                        val textShader: Shader = LinearGradient(0f, 0f, width, view.adviceTitle.textSize, intArrayOf(
                            Color.parseColor("#58B9FF"), Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
                        ), null, Shader.TileMode.REPEAT)
                        view.adviceTitle.paint.shader = textShader
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
                    title = when (App.preferences.locale) {
                        "ru" -> baseViewModel.faqsList[7].titleRu
                        "es" -> baseViewModel.faqsList[7].titleEs
                        else -> baseViewModel.faqsList[8].titleEn
                    },
                    desc = when (App.preferences.locale) {
                        "ru" -> baseViewModel.faqsList[7].textRu
                        "es" -> baseViewModel.faqsList[7].textEs
                        else -> baseViewModel.faqsList[8].textEn
                    }
                )
            )
        }

    }

}