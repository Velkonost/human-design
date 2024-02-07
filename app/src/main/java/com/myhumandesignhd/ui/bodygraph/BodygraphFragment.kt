package com.myhumandesignhd.ui.bodygraph

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.amplitude.api.Amplitude
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.play.core.review.ReviewManagerFactory
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentBodygraphBinding
import com.myhumandesignhd.event.BodygraphCenterClickEvent
import com.myhumandesignhd.event.HelpType
import com.myhumandesignhd.event.OpenBodygraphEvent
import com.myhumandesignhd.event.SetupNavMenuEvent
import com.myhumandesignhd.event.ShowHelpEvent
import com.myhumandesignhd.event.UpdateBalloonBgStateEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.util.convertDpToPx
import com.myhumandesignhd.util.ext.alpha1
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.util.ext.setTextAnimation07
import com.myhumandesignhd.vm.BaseViewModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.item_transit_advice.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class BodygraphFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphBinding>(
    R.layout.fragment_bodygraph,
    BodygraphViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    private val fromStart: Boolean by lazy {
        arguments?.getBoolean("fromStart") ?: false
    }

    private val needUpdateNavMenu: Boolean by lazy {
        arguments?.getBoolean("needUpdateNavMenu") ?: false
    }

    private val nextYearSheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.nextYearBottomSheet.bottomSheetContainer)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupUserData()
        setupZnaks()
        setupNextYearBottomSheet()

        if (needUpdateNavMenu)
            EventBus.getDefault().post(SetupNavMenuEvent())

        EventBus.getDefault().post(OpenBodygraphEvent())

        isFirstFragmentLaunch = false
        Amplitude.getInstance().logEvent("tab1_screen_shown")

        if (App.preferences.showAskReview) {
            requestReviewFlow()
            App.preferences.showAskReview = false
        }
    }

    private fun requestReviewFlow() {
        val reviewManager = ReviewManagerFactory.create(requireActivity())
        val requestReviewFlow = reviewManager.requestReviewFlow()

        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {
                }
            } else {
            }
        }
    }

    override fun onPause() {
        binding.bigCircle.clearAnimation()
        binding.midCircle.clearAnimation()

        binding.bigCircle.requestLayout()
        binding.midCircle.requestLayout()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))

        if (
            !App.preferences.isDarkTheme
            && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
        ) {
            binding.bigCircle.clearAnimation()
            binding.midCircle.clearAnimation()

            binding.bigCircle.requestLayout()
            binding.midCircle.requestLayout()

            binding.bigCircle.setImageResource(R.drawable.ic_circle_big_light)
            binding.midCircle.setImageResource(R.drawable.ic_circle_mid_light)

            binding.bigCircle.isVisible = true
            binding.midCircle.isVisible = true

            val rotate = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            rotate.startOffset = 0
            rotate.repeatCount = 1
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
            rotateNegative.startOffset = 0
            rotateNegative.repeatCount = 1//Animation.INFINITE
            rotateNegative.fillAfter = true
            rotateNegative.duration = 100000
            rotateNegative.interpolator = LinearInterpolator()

            binding.bigCircle.startAnimation(rotate)
//            rotate.setAnimationListener(object  : Animation.AnimationListener {
//                override fun onAnimationStart(animation: Animation?) {
//
//                }
//
//                override fun onAnimationEnd(animation: Animation?) {
//                    binding.bigCircle.startAnimation(rotate)
//                }
//
//                override fun onAnimationRepeat(animation: Animation?) {
//                }
//
//            })
            binding.midCircle.startAnimation(rotateNegative)
        }
    }

    @Subscribe
    fun onShowHelpEvent(e: ShowHelpEvent) {
        showHelp(e.type)
    }

    private fun startBodygraphZoomAnimation() {
        binding.bodygraphView.scaleXY(1f, 1f, 1000) {
            App.isBodygraphAnimationEnded = true
            binding.bodygraphView.changeIsAllowDrawLinesState(true)
            showZnaksBlock()

            android.os.Handler().postDelayed({
                if (fromStart)
                    android.os.Handler().postDelayed({
                        showHelp(HelpType.BodygraphCenters)
                    }, 2000)
                else if (!App.preferences.isPremiun)
                    if (isAdded) {
                        router.navigateTo(Screens.paywallScreen(source = "bodygraph"))
                    }
            }, 3000)
        }
    }

    private fun showZnaksBlock() {
        binding.designBlock.alpha1(500)
        binding.transitBlock.alpha1(500)
    }

    private fun showHelp(type: HelpType) {
        if (type == HelpType.BodygraphCenters) {
            if (!App.preferences.bodygraphCentersHelpShown)
                showCentersHelp()
            else showHelp(HelpType.BodygraphAddDiagram)
        } else if (type == HelpType.BodygraphAddDiagram) {
            if (!App.preferences.bodygraphAddDiagramHelpShown)
                showAddDiagramHelp()
        }
    }

    private fun showAddDiagramHelp() {
        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(0.9f)
            .setTextGravity(Gravity.CENTER)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_add_diagram))
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
                App.preferences.bodygraphAddDiagramHelpShown = true
                EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))
            }
            .build()

        balloon.showAlignBottom(
            binding.icAddUser,
            xOff = -requireContext().convertDpToPx(112f).toInt()
        )
    }

    private fun setupNextYearBottomSheet() {
//        binding.nextYearBlock.visibility =
//            if (!App.preferences.isNextYearShown) View.VISIBLE else View.INVISIBLE
//
//        val sheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    binding.nextYearBlock.visibility = View.INVISIBLE
//                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
//                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
//                }
//            }
//        }
//        nextYearSheetBehavior.addBottomSheetCallback(sheetCallback)

//        val adapter = NextYearAdapter()
//        binding.nextYearBottomSheet.nextYearRecycler.adapter = adapter
//        binding.nextYearBottomSheet.gradient.background = ContextCompat.getDrawable(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.drawable.gradient_next_year_dark
//            else R.drawable.gradient_next_year_light
//        )
//        adapter.create(requireContext(), recyclerView = binding.nextYearBottomSheet.nextYearRecycler)
//
//        binding.nextYearBottomSheet.closeSheetBtn.setOnClickListener {
//            nextYearSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            YandexMetrica.reportEvent("tab1_forecast_close_clicked")
//            Amplitude.getInstance().logEvent("tab1_forecast_close_clicked")
//        }
//
//        var range: Int = binding.nextYearBottomSheet.nextYearRecycler.computeVerticalScrollRange()
//
//        binding.nextYearBottomSheet.nextYearRecycler.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val recycler = binding.nextYearBottomSheet.nextYearRecycler
//                val offset: Int = recycler.computeVerticalScrollOffset()
//
//
//                range = binding.nextYearBottomSheet.nextYearRecycler.computeVerticalScrollRange()
//
//                val extent: Int = recycler.computeVerticalScrollExtent()
//
//                val percentage = offset / (range - extent).toFloat()
//                binding.nextYearBottomSheet.nextYearIndicator.isVisible = percentage > 0.02
//                if (dy > 0 && percentage > prevNextYearPercentage) {
//                    binding.nextYearBottomSheet.nextYearIndicator.layoutParams.width =
//                        (getScreenWidth(requireContext()) * percentage).toInt()
//                    binding.nextYearBottomSheet.nextYearIndicator.requestLayout()
//                    prevNextYearPercentage = percentage
//                } else if (dy < 0 && percentage <= prevNextYearPercentage) {
//                    binding.nextYearBottomSheet.nextYearIndicator.layoutParams.width =
//                        (getScreenWidth(requireContext()) * percentage).toInt()
//                    binding.nextYearBottomSheet.nextYearIndicator.requestLayout()
//                    prevNextYearPercentage = percentage
//                }
//            }
//        })
    }

    private var prevNextYearPercentage = 0f

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun setupUserData() {
        if (baseViewModel.isCurrentUserInitialized()) {
            binding.userName.text = baseViewModel.currentUser.name
        }

        baseViewModel.currentUserSetupEvent.observe(viewLifecycleOwner) {
            if (it) {
                if (isFirstFragmentLaunch)
                    binding.userName.setTextAnimation(baseViewModel.currentUser.name)
                else binding.userName.text = baseViewModel.currentUser.name
            }
        }

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            if (it.typeRu.isNotEmpty() && it.line.isNotEmpty() && it.profileRu.isNotEmpty()) {
                if (isFirstFragmentLaunch) {
                    binding.subtitle1.setTextAnimation07(
                        "${if (App.preferences.locale == "ru") it.typeRu else if (App.preferences.locale == "es") it.typeEs else it.typeEn} • " +
                                "${it.line} •<br>" +
                                "${if (App.preferences.locale == "ru") it.profileRu else if (App.preferences.locale == "es") it.profileEs else it.profileEn}"
                    ) {
                        binding.subtitle1.alpha = 0.5f
                    }
                } else {
                    binding.subtitle1.text =
                        "${if (App.preferences.locale == "ru") it.typeRu else if (App.preferences.locale == "es") it.typeEs else it.typeEn} • " +
                                "${it.line} •\n" +
                                "${if (App.preferences.locale == "ru") it.profileRu else if (App.preferences.locale == "es") it.profileEs else it.profileEn}"
                }
            }
        }
    }

    override fun updateThemeAndLocale() {
        // next year
//        binding.nextYearBlock.background = ContextCompat.getDrawable(
//            requireContext(), if (App.preferences.isDarkTheme) R.drawable.bg_next_year_dark
//            else R.drawable.bg_next_year_light
//        )
//
//        val color = ContextCompat.getColor(
//            requireContext(), if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//        binding.nextYearTitle.setTextColor(color)
//        binding.nextYearSubtitle.setTextColor(color)

//        binding.nextYearBottomSheet.backSheet.background = ContextCompat.getDrawable(
//            requireContext(), if (App.preferences.isDarkTheme) R.drawable.bg_sheet_header_dark
//            else R.drawable.bg_sheet_header_light
//        )
//
//        binding.nextYearBottomSheet.sheetContainer.background = ContextCompat.getDrawable(
//            requireContext(), if (App.preferences.isDarkTheme) R.color.darkColor
//            else R.color.lightColor
//        )
//
//        binding.nextYearBottomSheet.sheetTitle.background = ContextCompat.getDrawable(
//            requireContext(), if (App.preferences.isDarkTheme) R.drawable.bg_sheet_header_dark
//            else R.drawable.bg_sheet_header_light
//        )
//
//        binding.nextYearBottomSheet.sheetTitle.setTextColor(color)
//        binding.nextYearBottomSheet.bottomSheetContainer.setBackgroundColor(
//            ContextCompat.getColor(
//                requireContext(), if (!App.preferences.isDarkTheme) R.color.lightColor
//                else R.color.darkColor
//            )
//        )
//
//        binding.nextYearBottomSheet.icSheetCross.imageTintList = ColorStateList.valueOf(color)
//

        binding.bodygraphContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.userName.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.subtitle1.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.icAddUser.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.icSettings.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

//        first
        if (isFirstFragmentLaunch) {
            binding.designTitle.text = App.resourcesProvider.getStringLocale(R.string.design_title)
            binding.transitTitle.text =
                App.resourcesProvider.getStringLocale(R.string.personality_title)
            binding.toDecryptionText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.to_decryption_text))
        } else {
            binding.designTitle.text = App.resourcesProvider.getStringLocale(R.string.design_title)
            binding.transitTitle.text =
                App.resourcesProvider.getStringLocale(R.string.personality_title)
            binding.toDecryptionText.text =
                App.resourcesProvider.getStringLocale(R.string.to_decryption_text)
        }

        binding.doubleArrow.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.toDecryptionText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setupZnaks() {
        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            binding.bodygraphView.setupData(
                it.design,
                it.personality,
                it.activeCentres,
                it.inactiveCentres,
                isTouchable = true
            )

            if (!App.isBodygraphWithAnimationShown) {
                android.os.Handler().postDelayed({
                    App.isBodygraphWithAnimationShown = true
                    startBodygraphZoomAnimation()
                }, 1000)
            } else {
                if (App.isBodygraphAnimationEnded) {
                    binding.bodygraphView.changeIsDrawLinesWithAnimation(false)
                    binding.bodygraphView.changeIsAllowDrawLinesState(true)

                    binding.designBlock.alpha = 1f
                    binding.transitBlock.alpha = 1f

                    binding.bodygraphView.scaleX = 1f
                    binding.bodygraphView.scaleY = 1f
                }
            }

            if (!it.design.planets.isNullOrEmpty()) {
                binding.rightZnak1.text =
                    "${it.design.planets[0].gate}.${it.design.planets[0].line}"

                binding.rightZnak2.text =
                    "${it.design.planets[1].gate}.${it.design.planets[1].line}"
                binding.rightZnak3.text =
                    "${it.design.planets[2].gate}.${it.design.planets[2].line}"
                binding.rightZnak4.text =
                    "${it.design.planets[3].gate}.${it.design.planets[3].line}"
                binding.rightZnak5.text =
                    "${it.design.planets[4].gate}.${it.design.planets[4].line}"
                binding.rightZnak6.text =
                    "${it.design.planets[5].gate}.${it.design.planets[5].line}"
                binding.rightZnak7.text =
                    "${it.design.planets[6].gate}.${it.design.planets[6].line}"
                binding.rightZnak8.text =
                    "${it.design.planets[7].gate}.${it.design.planets[7].line}"
                binding.rightZnak9.text =
                    "${it.design.planets[8].gate}.${it.design.planets[8].line}"
                binding.rightZnak10.text =
                    "${it.design.planets[9].gate}.${it.design.planets[9].line}"
                binding.rightZnak11.text =
                    "${it.design.planets[10].gate}.${it.design.planets[10].line}"
                binding.rightZnak12.text =
                    "${it.design.planets[11].gate}.${it.design.planets[11].line}"
                binding.rightZnak13.text =
                    "${it.design.planets[12].gate}.${it.design.planets[12].line}"
            }

            if (!it.personality.planets.isNullOrEmpty()) {
                binding.blueZnak1.text =
                    "${it.personality.planets[0].gate}.${it.personality.planets[0].line}"
                binding.blueZnak2.text =
                    "${it.personality.planets[1].gate}.${it.personality.planets[1].line}"
                binding.blueZnak3.text =
                    "${it.personality.planets[2].gate}.${it.personality.planets[2].line}"
                binding.blueZnak4.text =
                    "${it.personality.planets[3].gate}.${it.personality.planets[3].line}"
                binding.blueZnak5.text =
                    "${it.personality.planets[4].gate}.${it.personality.planets[4].line}"
                binding.blueZnak6.text =
                    "${it.personality.planets[5].gate}.${it.personality.planets[5].line}"
                binding.blueZnak7.text =
                    "${it.personality.planets[6].gate}.${it.personality.planets[6].line}"
                binding.blueZnak8.text =
                    "${it.personality.planets[7].gate}.${it.personality.planets[7].line}"
                binding.blueZnak9.text =
                    "${it.personality.planets[8].gate}.${it.personality.planets[8].line}"
                binding.blueZnak10.text =
                    "${it.personality.planets[9].gate}.${it.personality.planets[9].line}"
                binding.blueZnak11.text =
                    "${it.personality.planets[10].gate}.${it.personality.planets[10].line}"
                binding.blueZnak12.text =
                    "${it.personality.planets[11].gate}.${it.personality.planets[11].line}"
                binding.blueZnak13.text =
                    "${it.personality.planets[12].gate}.${it.personality.planets[12].line}"
            }
        }
    }

    private fun showCentersHelp() {
        if (!isAdded) return

        runCatching {
            val view = View(
                requireContext()
            )
            view.layoutParams = LinearLayout.LayoutParams(
                1,
                1
            )
            view.x =
                binding.bodygraphContainer2.width / 2f//binding.bodygraphView.getTopPoint().x.toFloat()
            view.y =
                binding.bodygraphContainer2.top + binding.bodygraphView.getTopPoint().y.toFloat()

            binding.bodygraphContainer.addView(view)

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
                .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_centers))
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
                    App.preferences.bodygraphCentersHelpShown = true
                    showHelp(HelpType.BodygraphAddDiagram)
                }
                .build()


            balloon.showAlignTop(view)
        }
    }

    @Subscribe
    fun onBodygraphCenterClickEvent(e: BodygraphCenterClickEvent) {
        if (!isAdded) return

        YandexMetrica.reportEvent("UserClickedBodygraphCenter")

        val view = View(requireContext())
        view.layoutParams = LinearLayout.LayoutParams(
            1,
            1
        )
        view.x =
            if (e.isXCenter) binding.bodygraphContainer2.width / 2f
            else if (e.isLeftTriangle || e.isRightTriangle) binding.bodygraphView.left + e.x
            else e.x
        view.y = e.y

        binding.bodygraphContainer2.addView(view)

        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(e.arrowPosition)
            .setTextGravity(Gravity.START)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText("<small><strong>${e.title}</strong></small><br>${e.desc}")
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                EventBus.getDefault().post(UpdateBalloonBgStateEvent(false))
//                binding.balloonBg.isVisible = false
            }
            .build()

        if (e.alignTop) balloon.showAlignTop(view, xOff = e.xOffset)
        else balloon.showAlignBottom(view, xOff = e.xOffset)

        EventBus.getDefault().post(UpdateBalloonBgStateEvent(true))
//        binding.balloonBg.isVisible = true
    }

    inner class Handler {

        fun onAddUserClicked(v: View) {
            YandexMetrica.reportEvent("Tab1AddUserTapped")
            Amplitude.getInstance().logEvent("tab1TappedShowUsers")

            router.navigateTo(
                if (App.preferences.isPremiun) Screens.diagramScreen()
                else Screens.paywallScreen(source = "adduser")
            )
        }

        fun onSettingsClicked(v: View) {
            YandexMetrica.reportEvent("Tab1SettingsTapped")
            Amplitude.getInstance().logEvent("tab1TappedSettings")

            router.navigateTo(Screens.settingsScreen())
        }

        fun onNextYearClicked(v: View) {
            YandexMetrica.reportEvent("tab1_forecast_click")
            Amplitude.getInstance().logEvent("tab1_forecast_click")

            if (!App.preferences.isPremiun) {
                if (isAdded) {
                    router.navigateTo(Screens.paywallScreen(source = "bodygraph", fromStart = true))
                }
            } else {
                App.preferences.isNextYearShown = true

                nextYearSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                YandexMetrica.reportEvent("tab1_forecast_shown")
                Amplitude.getInstance().logEvent("tab1_forecast_shown")
            }
        }
    }

}