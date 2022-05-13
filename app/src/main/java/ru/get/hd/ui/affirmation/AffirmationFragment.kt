package ru.get.hd.ui.affirmation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import kotlinx.android.synthetic.main.item_affirmation.view.*
import kotlinx.android.synthetic.main.item_forecast.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.get.hd.App
import ru.get.hd.BuildConfig
import ru.get.hd.R
import ru.get.hd.databinding.FragmentAffirmationBinding
import ru.get.hd.databinding.FragmentTransitBinding
import ru.get.hd.ui.affirmation.ext.shareAffirmation
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.transit.TransitViewModel
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel
import java.util.*

class AffirmationFragment : BaseFragment<AffirmationViewModel, FragmentAffirmationBinding>(
    ru.get.hd.R.layout.fragment_affirmation,
    AffirmationViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
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
        binding.affirmationTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.forecastTitle.setTextColor(ContextCompat.getColor(
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
        binding.insightsTitle.background = null
    }

    private fun selectForecasts() {
        binding.forecastTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.affirmationTitle.setTextColor(ContextCompat.getColor(
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

        binding.insightsTitle.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_section_active_dark
            else R.drawable.bg_section_active_light
        )

        binding.affirmationTitle.background = null
        binding.forecastTitle.background = null
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
            return 2
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
                            else it.en
                        )

                        view.affirmationText.background =
                            if (App.preferences.isDarkTheme) null
                            else ContextCompat.getDrawable(requireContext(), R.drawable.bg_affirmation_text)

                        glideRequestManager
                            .load(BuildConfig.BASE_URL + "/affirmations/" +
                                    if (App.preferences.isDarkTheme) it.imageD
                                    else it.imageL
                            ).into(view.affirmationIcon)
                    }

                    view.shareBtn.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.affirmation_share))
                    view.shareBtn.setOnClickListener {
                        view.shareBtn.isVisible = false
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
                            else it.titleEn

                        view.forecastText.text =
                            if (App.preferences.locale == "ru") it.ru
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
                            view.line1.background = ContextCompat.getDrawable(
                                requireContext(), R.drawable.bg_forecast_line_first
                            )
                        }
                        Calendar.TUESDAY -> {
                            view.line1.background = ContextCompat.getDrawable(
                                requireContext(), R.drawable.bg_forecast_line_first
                            )
                            view.line2.background = ContextCompat.getDrawable(
                                requireContext(), R.drawable.bg_forecast_line_second
                            )
                        }
                        Calendar.WEDNESDAY -> {
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
                        Calendar.THURSDAY -> {
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
                        Calendar.FRIDAY -> {
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
                        Calendar.SATURDAY -> {
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
                        Calendar.SUNDAY -> {
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

    }

}