package com.myhumandesignhd.ui.adduser

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amplitude.api.Amplitude
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentAddUserBinding
import com.myhumandesignhd.event.BodygraphCenterClickEvent
import com.myhumandesignhd.event.LastKnownLocationUpdateEvent
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.PlaceSelectedEvent
import com.myhumandesignhd.event.UpdateBalloonBgStateEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.model.Place
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.start.StartPage
import com.myhumandesignhd.ui.start.StartViewModel
import com.myhumandesignhd.ui.start.adapter.PlacesAdapter
import com.myhumandesignhd.util.Keyboard
import com.myhumandesignhd.util.ext.alpha0
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
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.single_day_and_time_picker.view.minutesPicker
import kotlinx.android.synthetic.main.view_place_select.view.icArrowPlace
import kotlinx.android.synthetic.main.view_place_select.view.icSearch
import kotlinx.android.synthetic.main.view_place_select.view.newPlaceET
import kotlinx.android.synthetic.main.view_place_select.view.placeRecycler
import kotlinx.android.synthetic.main.view_place_select.view.placesViewContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.Calendar

class AddUserFragment : BaseFragment<StartViewModel, FragmentAddUserBinding>(
    R.layout.fragment_add_user,
    StartViewModel::class,
    Handler::class
) {

    var currentStartPage: StartPage = StartPage.RAVE

    var isCurrentLocationVariantSet = false

    var selectedLat = ""
    var selectedLon = ""

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val fromCompatibility: Boolean by lazy {
        arguments?.getBoolean("fromCompatibility", false) ?: false
    }

    private val isChild: Boolean by lazy {
        arguments?.getBoolean("isChild", false) ?: false
    }

    private val placesAdapter: PlacesAdapter by lazy {
        PlacesAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))

        prepareLogic()
        startCirclesRotation(StartPage.RAVE)

        setupRave()
        setupPlacesView()

        if (!App.preferences.lastKnownLocation.isNullOrEmpty()) {
            binding.placeET.setText(App.preferences.lastKnownLocation)
            selectedLat = App.preferences.lastKnownLocationLat!!
            selectedLon = App.preferences.lastKnownLocationLon!!
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Handler().onBackClicked(binding.backBtn)
    }

    private var prevHeightDiff = -1
    private fun addKeyboardDetectListener(){
        binding.startContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (isAdded) {
                val heightDifference =
                    binding.startContainer.rootView.height - binding.startContainer.height
                if (heightDifference == prevHeightDiff) {
                    return@addOnGlobalLayoutListener
                }
                prevHeightDiff = heightDifference

                if (heightDifference > dpToPx(requireContext(), 200F) && !binding.placesView.isVisible) {
                    binding.nameET.isVisible = false
                    binding.nameET.requestLayout()

                    binding.indicatorsContainer.visibility = View.GONE
                    binding.startBtn.visibility = View.INVISIBLE

                    (binding.nameET.layoutParams as ViewGroup.MarginLayoutParams)
                        .setMargins(
                            dpToPx(requireContext(), 20f).toInt(),
                            0,
                            dpToPx(requireContext(), 20f).toInt(),
                            0
                        )


                    android.os.Handler().postDelayed({
                        requireActivity().runOnUiThread {
                            if (!binding.placesView.isVisible) {
                                binding.nameET.isVisible = true
                                binding.nameET.requestLayout()
                                binding.nameET.requestFocus()
                            }
                        }

                    }, 50)
                } else {
                    android.os.Handler().postDelayed({
                        if (isAdded) {
                            binding.indicatorsContainer.isVisible = true

                            if (!binding.placesView.isVisible)
                                binding.startBtn.isVisible = true

                            (binding.nameET.layoutParams as ViewGroup.MarginLayoutParams)
                                .setMargins(
                                    dpToPx(requireContext(), 20f).toInt(),
                                    0,
                                    dpToPx(requireContext(), 20f).toInt(),
                                    dpToPx(requireContext(), 46f).toInt()
                                )
                        }
                    }, 10)
                }
            }
        }
    }

    private fun dpToPx(context: Context, valueInDp: Float) : Float{
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics)
    }

    @Subscribe
    fun onLastKnownLocationUpdateEvent(e: LastKnownLocationUpdateEvent) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (!App.preferences.lastKnownLocation.isNullOrEmpty()) {
                binding.placeET.setText(App.preferences.lastKnownLocation)
                selectedLat = App.preferences.lastKnownLocationLat!!
                selectedLon = App.preferences.lastKnownLocationLon!!
            }
        }
    }

    override fun updateThemeAndLocale() {

        binding.bodygraphReadyText1.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
        binding.bodygraphReadyText2.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
        binding.bodygraphReadyText3.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
        binding.bodygraphReadyText4.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
        binding.bodygraphReadyText5.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.bottomGradient.isVisible = App.preferences.isDarkTheme

        binding.icArrow.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.startContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.raveTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.raveDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.nameTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.nameDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.nameET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.nameET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        binding.skipTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.placeET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.placeET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        binding.placesView.newPlaceET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.placesView.newPlaceET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        binding.placesView.newPlaceET.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        binding.placesView.placesViewContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )


        binding.bodygraphReadyTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.bodygraphReadyText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.nameET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        ))

        binding.placeET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        ))

        binding.placesView.newPlaceET.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_search_dark
            else R.drawable.bg_search_light
        )

        binding.placesView.icArrowPlace.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.placesView.icSearch.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.searchTintDark
            else R.color.searchTintLight
        ))

        binding.skipTime.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_skip_time
            else R.drawable.bg_skip_time_light
        )
    }

    override fun onViewModelReady(viewModel: StartViewModel) {
        super.onViewModelReady(viewModel)

        viewModel.nominatimSuggestions.observe(this) { list ->
            if (binding.placesView.isVisible) {
                val addresses: MutableList<Place> = mutableListOf()

                list.forEach { feature ->
                    if (addresses.none { it.name == feature.placeName }) {
                        addresses.add(
                            Place(
                                name = feature.placeName,
                                lat = feature.lat,
                                lon = feature.lon
                            )

                        )
                    }
                }
                placesAdapter.createList(addresses.toList())
            }
        }
    }

    private fun setupPlacesView() {
        (binding.placesView.placeRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.placesView.placeRecycler.itemAnimator = null
        binding.placesView.placeRecycler.adapter = placesAdapter

        binding.placesView.icArrowPlace.setOnClickListener {
            Keyboard.hide(requireActivity())
            binding.placesView.isVisible = false
            binding.startBtn.isVisible = true
            placesAdapter.createList(emptyList(), false)
        }

        binding.placesView.newPlaceET.hint = App.resourcesProvider.getStringLocale(R.string.search)
        binding.placesView.newPlaceET.addTextChangedListener {
            if (!binding.placesView.newPlaceET.text.isNullOrEmpty()) {
                binding.viewModel!!.geocodingNominatim(binding.placesView.newPlaceET.text.toString())
            }
        }
    }

    @Subscribe
    fun onPlaceSelectedEvent(e: PlaceSelectedEvent) {
        Keyboard.hide(requireActivity())

        binding.placesView.isVisible = false
        binding.startBtn.isVisible = true
        binding.placesView.newPlaceET.setText("")
        placesAdapter.createList(emptyList(), false)

        binding.placeET.setText(e.place.name)
        selectedLat = e.place.lat
        selectedLon = e.place.lon
    }

    private var totalTranslationYForBigCircle = 0f
    private var totalTranslationYForMidCircle = 0f
    var stepTranslationYForBigCircle = 0f
    private var stepTranslationYForMidCircle = 0f

    private fun prepareLogic() {
        val pos = IntArray(2)
        binding.icSplashBigCircle.getLocationOnScreen(pos)

        //Get screen size
        val screenWidth: Int = requireContext().resources.displayMetrics.widthPixels
        val screenHeight: Int = requireContext().resources.displayMetrics.heightPixels

        totalTranslationYForBigCircle =
            -(screenHeight / 2f - pos[1] + binding.icSplashBigCircle.height / 2f)
        totalTranslationYForMidCircle =
            -(screenHeight / 2f - pos[1] + binding.icSplashMidCircle.height / 2f)
        stepTranslationYForBigCircle = totalTranslationYForBigCircle / 5
        stepTranslationYForMidCircle = totalTranslationYForMidCircle / 5
    }

    private fun setupRave() {
        currentStartPage = StartPage.RAVE

        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        when {
            isChild -> {

                binding.raveTitle.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.add_child_rave_title))
                binding.raveDesc.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.add_child_rave_desc))
            }
            fromCompatibility -> {

                binding.raveTitle.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.add_partner_rave_title))
                binding.raveDesc.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.add_partner_rave_desc))
            }
            else -> {

                binding.raveTitle.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.diagram_rave_title))
                binding.raveDesc.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.diagram_rave_desc))
            }
        }
    }

    private fun setupName() {
        currentStartPage = StartPage.NAME

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        addKeyboardDetectListener()

//        if (App.preferences.isDarkTheme)
//            binding.bottomGradient.alpha1(200)

        binding.nameContainer.isVisible = true
        binding.raveContainer.alpha0(500)
        binding.nameContainer.alpha1(500)
        binding.indicatorsContainer.alpha1(500)

        when {
            isChild -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_name_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_name_desc))
            }
            fromCompatibility -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_name_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_name_desc))
            }
            else -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_name_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_name_desc))
            }
        }

        binding.nameET.hint = App.resourcesProvider.getStringLocale(R.string.start_name_hint)
    }

    private fun setupDateBirth() {
        currentStartPage = StartPage.DATE_BIRTH

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -20)
        binding.date.maxDate = Calendar.getInstance().time
        binding.date.setDefaultDate(c.time)

        binding.nameET.alpha0(500) {
            binding.nameET.isVisible = false
        }
        binding.date.isVisible = true
        binding.date.alpha1(500)

        when {
            isChild -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_date_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_date_birth_desc))
            }
            fromCompatibility -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_date_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_date_birth_desc))
            }
            else -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_date_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_date_desc))
            }
        }
    }

    private fun setupTimeBirth() {
        currentStartPage = StartPage.TIME_BIRTH

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        when {
            isChild -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_time_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_time_birth_desc))
            }
            fromCompatibility -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_time_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_time_birth_desc))
            }
            else -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_time_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_time_desc))
            }
        }

        binding.skipTime.isVisible = true
        binding.skipTime.alpha = 1f
        binding.skipTime.text = (App.resourcesProvider.getStringLocale(R.string.start_time_skip))

        binding.date.alpha0(300) {
            binding.date.isVisible = false
        }

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 12)
        c.set(Calendar.MINUTE, 0)

        if (App.preferences.locale == "en")
            binding.time.setIsAmPm(true)
        else binding.time.setIsAmPm(false)

        binding.time.selectDate(c)

        binding.time.isVisible = true
        binding.time.alpha1(500)
    }

    private fun setupPlaceBirth() {
        currentStartPage = StartPage.PLACE_BIRTH

        unselectAllIndicators()
        binding.indicator5.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        when {
            isChild -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_place_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_place_birth_desc))
            }
            fromCompatibility -> {

                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_place_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_place_birth_desc))
            }
            else -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_place_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_place_desc))
            }
        }

        binding.skipTime.alpha0(300) {
            binding.skipTime.isVisible = false
        }
        binding.time.alpha0(300) {
            binding.time.isVisible = false
        }

        if (!isCurrentLocationVariantSet)
            binding.placeET.hint = App.resourcesProvider.getStringLocale(R.string.start_place_hint)

        if (!App.preferences.lastKnownLocation.isNullOrEmpty())
            binding.placeET.setText(App.preferences.lastKnownLocation)

        binding.placeET.isVisible = true
        binding.placeET.alpha1(500)

    }

    private fun setupBodygraph() {
        currentStartPage = StartPage.BODYGRAPH
        binding.startBtn.isVisible = false

        binding.indicatorsContainer.alpha0(300)

        binding.nameContainer.alpha0(100) {
            binding.backBtn.isVisible = false
        }

        binding.bodygraphContainer.isVisible = true
        binding.bodygraphContainer.alpha1(500)

        when {
            isChild -> {
//                binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_bodygraph_ready_title))

            }
            fromCompatibility -> {
//                binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_bodygraph_ready_title))
            }
            else -> {
//                binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_bodygraph_title))

            }
        }
        binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_bodygraph_creating_title))

        when {
            isChild -> {
                baseViewModel.currentChildBodygraph.observe(viewLifecycleOwner) {
                    if (
                        !it.design.channels.isNullOrEmpty()
                        && !it.personality.channels.isNullOrEmpty()
                    ) {
                        android.os.Handler().postDelayed({
                            binding.startBtn.isEnabled = true

                            if (!binding.bodygraphView.isVisible) {
                                binding.bodygraphView.isVisible = true
                                binding.bodygraphView.scaleXY(1f, 1f, 1500) {
                                    binding.bodygraphView.changeSpeedAnimationFactor(10f)
                                    binding.bodygraphView.changeIsAllowDrawLinesState(true)
                                }

                                binding.bodygraphReadyMark1.isVisible = true
                                binding.bodygraphReadyMark1.playAnimation()
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark2.isVisible = true
                                    binding.bodygraphReadyMark2.playAnimation()
                                }, 1500)
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark3.isVisible = true
                                    binding.bodygraphReadyMark3.playAnimation()
                                }, 3000)

                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark4.isVisible = true
                                    binding.bodygraphReadyMark4.playAnimation()
                                }, 4500)
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark5.isVisible = true
                                    binding.bodygraphReadyMark5.playAnimation()
                                }, 6000)

                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyTitle.text =
                                        App.resourcesProvider.getStringLocale(
                                            R.string.start_bodygraph_ready_title
                                        )


                                    android.os.Handler().postDelayed({
                                        App.preferences.lastLoginPageId = -1

                                        when {
                                            isChild -> {
                                                Amplitude.getInstance()
                                                    .logEvent("tab4AddChildStartButton7")
                                                App.preferences.isCompatibilityFromChild = true
                                                router.navigateTo(Screens.compatibilityScreen())
                                            }
                                            fromCompatibility -> {
                                                Amplitude.getInstance()
                                                    .logEvent("tab3TappedStartButton7")
                                                router.navigateTo(Screens.compatibilityScreen())
                                            }
                                            else -> {
                                                Amplitude.getInstance()
                                                    .logEvent("addUserTappedStart6")
                                                App.isBodygraphWithAnimationShown = false

                                                App.preferences.isInvokeNewTransits = true
                                                App.preferences.isInvokeNewDescription = true
                                                App.preferences.isInvokeNewCompatibility = true
                                                App.preferences.isInvokeNewInsights = true

                                                App.isBodygraphWithAnimationShown = false

                                                router.replaceScreen(Screens.bodygraphScreen())
                                            }
                                        }
                                    }, 1000)

                                }, 7500)
                            }
                        }, 700)

                    }

                    binding.bodygraphView.setupData(
                        it.design,
                        it.personality,
                        it.activeCentres,
                        it.inactiveCentres
                    )
                }
            }
            fromCompatibility -> {
                baseViewModel.currentPartnerBodygraph.observe(viewLifecycleOwner) {
                    if (
                        !it.design.channels.isNullOrEmpty()
                        && !it.personality.channels.isNullOrEmpty()
                    ) {
                        android.os.Handler().postDelayed({
                            binding.startBtn.isEnabled = true

                            if (!binding.bodygraphView.isVisible) {
                                binding.bodygraphView.isVisible = true
                                binding.bodygraphView.scaleXY(1f, 1f, 1500) {
                                    binding.bodygraphView.changeSpeedAnimationFactor(10f)
                                    binding.bodygraphView.changeIsAllowDrawLinesState(true)
                                }

                                binding.bodygraphReadyMark1.isVisible = true
                                binding.bodygraphReadyMark1.playAnimation()
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark2.isVisible = true
                                    binding.bodygraphReadyMark2.playAnimation()
                                }, 1500)
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark3.isVisible = true
                                    binding.bodygraphReadyMark3.playAnimation()
                                }, 3000)

                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark4.isVisible = true
                                    binding.bodygraphReadyMark4.playAnimation()
                                }, 4500)
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark5.isVisible = true
                                    binding.bodygraphReadyMark5.playAnimation()
                                }, 6000)

                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyTitle.text =
                                        App.resourcesProvider.getStringLocale(
                                            R.string.start_bodygraph_ready_title
                                        )


                                    android.os.Handler().postDelayed({
                                        App.preferences.lastLoginPageId = -1

                                        when {
                                            isChild -> {
                                                Amplitude.getInstance()
                                                    .logEvent("tab4AddChildStartButton7")
                                                App.preferences.isCompatibilityFromChild = true
                                                router.navigateTo(Screens.compatibilityScreen())
                                            }
                                            fromCompatibility -> {
                                                Amplitude.getInstance()
                                                    .logEvent("tab3TappedStartButton7")
                                                router.navigateTo(Screens.compatibilityScreen())
                                            }
                                            else -> {
                                                Amplitude.getInstance()
                                                    .logEvent("addUserTappedStart6")
                                                App.isBodygraphWithAnimationShown = false

                                                App.preferences.isInvokeNewTransits = true
                                                App.preferences.isInvokeNewDescription = true
                                                App.preferences.isInvokeNewCompatibility = true
                                                App.preferences.isInvokeNewInsights = true

                                                App.isBodygraphWithAnimationShown = false

                                                router.replaceScreen(Screens.bodygraphScreen())
                                            }
                                        }
                                    }, 1000)

                                }, 7500)
                            }
                        }, 700)

                    }

                    binding.bodygraphView.setupData(
                        it.design,
                        it.personality,
                        it.activeCentres,
                        it.inactiveCentres
                    )
                }
            }
            else -> {
                baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                    if (
                        !it.design.channels.isNullOrEmpty()
                        && !it.personality.channels.isNullOrEmpty()
                    ) {
                        android.os.Handler().postDelayed({
                            binding.startBtn.isEnabled = true

                            if (!binding.bodygraphView.isVisible) {
                                binding.bodygraphView.isVisible = true
                                binding.bodygraphView.scaleXY(1f, 1f, 1500) {
                                    binding.bodygraphView.changeSpeedAnimationFactor(10f)
                                    binding.bodygraphView.changeIsAllowDrawLinesState(true)
                                }

                                binding.bodygraphReadyMark1.isVisible = true
                                binding.bodygraphReadyMark1.playAnimation()
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark2.isVisible = true
                                    binding.bodygraphReadyMark2.playAnimation()
                                }, 1500)
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark3.isVisible = true
                                    binding.bodygraphReadyMark3.playAnimation()
                                }, 3000)

                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark4.isVisible = true
                                    binding.bodygraphReadyMark4.playAnimation()
                                }, 4500)
                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyMark5.isVisible = true
                                    binding.bodygraphReadyMark5.playAnimation()
                                }, 6000)

                                android.os.Handler().postDelayed({
                                    binding.bodygraphReadyTitle.text =
                                        App.resourcesProvider.getStringLocale(
                                            R.string.start_bodygraph_ready_title
                                        )


                                    android.os.Handler().postDelayed({
                                        App.preferences.lastLoginPageId = -1

                                        when {
                                            isChild -> {
                                                Amplitude.getInstance()
                                                    .logEvent("tab4AddChildStartButton7")
                                                App.preferences.isCompatibilityFromChild = true
                                                router.navigateTo(Screens.compatibilityScreen())
                                            }
                                            fromCompatibility -> {
                                                Amplitude.getInstance()
                                                    .logEvent("tab3TappedStartButton7")
                                                router.navigateTo(Screens.compatibilityScreen())
                                            }
                                            else -> {
                                                Amplitude.getInstance()
                                                    .logEvent("addUserTappedStart6")
                                                App.isBodygraphWithAnimationShown = false

                                                App.preferences.isInvokeNewTransits = true
                                                App.preferences.isInvokeNewDescription = true
                                                App.preferences.isInvokeNewCompatibility = true
                                                App.preferences.isInvokeNewInsights = true

                                                App.isBodygraphWithAnimationShown = false

                                                router.replaceScreen(Screens.bodygraphScreen())
                                            }
                                        }
                                    }, 1000)

                                }, 7500)
                            }
                        }, 700)

                    }

                    binding.bodygraphView.setupData(
                        it.design,
                        it.personality,
                        it.activeCentres,
                        it.inactiveCentres
                    )
                }
            }
        }
    }

    private fun unselectAllIndicators() {
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator5.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )
    }

    private val snackbarName: Snackbar by lazy {
        val snackView = View.inflate(requireContext(), R.layout.view_snackbar, null)
        val snackbar = Snackbar.make(binding.snackbarContainer, "", Snackbar.LENGTH_LONG)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        val view = snackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params

        (snackbar.view as ViewGroup).removeAllViews()
        (snackbar.view as ViewGroup).addView(snackView)

        snackView.findViewById<TextView>(R.id.title).text = App.resourcesProvider.getStringLocale(R.string.snackbar_title)
        snackView.findViewById<TextView>(R.id.desc).text = App.resourcesProvider.getStringLocale(R.string.snackbar_name)
        snackbar.setBackgroundTint(Color.parseColor("#F7C52B"))

        snackbar
    }

    private val snackbarAddress: Snackbar by lazy {
        val snackView = View.inflate(requireContext(), R.layout.view_snackbar, null)
        val snackbar = Snackbar.make(binding.snackbarContainer, "", Snackbar.LENGTH_LONG)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        val view = snackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params

        (snackbar.view as ViewGroup).removeAllViews()
        (snackbar.view as ViewGroup).addView(snackView)

        snackView.findViewById<TextView>(R.id.title).text = App.resourcesProvider.getStringLocale(R.string.snackbar_title)
        snackView.findViewById<TextView>(R.id.desc).text = App.resourcesProvider.getStringLocale(R.string.snackbar_address)
        snackbar.setBackgroundTint(Color.parseColor("#F7C52B"))

        snackbar
    }

    private fun isNetworkConnected(): Boolean {
        val cm = requireContext().getSystemService(DaggerAppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    @Subscribe
    fun onBodygraphCenterClickEvent(e: BodygraphCenterClickEvent) {
        if (!isAdded) return

        val view = View(
            requireContext()
        )
        view.layoutParams = LinearLayout.LayoutParams(
            1,
            1
        )
        view.x =
            if (e.isLeftTriangle) binding.bodygraphView.left + e.x
            else if (e.isRightTriangle) binding.bodygraphView.right - e.x
             else if (e.isXCenter) binding.bodygraphContainer.width / 2f
            else e.x
        view.y =
            binding.bodygraphView.top + e.y

        binding.bodygraphContainer.addView(view)

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

        fun openPlacesView(v: View) {
            binding.placesView.isVisible = true
            binding.startBtn.isVisible = false
        }

        fun onBackClicked(v: View) {
            if (binding.placesView.isVisible) return

            animateBackCirclesBtwPages()
            when (currentStartPage) {
                StartPage.RAVE -> {
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                    router.exit()
                }
                StartPage.NAME -> {
                    binding.nameContainer.alpha0(500) {
                        binding.nameContainer.isVisible = false
                    }
                    binding.raveContainer.alpha1(500)
//                    binding.startBtn.translationY(requireContext().convertDpToPx(0f), 500)
                    setupRave()
                }
                StartPage.DATE_BIRTH -> {
                    binding.date.alpha0(500) {
                        binding.date.isVisible = false
                    }
                    binding.nameET.isVisible = true
                    binding.nameET.alpha1(500)

                    setupName()
                }
                StartPage.TIME_BIRTH -> {
                    binding.skipTime.isVisible = false
                    binding.time.alpha0(500) {
                        binding.time.isVisible = false
                    }
                    setupDateBirth()
                }
                StartPage.PLACE_BIRTH -> {
                    binding.placeET.alpha0(500) {
                        binding.placeET.isVisible = false
                    }
                    binding.skipTime.alpha = 1f
                    setupTimeBirth()
                }
                StartPage.BODYGRAPH -> {
                    setupPlaceBirth()
                }
            }
        }

        fun onBtnClicked(v: View) {
            when (currentStartPage) {
                StartPage.RAVE -> {
                    when {
                        isChild -> Amplitude.getInstance().logEvent("tab4AddChildStartButton1")
                        fromCompatibility -> Amplitude.getInstance().logEvent("tab3TappedStartButton1")
                        else -> Amplitude.getInstance().logEvent("addUserTappedStart1")
                    }

                    animateCirclesBtwPages(1000)
                    setupName()
                }
                StartPage.NAME -> {
                    when {
                        isChild -> Amplitude.getInstance().logEvent("tab4AddChildStartButton2")
                        fromCompatibility -> Amplitude.getInstance().logEvent("tab3TappedStartButton2")
                        else -> Amplitude.getInstance().logEvent("addUserTappedStart2")
                    }

                    if (binding.nameET.text.toString().replace(" ", "").isNullOrEmpty()) {
                        snackbarName.show()
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Keyboard.hide(binding.nameET)
                        }.invokeOnCompletion {
                            requireActivity().runOnUiThread {
                                animateCirclesBtwPages(1000)
                                setupDateBirth()
                            }
                        }

                    }
                }
                StartPage.DATE_BIRTH -> {
                    when {
                        isChild -> Amplitude.getInstance().logEvent("tab4AddChildStartButton3")
                        fromCompatibility -> Amplitude.getInstance().logEvent("tab3TappedStartButton3")
                        else -> Amplitude.getInstance().logEvent("addUserTappedStart3")
                    }

                    animateCirclesBtwPages(1000)
                    setupTimeBirth()
                }
                StartPage.TIME_BIRTH -> {
                    when {
                        isChild -> Amplitude.getInstance().logEvent("tab4AddChildStartButton4")
                        fromCompatibility -> Amplitude.getInstance().logEvent("tab3TappedStartButton4")
                        else -> Amplitude.getInstance().logEvent("addUserTappedStart4")
                    }

                    Amplitude.getInstance().logEvent(
                        "",
                        JSONObject(mutableMapOf(
                            "source" to when {
                                isChild -> "fromKidsBodygraphCreating"
                                fromCompatibility -> "fromCompatibleBodygraphCreating"
                                else -> "fromSecondaryBodygraphCreating"
                            }
                        ).toMap()))


                    animateCirclesBtwPages(1000)
                    setupPlaceBirth()
                }
                StartPage.PLACE_BIRTH -> {

                    when {
                        isChild -> Amplitude.getInstance().logEvent("tab4AddChildStartButton5")
                        fromCompatibility -> Amplitude.getInstance().logEvent("tab3TappedStartButton5")
                        else -> Amplitude.getInstance().logEvent("addUserTappedStart5")
                    }

                    if (!isNetworkConnected()) {
                        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                        EventBus.getDefault().post(NoInetEvent())

                        return
                    }

                    if (binding.placeET.text.toString().replace(" ", "").isNullOrEmpty()) {
                        snackbarAddress.show()
                    } else {
                        animateCirclesBtwPages(1000)

                        lifecycleScope.launch {
                            if (isChild) {
                                baseViewModel.createNewChild(
                                    name = binding.nameET.text.toString(),
                                    place = binding.placeET.text.toString(),
                                    date = binding.date.date.time,
                                    time = String.format(
                                        "%02d",
                                        binding.time.date.hours
//                                        binding.time.hoursPicker.currentHour
                                    ) + ":" + String.format("%02d", binding.time.minutesPicker.currentMinute),
                                    lat = selectedLat,
                                    lon = selectedLon
                                )
                            } else {
                                baseViewModel.createNewUser(
                                    name = binding.nameET.text.toString(),
                                    place = binding.placeET.text.toString(),
                                    date = binding.date.date.time,
                                    time = String.format(
                                        "%02d",
                                        binding.time.date.hours
//                                        binding.time.hoursPicker.currentHour
                                    ) + ":" + String.format("%02d", binding.time.minutesPicker.currentMinute),
                                    lat = selectedLat,
                                    lon = selectedLon,
                                    fromCompatibility = fromCompatibility
                                )
                            }

                            binding.startBtn.isEnabled = false
                            setupBodygraph()
                        }
                    }
                }
                StartPage.BODYGRAPH -> {
                    App.preferences.lastLoginPageId = -1

                    when {
                        isChild -> {
                            Amplitude.getInstance().logEvent("tab4AddChildStartButton7")
                            App.preferences.isCompatibilityFromChild = true
                            router.navigateTo(Screens.compatibilityScreen())
                        }
                        fromCompatibility -> {
                            Amplitude.getInstance().logEvent("tab3TappedStartButton7")
                            router.navigateTo(Screens.compatibilityScreen())
                        }
                        else -> {
                            Amplitude.getInstance().logEvent("addUserTappedStart6")
                            App.isBodygraphWithAnimationShown = false

                            App.preferences.isInvokeNewTransits = true
                            App.preferences.isInvokeNewDescription = true
                            App.preferences.isInvokeNewCompatibility = true
                            App.preferences.isInvokeNewInsights = true

                            App.isBodygraphWithAnimationShown = false

                            router.replaceScreen(Screens.bodygraphScreen())
//                            router.navigateTo(Screens.bodygraphScreen())
                        }
                    }
                }
            }
        }
    }

}