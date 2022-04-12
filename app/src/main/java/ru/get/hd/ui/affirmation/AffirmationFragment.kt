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

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)


    }

    private fun setupViewPager() {
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
                    else -> selectForecasts()
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

        binding.affirmationTitle.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_affirmation_bg
        )

        binding.forecastTitle.background = null
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

        binding.forecastTitle.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_affirmation_bg
        )

        binding.affirmationTitle.background = null
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
                    view.forecastNextTitle.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    baseViewModel.currentForecast.observe(viewLifecycleOwner) {
                        view.forecastText.text =
                            if (App.preferences.locale == "ru") it.ru
                            else it.en
                    }


                    view.forecastText.setTextColor(ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    val calendar: Calendar = Calendar.getInstance()

                    when (calendar.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> {
                            view.sun1.isVisible = true
                        }
                        Calendar.TUESDAY -> {
                            view.sun2.isVisible = true

                            view.line1.isVisible = true
                        }
                        Calendar.WEDNESDAY -> {
                            view.sun3.isVisible = true

                            view.line1.isVisible = true
                            view.line2.isVisible = true
                        }
                        Calendar.THURSDAY -> {
                            view.sun4.isVisible = true

                            view.line1.isVisible = true
                            view.line2.isVisible = true
                            view.line3.isVisible = true
                        }
                        Calendar.FRIDAY -> {
                            view.sun5.isVisible = true

                            view.line1.isVisible = true
                            view.line2.isVisible = true
                            view.line3.isVisible = true
                            view.line4.isVisible = true
                        }
                        Calendar.SATURDAY -> {
                            view.sun6.isVisible = true

                            view.line1.isVisible = true
                            view.line2.isVisible = true
                            view.line3.isVisible = true
                            view.line4.isVisible = true
                            view.line5.isVisible = true
                        }
                        Calendar.SUNDAY -> {
                            view.sun7.isVisible = true

                            view.line1.isVisible = true
                            view.line2.isVisible = true
                            view.line3.isVisible = true
                            view.line4.isVisible = true
                            view.line5.isVisible = true
                            view.line6.isVisible = true
                        }
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

        fun onAffirmationsClicked(v: View) {
            selectAffirmations()
            binding.viewPager.setCurrentItem(0, true)
        }

        fun onForecastsClicked(v: View) {
            selectForecasts()
            binding.viewPager.setCurrentItem(1, true)
        }

    }

}