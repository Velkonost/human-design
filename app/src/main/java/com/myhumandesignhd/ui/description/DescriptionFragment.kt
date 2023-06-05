package com.myhumandesignhd.ui.description

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentDescriptionBinding
import com.myhumandesignhd.event.HelpType
import com.myhumandesignhd.event.OpenAboutItemEvent
import com.myhumandesignhd.event.OpenCenterItemEvent
import com.myhumandesignhd.event.OpenChannelItemEvent
import com.myhumandesignhd.event.OpenGateItemEvent
import com.myhumandesignhd.event.OpenPaywallEvent
import com.myhumandesignhd.event.SetupNavMenuEvent
import com.myhumandesignhd.event.ShowHelpEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.model.AboutItem
import com.myhumandesignhd.model.AboutType
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.description.adapter.DescriptionAdapter
import com.myhumandesignhd.util.ext.alpha1
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DescriptionFragment : BaseFragment<DescriptionViewModel, FragmentDescriptionBinding>(
    R.layout.fragment_description,
    DescriptionViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val sheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.aboutBottomSheet.bottomSheetContainer)
    }

    private val descriptionAdapter: DescriptionAdapter by lazy {
        DescriptionAdapter()
    }

    private val fromStart: Boolean by lazy {
        arguments?.getBoolean("fromStart")?: false
    }

    private val needUpdateNavMenu: Boolean by lazy {
        arguments?.getBoolean("needUpdateNavMenu")?: false
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

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

        if (needUpdateNavMenu)
            EventBus.getDefault().post(SetupNavMenuEvent())

        Amplitude.getInstance().logEvent("tab1_screen_shown")
    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    @Subscribe
    fun onShowHelpEvent(e: ShowHelpEvent) {
        showHelp(e.type)
    }

    override fun updateThemeAndLocale() {
        binding.container.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.title.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.selectionBlock.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_selection_block_dark
            else R.drawable.bg_selection_block_light
        )

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

        binding.aboutTitle.text = App.resourcesProvider.getStringLocale(R.string.about_title)
        binding.centersTitle.text = App.resourcesProvider.getStringLocale(R.string.centers_title)
        binding.gatesTitle.text = App.resourcesProvider.getStringLocale(R.string.gates_title)
        binding.channelsTitle.text = App.resourcesProvider.getStringLocale(R.string.channels_title)

        selectSection(SECTION.ABOUT)
        setupViewPager()
        isFirstFragmentLaunch = false

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
    fun onOpenAboutItemEvent(e: OpenAboutItemEvent) {
        if (!isAdded) return

        showSheet(e.item.title, e.item.text)

        with(binding.aboutBottomSheet) {
            when(e.item.type) {
                AboutType.BODYGRAPH -> {
                    designBlock.isVisible = true
                    transitBlock.isVisible = true

                    baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                        if (it.typeRu.isNotEmpty() && it.line.isNotEmpty() && it.profileRu.isNotEmpty()) {
                            bodygraphTitle.text =
                                "${if (App.preferences.locale == "ru") it.typeRu else if (App.preferences.locale == "es") it.typeEs else it.typeEn} • " +
                                        "${it.line} •\n" +
                                        "${if (App.preferences.locale == "ru") it.profileRu else if (App.preferences.locale == "es") it.profileEs else it.profileEn}"

                        }
                    }

                    baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                        bodygraphView.setupData(
                            it.design,
                            it.personality,
                            it.activeCentres,
                            it.inactiveCentres,
                            isTouchable = true
                        )
                        bodygraphView.isVisible = true

//                                bodygraphView.changeIsDrawLinesWithAnimation(false)
                        bodygraphView.changeIsAllowDrawLinesState(true)

                        designBlock.alpha = 1f
                        transitBlock.alpha = 1f

                        bodygraphView.scaleX = 1f
                        bodygraphView.scaleY = 1f

                        if (!it.design.planets.isNullOrEmpty()) {
                            rightZnak1.text = "${it.design.planets[0].gate}.${it.design.planets[0].line}"
                            rightZnak2.text = "${it.design.planets[1].gate}.${it.design.planets[1].line}"
                            rightZnak3.text = "${it.design.planets[2].gate}.${it.design.planets[2].line}"
                            rightZnak4.text = "${it.design.planets[3].gate}.${it.design.planets[3].line}"
                            rightZnak5.text = "${it.design.planets[4].gate}.${it.design.planets[4].line}"
                            rightZnak6.text = "${it.design.planets[5].gate}.${it.design.planets[5].line}"
                            rightZnak7.text = "${it.design.planets[6].gate}.${it.design.planets[6].line}"
                            rightZnak8.text = "${it.design.planets[7].gate}.${it.design.planets[7].line}"
                            rightZnak9.text = "${it.design.planets[8].gate}.${it.design.planets[8].line}"
                            rightZnak10.text = "${it.design.planets[9].gate}.${it.design.planets[9].line}"
                            rightZnak11.text = "${it.design.planets[10].gate}.${it.design.planets[10].line}"
                            rightZnak12.text = "${it.design.planets[11].gate}.${it.design.planets[11].line}"
                            rightZnak13.text = "${it.design.planets[12].gate}.${it.design.planets[12].line}"
                        }

                        if (!it.personality.planets.isNullOrEmpty()) {
                            blueZnak1.text = "${it.personality.planets[0].gate}.${it.personality.planets[0].line}"
                            blueZnak2.text = "${it.personality.planets[1].gate}.${it.personality.planets[1].line}"
                            blueZnak3.text = "${it.personality.planets[2].gate}.${it.personality.planets[2].line}"
                            blueZnak4.text = "${it.personality.planets[3].gate}.${it.personality.planets[3].line}"
                            blueZnak5.text = "${it.personality.planets[4].gate}.${it.personality.planets[4].line}"
                            blueZnak6.text = "${it.personality.planets[5].gate}.${it.personality.planets[5].line}"
                            blueZnak7.text = "${it.personality.planets[6].gate}.${it.personality.planets[6].line}"
                            blueZnak8.text = "${it.personality.planets[7].gate}.${it.personality.planets[7].line}"
                            blueZnak9.text = "${it.personality.planets[8].gate}.${it.personality.planets[8].line}"
                            blueZnak10.text = "${it.personality.planets[9].gate}.${it.personality.planets[9].line}"
                            blueZnak11.text = "${it.personality.planets[10].gate}.${it.personality.planets[10].line}"
                            blueZnak12.text = "${it.personality.planets[11].gate}.${it.personality.planets[11].line}"
                            blueZnak13.text = "${it.personality.planets[12].gate}.${it.personality.planets[12].line}"
                        }
                    }
                }
                else -> {
                    designBlock.isVisible = false
                    transitBlock.isVisible = false
                    bodygraphTitle.isVisible = false
                    bodygraphView.isVisible = false
                }
            }
        }
    }

    @Subscribe
    fun onOpenCenterItemEvent(e: OpenCenterItemEvent) {
        if (!isAdded) return

        showSheet(e.center.name, e.center.description)

        with(binding.aboutBottomSheet) {
            designBlock.isVisible = false
            transitBlock.isVisible = false
            bodygraphTitle.isVisible = false
            bodygraphView.isVisible = false
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

    private fun startBodygraphZoomAnimation() {
        binding.aboutBottomSheet.bodygraphView.scaleXY(1f, 1f, 1000) {
            App.isBodygraphAnimationEnded = true
            binding.aboutBottomSheet.bodygraphView.changeIsAllowDrawLinesState(true)
            binding.aboutBottomSheet.designBlock.alpha1(500)
            binding.aboutBottomSheet.transitBlock.alpha1(500)

//            android.os.Handler().postDelayed({
//                if (fromStart)
//                    android.os.Handler().postDelayed({
//                        showHelp(HelpType.BodygraphCenters)
//                    }, 2000)
//                else if (!App.preferences.isPremiun)
//                    if (isAdded) {
//                        router.navigateTo(Screens.paywallScreen(source = "bodygraph"))
//                    }
//            }, 3000)
        }
    }

    @Subscribe
    fun onOpenPaywallEvent(e: OpenPaywallEvent) {
        router.replaceScreen(Screens.paywallScreen(source = e.source))
    }

    private fun showHelp(type: HelpType) {
//        if (type == HelpType.BodygraphCenters) {
//            if (!App.preferences.bodygraphCentersHelpShown)
//                showCentersHelp()
//            else showHelp(HelpType.BodygraphAddDiagram)
//        } else if (type == HelpType.BodygraphAddDiagram) {
//            if (!App.preferences.bodygraphAddDiagramHelpShown)
//                showAddDiagramHelp()
//        }
    }

//    private fun showAddDiagramHelp() {
//        val balloon = Balloon.Builder(context!!)
//            .setArrowSize(15)
//            .setArrowOrientation(ArrowOrientation.TOP)
//            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
//            .setArrowPosition(0.9f)
//            .setTextGravity(Gravity.CENTER)
//            .setPadding(10)
//            .setWidth(BalloonSizeSpec.WRAP)
//            .setMaxWidth(300)
//            .setHeight(BalloonSizeSpec.WRAP)
//            .setTextSize(12f)
//            .setCornerRadius(10f)
//            .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_add_diagram))
//            .setTextColor(
//                ContextCompat.getColor(
//                    context!!,
//                    R.color.lightColor
//                )
//            )
//            .setTextIsHtml(true)
//            .setOverlayColorResource(R.color.helpBgColor)
//            .setIsVisibleOverlay(true)
//            .setBackgroundColor(
//                Color.parseColor("#4D494D")
//            )
//            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
//            .setOnBalloonDismissListener {
//                App.preferences.bodygraphAddDiagramHelpShown = true
//                EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))
//            }
//            .build()
//
//        balloon.showAlignBottom(binding.icAddUser, xOff = -requireContext().convertDpToPx(112f).toInt())
//    }
//    private fun showCentersHelp() {
//        val view = View(
//            requireContext()
//        )
//        view.layoutParams = LinearLayout.LayoutParams(
//            1,
//            1
//        )
//        view.x = binding.bodygraphContainer2.width / 2f//binding.bodygraphView.getTopPoint().x.toFloat()
//        view.y = binding.bodygraphContainer2.top + binding.bodygraphView.getTopPoint().y.toFloat()
//
//        binding.bodygraphContainer.addView(view)
//
//        val balloon = Balloon.Builder(context!!)
//            .setArrowSize(15)
//            .setArrowOrientation(ArrowOrientation.BOTTOM)
//            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
//            .setArrowPosition(0.5f)
//            .setTextGravity(Gravity.CENTER)
//            .setPadding(10)
//            .setWidth(BalloonSizeSpec.WRAP)
//            .setMaxWidth(300)
//            .setHeight(BalloonSizeSpec.WRAP)
//            .setTextSize(12f)
//            .setCornerRadius(10f)
//            .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_centers))
//            .setTextColor(
//                ContextCompat.getColor(
//                    context!!,
//                    R.color.lightColor
//                )
//            )
//            .setTextIsHtml(true)
//            .setOverlayColorResource(R.color.helpBgColor)
//            .setIsVisibleOverlay(true)
//            .setBackgroundColor(
//                Color.parseColor("#4D494D")
//            )
//            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
//            .setOnBalloonDismissListener {
//                App.preferences.bodygraphCentersHelpShown = true
//                showHelp(HelpType.BodygraphAddDiagram)
//            }
//            .build()
//
//
//        balloon.showAlignTop(view)
//    }

//    @Subscribe
//    fun onBodygraphCenterClickEvent(e: BodygraphCenterClickEvent) {
//        if (!isAdded) return
//
//        YandexMetrica.reportEvent("UserClickedBodygraphCenter")
//
//        val view = View(requireContext())
//        view.layoutParams = LinearLayout.LayoutParams(
//            1,
//            1
//        )
//        view.x =
//            if (e.isXCenter) binding.bodygraphContainer2.width / 2f
//            else if (e.isLeftTriangle || e.isRightTriangle) binding.bodygraphView.left + e.x
//            else e.x
//        view.y = e.y
//
//        binding.bodygraphContainer2.addView(view)
//
//        val balloon = Balloon.Builder(context!!)
//            .setArrowSize(15)
//            .setArrowOrientation(ArrowOrientation.BOTTOM)
//            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
//            .setArrowPosition(e.arrowPosition)
//            .setTextGravity(Gravity.START)
//            .setPadding(10)
//            .setWidth(BalloonSizeSpec.WRAP)
//            .setMaxWidth(300)
//            .setHeight(BalloonSizeSpec.WRAP)
//            .setTextSize(12f)
//            .setCornerRadius(10f)
//            .setText("<small><strong>${e.title}</strong></small><br>${e.desc}")
//            .setTextColor(
//                ContextCompat.getColor(
//                    context!!,
//                    R.color.lightColor
//                )
//            )
//            .setTextIsHtml(true)
//            .setBackgroundColor(
//                Color.parseColor("#4D494D")
//            )
//            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
//            .setOnBalloonDismissListener {
//                EventBus.getDefault().post(UpdateBalloonBgStateEvent(false))
////                binding.balloonBg.isVisible = false
//            }
//            .build()
//
//        if (e.alignTop) balloon.showAlignTop(view, xOff = e.xOffset)
//        else balloon.showAlignBottom(view, xOff = e.xOffset)
//
//        EventBus.getDefault().post(UpdateBalloonBgStateEvent(true))
////        binding.balloonBg.isVisible = true
//    }

    private fun selectSection(newSECTION: SECTION) {
        when(newSECTION) {
            SECTION.ABOUT -> {
                YandexMetrica.reportEvent("Tab2AboutTapped")
                Amplitude.getInstance().logEvent("tab2TappedAbout")
            }
            SECTION.CENTERS -> {
                if (!App.preferences.isPremiun) return

                YandexMetrica.reportEvent("Tab2CentersTapped")
                Amplitude.getInstance().logEvent("tab2TappedCenters")
            }
            SECTION.GATES -> {
                if (!App.preferences.isPremiun) return

                YandexMetrica.reportEvent("Tab2GatesTapped")
                Amplitude.getInstance().logEvent("tab2TappedGates")
            }
            SECTION.CHANNELS -> {
                if (!App.preferences.isPremiun) return

                YandexMetrica.reportEvent("Tab2ChannelsTapped")
                Amplitude.getInstance().logEvent("tab2TappedChannels")
            }
        }


        binding.aboutTitle.background =
            if (newSECTION == SECTION.ABOUT)
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_selection_item)
            else null

        binding.centersTitle.background =
            if (newSECTION == SECTION.CENTERS)
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_selection_item)
            else null

        binding.gatesTitle.background =
            if (newSECTION == SECTION.GATES)
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_selection_item)
            else null

        binding.channelsTitle.background =
            if (newSECTION == SECTION.CHANNELS)
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_selection_item)
            else null

        binding.aboutTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (newSECTION == SECTION.ABOUT) R.color.lightColor
            else {
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            }
        ))

        binding.gatesTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (newSECTION == SECTION.GATES) R.color.lightColor
            else {
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            }
        ))

        binding.channelsTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (newSECTION == SECTION.CHANNELS) R.color.lightColor
            else {
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            }
        ))

        binding.centersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (newSECTION == SECTION.CENTERS) R.color.lightColor
            else {
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            }
        ))

        binding.aboutTitle.alpha =
            if (newSECTION == SECTION.ABOUT) 1f
            else 0.48f

        binding.gatesTitle.alpha =
            if (newSECTION == SECTION.GATES) 1f
            else 0.48f

        binding.centersTitle.alpha =
            if (newSECTION == SECTION.CENTERS) 1f
            else 0.48f

        binding.channelsTitle.alpha =
            if (newSECTION == SECTION.CHANNELS) 1f
            else 0.48f
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = descriptionAdapter

        if (!App.preferences.isPremiun)
            binding.viewPager.isUserInputEnabled = false

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
                    title = App.resourcesProvider.getStringLocale(R.string.menu_first_title),
                    subtitle = App.resourcesProvider.getStringLocale(R.string.onboarding_title_3),
                    text = "Your an individual bodygraph contains a schematic image of energy centers, channels. Learn more about yourself.",
                    type = AboutType.BODYGRAPH
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.type_title),
                    subtitle = it.description.typeTitle,
                    text = it.description.type,
                    type = AboutType.TYPE
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.profile_title),
                    subtitle = it.description.profileTitle,
                    text = it.description.profile,
                    type = AboutType.PROFILE
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.authority_title),
                    subtitle = it.authority.name,
                    text = it.authority.description,
                    type = AboutType.AUTHORITY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.injury_title),
                    subtitle = it.injury.name?: "",
                    text = it.injury.description?: "",
                    type = AboutType.INJURY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.strategy_title),
                    subtitle = it.strategy.name?: "",
                    text = it.strategy.description?: "",
                    type = AboutType.STRATEGY
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.nutrition_title),
                    subtitle = it.nutrition.name?: "",
                    text = it.nutrition.description?: "",
                    type = AboutType.NUTRITION
                )
            )

            aboutItemsList.add(
                AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.environment_title),
                    subtitle = it.environment.name?: "",
                    text = it.environment.description?: "",
                    type = AboutType.ENVIRONMENT
                )
            )

            descriptionAdapter.createList(
                activeCenters = it.activeCentres,
                inactiveCenters = it.inactiveCentres,
                gates = gates,
                channels = channels,
                aboutItems = aboutItemsList,
                currentUser = baseViewModel.currentUser
            )

            if (fromStart) {
                onOpenAboutItemEvent(OpenAboutItemEvent(item = AboutItem(
                    title = App.resourcesProvider.getStringLocale(R.string.menu_first_title),
                    subtitle = App.resourcesProvider.getStringLocale(R.string.onboarding_title_3),
                    text = "Your an individual bodygraph contains a schematic image of energy centers, channels. Learn more about yourself.",
                    type = AboutType.BODYGRAPH
                )))
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                selectSection(
                    when(position) {
                        0 -> SECTION.ABOUT
                        1 -> SECTION.CENTERS
                        2 -> SECTION.GATES
                        else -> SECTION.CHANNELS
                    }
                )

            }
        })
    }


    inner class Handler {
        fun onAboutClicked(v: View) {
            if (App.preferences.isPremiun) {
                binding.viewPager.setCurrentItem(0, true)
                selectSection(SECTION.ABOUT)
            }
        }

        fun onCentersClicked(v: View) {
            if (App.preferences.isPremiun) {
                binding.viewPager.setCurrentItem(1, true)
                selectSection(SECTION.CENTERS)
            }
        }

        fun onGatesClicked(v: View) {
            if (App.preferences.isPremiun) {
                binding.viewPager.setCurrentItem(2, true)
                selectSection(SECTION.GATES)
            }
        }

        fun onChannelsClicked(v: View) {
            if (App.preferences.isPremiun) {
                binding.viewPager.setCurrentItem(3, true)
                selectSection(SECTION.CHANNELS)
            }
        }
    }

    enum class SECTION {
        ABOUT, CENTERS, GATES, CHANNELS
    }
}