package ru.get.hd.ui.transit

import android.annotation.SuppressLint
import android.content.Context
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
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import kotlinx.android.synthetic.main.item_transit_advice.view.*
import kotlinx.android.synthetic.main.item_transit_channels.view.*
import kotlinx.android.synthetic.main.item_transit_cycles.view.*
import kotlinx.android.synthetic.main.item_transit_gates.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentTransitBinding
import ru.get.hd.event.HelpType
import ru.get.hd.event.ShowHelpEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.repo.AppDatabase
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.transit.adapter.ChannelsAdapter
import ru.get.hd.ui.transit.adapter.CyclesAdapter
import ru.get.hd.ui.transit.adapter.GatesAdapter
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel

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

        private fun buildTransitFragment() = TransitFragment()
    }

    override fun onResume() {
        super.onResume()

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
        binding.icInfo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

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

        binding.transitsTitle.text = App.resourcesProvider.getStringLocale(R.string.transits_title)
        binding.cyclesTitle.text = App.resourcesProvider.getStringLocale(R.string.cycles_title)
        binding.adviceTitle.text = App.resourcesProvider.getStringLocale(R.string.advice_title)

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

        selectTransits()
        setupViewPager()

        if (!App.preferences.transitHelpShown)
            showTransitHelp()
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = getViewPagerAdapter()
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

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
        binding.icInfo.isVisible = true

        binding.transitsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.cyclesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.adviceTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.transitsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.cyclesTitle.background = null
        binding.adviceTitle.background = null
    }

    private fun selectCycles() {
        binding.icInfo.isVisible = false

        binding.cyclesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.transitsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.adviceTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.cyclesTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.transitsTitle.background = null
        binding.adviceTitle.background = null
    }

    private fun selectAdvice() {
        binding.icInfo.isVisible = false

        binding.adviceTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.transitsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.cyclesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

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

        @SuppressLint("SetTextI18n")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return when (position) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_gates, null)

                    val gatesAdapter = GatesAdapter()
                    view.gatesRecycler.adapter = gatesAdapter

                    val channelsAdapter = ChannelsAdapter()
                    view.channelsRecycler.adapter = channelsAdapter

                    view.gatesTitle.text = App.resourcesProvider.getStringLocale(R.string.gates_title)
                    view.channelsTitle.text = App.resourcesProvider.getStringLocale(R.string.channels_title)

                    view.gatesTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.channelsTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.transitTitle.text = App.resourcesProvider.getStringLocale(R.string.transit_title)
                    view.designTitle.text = App.resourcesProvider.getStringLocale(R.string.design_title)

                    view.nextTransitTitle.text = App.resourcesProvider.getStringLocale(R.string.next_transit_text)
                    view.nextTransitTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    baseViewModel.currentTransit.observe(viewLifecycleOwner) {
                        gatesAdapter.createList(it.onlyCurrentGates.sortedBy { it.number })
                        channelsAdapter.createList(it.onlyCurrentChannels)

                        view.channelsTitle.isVisible = !it.onlyCurrentChannels.isNullOrEmpty()
                        view.gatesTitle.isVisible = !it.onlyCurrentGates.isNullOrEmpty()

                        view.emptyText.isVisible = it.onlyCurrentChannels.isNullOrEmpty() && it.onlyCurrentGates.isNullOrEmpty()
                        view.emptyText.setTextColor(ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        ))
                        view.emptyText.text = App.resourcesProvider.getStringLocale(R.string.transit_no_channels)

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
                    rotate.duration = 50000
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
                    rotateNegative.duration = 50000
                    rotateNegative.interpolator = LinearInterpolator()

                    val rotate1 = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate1.repeatCount = Animation.INFINITE
                    rotate1.fillAfter = true
                    rotate1.duration = 50000
                    rotate1.interpolator = LinearInterpolator()
                    view.circle1.startAnimation(rotate1)

                    val rotate2 = RotateAnimation(
                        0f,
                        -360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate2.repeatCount = Animation.INFINITE
                    rotate2.fillAfter = true
                    rotate2.duration = 50000
                    rotate2.interpolator = LinearInterpolator()
                    view.circle2.startAnimation(rotate2)

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
                    rotate3.duration = 50000
                    rotate3.interpolator = LinearInterpolator()
                    view.circle3.startAnimation(rotate3)

                    val rotate4 = RotateAnimation(
                        0f,
                        -360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate4.repeatCount = Animation.INFINITE
                    rotate4.fillAfter = true
                    rotate4.duration = 50000
                    rotate4.interpolator = LinearInterpolator()
                    view.circle4.startAnimation(rotate4)

                    val rotate5 = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate5.repeatCount = Animation.INFINITE
                    rotate5.fillAfter = true
                    rotate5.duration = 50000
                    rotate5.interpolator = LinearInterpolator()
                    view.circle5.startAnimation(rotate5)

                    val rotate6 = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate6.repeatCount = Animation.INFINITE
                    rotate6.fillAfter = true
                    rotate6.duration = 50000
                    rotate6.interpolator = LinearInterpolator()
                    view.circle6.startAnimation(rotate6)

                    val rotate7 = RotateAnimation(
                        0f,
                        -360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate7.repeatCount = Animation.INFINITE
                    rotate7.fillAfter = true
                    rotate7.duration = 50000
                    rotate7.interpolator = LinearInterpolator()
                    view.circle7.startAnimation(rotate7)

                    val rotate8 = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate8.repeatCount = Animation.INFINITE
                    rotate8.fillAfter = true
                    rotate8.duration = 50000
                    rotate8.interpolator = LinearInterpolator()
                    view.circle8.startAnimation(rotate8)
//                    view.circle8.startAnimation(rotate)


                    container.addView(view)
                    view
                }
                1 -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_cycles, null)

                    view.cyclesTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.cyclesSubtitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.cyclesTitle.text = App.resourcesProvider.getStringLocale(R.string.transit_cycles_title)
                    view.cyclesSubtitle.text = App.resourcesProvider.getStringLocale(R.string.transit_cycles_desc)

                    val cyclesAdapter = CyclesAdapter()
                    view.cyclesRecycler.adapter = cyclesAdapter

                    baseViewModel.currentCycles.observe(viewLifecycleOwner) {
                        cyclesAdapter.createList(it)
                    }

                    container.addView(view)
                    view
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.item_transit_advice, null)

                    baseViewModel.currentDailyAdvice.observe(viewLifecycleOwner) {
                        view.adviceTitle.text =
                            if (App.preferences.locale == "ru") it.titleRu
                            else it.titleEn

                        view.adviceText.text =
                            if (App.preferences.locale == "ru") it.textRu
                            else it.textEn
                    }

                    view.adviceSubtitle.text = App.resourcesProvider.getStringLocale(R.string.daily_advice_subtitle)

                    view.adviceBlock.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_daily_advice_dark
                        else R.drawable.bg_daily_advice_light
                    )

                    view.adviceTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.adviceText.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.adviceSubtitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

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
            router.navigateTo(
                Screens.faqDetailScreen(
                title = if (App.preferences.locale == "ru") baseViewModel.faqsList[7].titleRu
                else baseViewModel.faqsList[7].titleEn,
                desc = if (App.preferences.locale == "ru") baseViewModel.faqsList[7].textRu
                else baseViewModel.faqsList[7].textEn
            ))
        }

    }

}