package com.myhumandesignhd.ui.affirmation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.BuildConfig
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentAffirmationBinding
import com.myhumandesignhd.ui.affirmation.adapter.NextYearAdapter
import com.myhumandesignhd.ui.affirmation.ext.shareAffirmation
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.vm.BaseViewModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.item_affirmation.view.affirmationCard
import kotlinx.android.synthetic.main.item_affirmation.view.affirmationIcon
import kotlinx.android.synthetic.main.item_affirmation.view.affirmationNextText
import kotlinx.android.synthetic.main.item_affirmation.view.affirmationText
import kotlinx.android.synthetic.main.item_affirmation.view.shareBtn
import kotlinx.android.synthetic.main.item_forecast.view.forecastNextTitle
import kotlinx.android.synthetic.main.item_forecast.view.forecastText
import kotlinx.android.synthetic.main.item_forecast.view.forecastTitle
import kotlinx.android.synthetic.main.item_forecast.view.line1
import kotlinx.android.synthetic.main.item_forecast.view.line2
import kotlinx.android.synthetic.main.item_forecast.view.line3
import kotlinx.android.synthetic.main.item_forecast.view.line4
import kotlinx.android.synthetic.main.item_forecast.view.line5
import kotlinx.android.synthetic.main.item_forecast.view.line6
import kotlinx.android.synthetic.main.item_forecast.view.line7
import kotlinx.android.synthetic.main.item_next_year.view.nextYearRecycler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class AffirmationFragment : BaseFragment<AffirmationViewModel, FragmentAffirmationBinding>(
    R.layout.fragment_affirmation,
    AffirmationViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        Amplitude.getInstance().logEvent("tab5_screen_shown")
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
                when(position) {
                    0 -> selectAffirmations()
                    1 -> selectForecasts()
                    2 -> selectNextYear()
                    else -> selectInsights()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.viewPager.post {
            android.os.Handler().postDelayed({
                showAffirmationHelp()
            }, 1000)

        }
    }

    override fun updateThemeAndLocale() {
        binding.wisdomTitle.text = App.resourcesProvider.getStringLocale(R.string.menu_fifth_title)
        binding.wisdomTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.affirmationContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.affirmationTitle.text = App.resourcesProvider.getStringLocale(R.string.affirmations_title)
        binding.forecastTitle.text = App.resourcesProvider.getStringLocale(R.string.forecasts_title)
        binding.insightsTitle.text = App.resourcesProvider.getStringLocale(R.string.menu_fifth_title)

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

        selectAffirmations()
        setupViewPager()
    }

    private fun selectAffirmations() {
        YandexMetrica.reportEvent("Tap5AffirmationsTapped")
        Amplitude.getInstance().logEvent("tab5TappedAffirmations")

        binding.affirmationTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.forecastTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.nextYearTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.insightsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.affirmationTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.forecastTitle.background = null
        binding.nextYearTitle.background = null
        binding.insightsTitle.background = null
    }

    private fun selectNextYear() {
        YandexMetrica.reportEvent("tab5_2024_clicked")
        Amplitude.getInstance().logEvent("tab5_2024_clicked")

        binding.nextYearTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.forecastTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.affirmationTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.insightsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.nextYearTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.forecastTitle.background = null
        binding.affirmationTitle.background = null
        binding.insightsTitle.background = null
    }

    private fun selectForecasts() {
        YandexMetrica.reportEvent("Tap5ForecastsTapped")
        Amplitude.getInstance().logEvent("tab5TappedForecasts")

        binding.forecastTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.affirmationTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.nextYearTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.insightsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.forecastTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.affirmationTitle.background = null
        binding.insightsTitle.background = null
        binding.nextYearTitle.background = null
    }

    private fun selectInsights() {
        binding.insightsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.affirmationTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.forecastTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.nextYearTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.insightsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.affirmationTitle.background = null
        binding.forecastTitle.background = null
        binding.nextYearTitle.background = null
    }

    private lateinit var shareBtnView: View

    private fun showAffirmationHelp() {
        if (!App.preferences.affirmationHelpShown) {
            val balloon = Balloon.Builder(context!!)
                .setArrowSize(15)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
                .setArrowPosition(0.5f)
                .setTextGravity(Gravity.CENTER)
                .setPadding(10)
                .setWidth(BalloonSizeSpec.WRAP)
                .setMaxWidth(300)
                .setHeight(BalloonSizeSpec.WRAP)
                .setTextSize(12f)
                .setCornerRadius(10f)
                .setText(App.resourcesProvider.getStringLocale(R.string.help_affirmation))
                .setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.lightColor
                    )
                )
                .setTextIsHtml(true)
                .setOverlayColorResource(R.color.helpBgColor)
                .setOverlayPadding(10f)
                .setIsVisibleOverlay(true)
                .setBackgroundColor(
                    Color.parseColor("#4D494D")
                )
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setOnBalloonDismissListener {
                    App.preferences.affirmationHelpShown = true
                }
                .build()

            balloon.showAlignTop(shareBtnView)
        }
    }

    private fun getViewPagerAdapter(): PagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return 3
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return when (position) {
                0 -> {
                    val view = layoutInflater.inflate(R.layout.item_affirmation, null)

                    view.affirmationNextText.text = App.resourcesProvider.getStringLocale(R.string.next_affirmation_text)
                    view.affirmationNextText.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    baseViewModel.currentAffirmation.observe(viewLifecycleOwner) {
                        view.affirmationText.setTextAnimation(
                            if (App.preferences.locale == "ru") it.ru
                            else if (App.preferences.locale == "es") it.es
                            else it.en
                        )

                        view.affirmationText.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.bg_affirmation_text)

                        glideRequestManager
                            .load(
                                BuildConfig.BASE_URL + "/affirmations/" +
                                    if (App.preferences.isDarkTheme) it.imageD
                                    else it.imageL
                            ).into(view.affirmationIcon)
                    }

                    view.shareBtn.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.affirmation_share))
                    view.shareBtn.setOnClickListener {
                        YandexMetrica.reportEvent("Tab5TappedShareTapped")
                        Amplitude.getInstance().logEvent("tab5TappedShare")

//                        view.shareBtn.isVisible = false
                        GlobalScope.launch {

                            shareAffirmation(view.affirmationCard)

                        }.invokeOnCompletion {
                            requireActivity().runOnUiThread { view.shareBtn.isVisible = true  }

                        }
                    }

                    shareBtnView = view.shareBtn

                    container.addView(view)
                    view
                }
                2 -> {
                    val view = layoutInflater.inflate(R.layout.item_next_year, null)
                    val adapter = NextYearAdapter()
                    view.nextYearRecycler.adapter = adapter
                    adapter.create(requireContext())

                    container.addView(view)
                    view
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.item_forecast, null)

                    view.forecastNextTitle.text = App.resourcesProvider.getStringLocale(R.string.next_forecast_text)

                    view.forecastTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))
                    view.forecastNextTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    baseViewModel.currentForecast.observe(viewLifecycleOwner) {
                        view.forecastTitle.text =
                            if (App.preferences.locale == "ru") it.titleRu
                            else if (App.preferences.locale == "es") it.titleEs
                            else it.titleEn

                        view.forecastText.text =
                            if (App.preferences.locale == "ru") it.ru
                            else if (App.preferences.locale == "es") it.es
                            else it.en
                    }


                    view.forecastText.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    view.line1.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    view.line2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    view.line3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    view.line4.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    view.line5.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    view.line6.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    view.line7.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_forecast_line_inactive_dark
                        else R.drawable.bg_forecast_line_inactive_light
                    )

                    val calendar: Calendar = Calendar.getInstance()

                    when (calendar.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                            }
                        }
                        Calendar.TUESDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                            }
                        }
                        Calendar.WEDNESDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                            }
                        }
                        Calendar.THURSDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                                view.line5.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_fifth
                                )
                            }
                        }
                        Calendar.FRIDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                                view.line5.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_fifth
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                                view.line5.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_fifth
                                )
                                view.line6.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_sixth
                                )
                            }
                        }
                        Calendar.SATURDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                                view.line5.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_fifth
                                )
                                view.line6.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_sixth
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                                view.line5.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_fifth
                                )
                                view.line6.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_sixth
                                )
                                view.line7.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_seventh
                                )
                            }
                        }
                        Calendar.SUNDAY -> {
                            if (App.preferences.locale == "ru") {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                                view.line2.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_second
                                )
                                view.line3.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_third
                                )
                                view.line4.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_forth
                                )
                                view.line5.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_fifth
                                )
                                view.line6.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_sixth
                                )
                                view.line7.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_seventh
                                )
                            } else {
                                view.line1.background = ContextCompat.getDrawable(
                                    requireContext(), R.drawable.bg_forecast_line_first
                                )
                            }
                        }
                    }

                    container.addView(view)
                    view
                }

//                else -> {
//                    val view = layoutInflater.inflate(R.layout.item_forecast, null)
//
//
//                    container.addView(view)
//                    view
//                }
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
    }

    inner class Handler {

        fun onAffirmationsClicked(v: View) {
            selectAffirmations()
            binding.viewPager.setCurrentItem(0, true)
        }

        fun onForecastsClicked(v: View) {
            selectForecasts()
            binding.viewPager.setCurrentItem(1, true)
        }

        fun onInsightsClicked(v: View) {
            selectInsights()
            binding.viewPager.setCurrentItem(2, true)
        }

        fun onNextYearClicked(v: View) {
            selectNextYear()
            binding.viewPager.setCurrentItem(2, true)
        }
    }

}