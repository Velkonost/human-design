package com.myhumandesignhd.ui.start

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentStartBinding
import com.myhumandesignhd.event.LastKnownLocationUpdateEvent
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.PlaceSelectedEvent
import com.myhumandesignhd.event.SetupLocationEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.event.UpdateThemeEvent
import com.myhumandesignhd.model.Place
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.start.adapter.PlacesAdapter
import com.myhumandesignhd.util.Keyboard
import com.myhumandesignhd.util.ext.alpha0
import com.myhumandesignhd.util.ext.alpha1
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.util.ext.setTextAnimation07
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.container_start_name.view.date
import kotlinx.android.synthetic.main.container_start_name.view.nameContainer
import kotlinx.android.synthetic.main.container_start_name.view.nameDesc
import kotlinx.android.synthetic.main.container_start_name.view.nameET
import kotlinx.android.synthetic.main.container_start_name.view.nameTitle
import kotlinx.android.synthetic.main.container_start_name.view.placeET
import kotlinx.android.synthetic.main.container_start_name.view.skipTime
import kotlinx.android.synthetic.main.container_start_name.view.time
import kotlinx.android.synthetic.main.single_day_and_time_picker.view.minutesPicker
import kotlinx.android.synthetic.main.view_place_select.view.icArrowPlace
import kotlinx.android.synthetic.main.view_place_select.view.newPlaceET
import kotlinx.android.synthetic.main.view_place_select.view.placeRecycler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale

class StartFragment : BaseFragment<StartViewModel, FragmentStartBinding>(
    R.layout.fragment_start,
    StartViewModel::class,
    Handler::class
) {

    var currentStartPage: StartPage = StartPage.RAVE

    private var isCurrentLocationVariantSet = false

    private var selectedLat = ""
    private var selectedLon = ""

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity())[BaseViewModel::class.java]
    }

    private val placesAdapter: PlacesAdapter by lazy { PlacesAdapter() }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupInitTheme()
//        setupSplash01()
        setupLoader()
        setupLocale()

        prepareLogic()
        startCirclesRotation(StartPage.RAVE)

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

        binding.nameContainerStub.setOnInflateListener { stub, inflated ->
            if (!App.preferences.lastKnownLocation.isNullOrEmpty()) {
                inflated.placeET.setText(App.preferences.lastKnownLocation)
                selectedLat = App.preferences.lastKnownLocationLat!!
                selectedLon = App.preferences.lastKnownLocationLon!!
            }

            inflated.skipTime.setOnClickListener {
                Amplitude.getInstance().logEvent("userTappedStart3_dontnow")

                Handler().onBtnClicked(inflated.skipTime)
            }
            inflated.placeET.setOnClickListener { Handler().openPlacesView(inflated.placeET) }

            inflated.nameTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            inflated.nameDesc.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            inflated.nameET.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            inflated.nameET.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkHintColor
                    else R.color.lightHintColor
                )
            )

            inflated.skipTime.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            inflated.placeET.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            inflated.placeET.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.darkHintColor
                    else R.color.lightHintColor
                )
            )

            inflated.nameET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            ))

            inflated.placeET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            ))

            inflated.skipTime.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_skip_time
                else R.drawable.bg_skip_time_light
            )

            inflated.date.updateTheme()
            inflated.time.updateTheme()
        }
    }

    private fun requestReviewFlow() {
        val reviewManager = ReviewManagerFactory.create(requireActivity())
        val requestReviewFlow = reviewManager.requestReviewFlow()

        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                if (isAdded) {
                    val reviewInfo = request.result
                    val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                    flow.addOnCompleteListener {
                    }
                }
            } else {
            }
        }
    }

    private fun setupLoader() {
        binding.loaderView.anim.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                binding.loaderView.container.isVisible = false
                setupSplash01()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        android.os.Handler().postDelayed({
            binding.loaderView.anim.playAnimation()
        }, 1000)
    }

    private var prevHeightDiff = -1
    private fun addKeyboardDetectListener(){
        binding.startContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!isAdded) return@addOnGlobalLayoutListener

            val heightDifference = binding.startContainer.rootView.height - binding.startContainer.height

            if (heightDifference == prevHeightDiff) {
                return@addOnGlobalLayoutListener
            }
            prevHeightDiff = heightDifference

            if(heightDifference > dpToPx(requireContext(), 200F)) {
                inflatedNameContainer.nameET.isVisible = false
                inflatedNameContainer.nameET.requestLayout()

                binding.indicatorsContainer.visibility = View.GONE
                binding.startBtn.visibility = View.GONE

                (inflatedNameContainer.nameET.layoutParams as ViewGroup.MarginLayoutParams)
                    .setMargins(
                        dpToPx(requireContext(), 20f).toInt(),
                        0,
                        dpToPx(requireContext(), 20f).toInt(),
                        0
                    )

                android.os.Handler().postDelayed({
                    requireActivity().runOnUiThread {
                        if (!binding.placesView.isVisible) {
                            inflatedNameContainer.nameET.isVisible = true
                            inflatedNameContainer.nameET.requestLayout()
                            inflatedNameContainer.nameET.requestFocus()
                        }
                    }

                }, 50)
            } else {
                android.os.Handler().postDelayed({
                    binding.indicatorsContainer.isVisible = true

                    if (!binding.placesView.isVisible)
                        binding.startBtn.isVisible = true

                    if (isAdded) {
                        (inflatedNameContainer.nameET.layoutParams as ViewGroup.MarginLayoutParams)
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

    private fun dpToPx(context: Context, valueInDp: Float) : Float{
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics)
    }

    @Subscribe
    fun onLastKnownLocationUpdateEvent(e: LastKnownLocationUpdateEvent) {
        if (!App.preferences.lastKnownLocation.isNullOrEmpty() && binding.nameContainerStub.isInflated) {
            requireActivity().runOnUiThread { inflatedNameContainer.placeET.setText(App.preferences.lastKnownLocation) }
            selectedLat = App.preferences.lastKnownLocationLat!!
            selectedLon = App.preferences.lastKnownLocationLon!!
        }
    }

    private fun setupInitTheme() {
        if (App.preferences.isFirstLaunch) {
            App.preferences.isDarkTheme =
                resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            EventBus.getDefault().post(
                UpdateThemeEvent(
                    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> false
                        Configuration.UI_MODE_NIGHT_YES -> true
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                        else -> false
                    }
                )
            )
            updateTheme()

        } else {
//            EventBus.getDefault().post(
//                UpdateThemeEvent(
//                    App.preferences.isDarkTheme
//                )
//            )
        }

        val identify = Identify()
        identify.set("mode", if (App.preferences.isDarkTheme) "dark" else "light")
        Amplitude.getInstance().identify(identify)
    }

    private fun setupLocale() {
        if (App.preferences.isFirstLaunch || App.preferences.locale.isNullOrEmpty()) {
            Amplitude.getInstance().logEvent("first_launch")
            App.preferences.isFirstLaunch = false

            val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language
            App.preferences.locale =
                if (
                    locale == "ru"
                    || locale == "ua"
                    || locale == "kz"
                    || locale == "be"
                    || locale == "uk"
                ) "ru"
                else if (locale == "es") "es"
                else "en"
        }

        Locale.setDefault(Locale(App.preferences.locale))
        val resources = requireActivity().resources
        val config = resources.configuration
        config.setLocale(Locale(App.preferences.locale))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun updateThemeAndLocale() {
        updateTheme()
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

        inflatedNameContainer.placeET.setText(e.place.name)
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

    lateinit var inflatedNameContainer: View
    private fun setupName() {

        if (!::inflatedNameContainer.isInitialized) {
            inflatedNameContainer = binding.nameContainerStub.viewStub!!.inflate()
            setupPlacesView()
        }

        YandexMetrica.reportEvent("StartScreenNameShowen")

        binding.backBtn.isVisible = true

        currentStartPage = StartPage.NAME
        App.preferences.lastLoginPageId = StartPage.NAME.pageId

        addKeyboardDetectListener()

        binding.splash0102Container.alpha0(500) {
            binding.splash0102Container.isVisible = false
        }

        if (App.preferences.isDarkTheme)
            binding.bottomGradient.alpha1(200)

        binding.indicator4.isVisible = true
        binding.indicator5.isVisible = true

        binding.indicator6.alpha0(500) {
            binding.indicator6.isVisible = false
        }
        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.nameContainer.isVisible = true
        inflatedNameContainer.nameContainer.alpha1(500)
//        binding.startBtn.translationY(requireContext().convertDpToPx(-44f), 500)


        inflatedNameContainer.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_name_title))
        inflatedNameContainer.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.start_name_desc))
        inflatedNameContainer.nameET.hint = App.resourcesProvider.getStringLocale(R.string.start_name_hint)

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.next))
    }

    private fun setupDateBirth() {
        YandexMetrica.reportEvent("StartScreenDateShowen")

        currentStartPage = StartPage.DATE_BIRTH
        App.preferences.lastLoginPageId = StartPage.DATE_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -20)
        inflatedNameContainer.date.maxDate = Calendar.getInstance().time
        inflatedNameContainer.date.setDefaultDate(c.time)

        inflatedNameContainer.nameET.alpha0(300) {
            inflatedNameContainer.nameET.isVisible = false
        }
        inflatedNameContainer.date.isVisible = true
        inflatedNameContainer.date.alpha1(500)

        inflatedNameContainer.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_date_title))
        if (App.preferences.locale != "en") {
            inflatedNameContainer.nameDesc.setTextAnimation07(
                inflatedNameContainer.nameET.text.toString() + App.resourcesProvider.getStringLocale(
                    R.string.start_date_desc
                )
            )
        } else {
            inflatedNameContainer.nameDesc.setTextAnimation07(
                    App.resourcesProvider.getStringLocale(R.string.start_date_desc)
            )
        }
    }

    private fun setupTimeBirth() {
        YandexMetrica.reportEvent("StartScreenTimeShowen")

        currentStartPage = StartPage.TIME_BIRTH
        App.preferences.lastLoginPageId = StartPage.TIME_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_time_title))

        val desc = App.resourcesProvider.getStringLocale(R.string.start_time_desc)
        inflatedNameContainer.nameDesc.setTextAnimation07(desc.replace("name", inflatedNameContainer.nameET.text.toString()))

        inflatedNameContainer.skipTime.isVisible = true
        inflatedNameContainer.skipTime.alpha = 1f
        inflatedNameContainer.skipTime.text = (App.resourcesProvider.getStringLocale(R.string.start_time_skip))

        inflatedNameContainer.date.alpha0(300) {
            inflatedNameContainer.date.isVisible = false
        }

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 12)
        c.set(Calendar.MINUTE, 0)

        if (App.preferences.locale == "en")
            inflatedNameContainer.time.setIsAmPm(true)
        else inflatedNameContainer.time.setIsAmPm(false)

        inflatedNameContainer.time.selectDate(c)

        inflatedNameContainer.time.isVisible = true
        inflatedNameContainer.time.alpha1(500)
    }

    private fun setupPlaceBirth() {
        EventBus.getDefault().post(SetupLocationEvent())
        YandexMetrica.reportEvent("StartScreenPlaceShowen")

        currentStartPage = StartPage.PLACE_BIRTH
        App.preferences.lastLoginPageId = StartPage.PLACE_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_place_title))
        inflatedNameContainer.nameDesc.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_place_desc)) {
            inflatedNameContainer.nameDesc.alpha = 0.7f
        }

        inflatedNameContainer.skipTime.alpha0(300) {
            inflatedNameContainer.skipTime.isVisible = false
        }
        inflatedNameContainer.time.alpha0(300) {
            inflatedNameContainer.time.isVisible = false
        }

        if (!isCurrentLocationVariantSet)
            inflatedNameContainer.placeET.hint = App.resourcesProvider.getStringLocale(R.string.start_place_hint)

        if (!App.preferences.lastKnownLocation.isNullOrEmpty())
            inflatedNameContainer.placeET.setText(App.preferences.lastKnownLocation)

        inflatedNameContainer.placeET.isVisible = true
        inflatedNameContainer.placeET.alpha1(500)
    }

    private var isPaywallOpened = false
    private fun setupBodygraph() {
        YandexMetrica.reportEvent("StartScreenBodygraphShowen")

        currentStartPage = StartPage.BODYGRAPH
        App.preferences.lastLoginPageId = StartPage.BODYGRAPH.pageId

        binding.startBtn.isVisible = false
        binding.indicatorsContainer.alpha0(300)

        inflatedNameContainer.nameContainer.alpha0(100) {
            binding.backBtn.isVisible = false
        }

        binding.bodygraphContainer.isVisible = true
        binding.bodygraphContainer.alpha1(500)

        binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.creating_your_bodygraph))
//        binding.bodygraphReadyText.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.start_bodygraph_ready_text))

        if (!App.preferences.isPremiun) {
            binding.startBtn.visibility = View.INVISIBLE
        }

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            if (
                !it.design.channels.isNullOrEmpty()
                && !it.personality.channels.isNullOrEmpty()
            ) {
              android.os.Handler().postDelayed({
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
                      binding.bodygraphReadyTitle.text = App.resourcesProvider.getStringLocale(R.string.start_bodygraph_ready_title)
                      App.preferences.lastLoginPageId = -1
                      App.preferences.userNameFromStart = inflatedNameContainer.nameET.text.toString()

                      if (!App.preferences.isPremiun && !isPaywallOpened) {
                          isPaywallOpened = true
                          android.os.Handler().postDelayed({
                              router.replaceScreen(Screens.paywallScreen(true, source = "start"))
                          }, 1000)
                      } else {
                          android.os.Handler().postDelayed({
                              router.replaceScreen(Screens.bodygraphScreen(fromStart = true))
                          }, 1000)
                      }
                  }, 7500)
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

    private fun setupSplash01() {
        YandexMetrica.reportEvent("Onboarding1Showen")
        Amplitude.getInstance().logEvent("welcome1ScreenShowen")

        binding.indicatorsContainer.alpha1(500)
        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        currentStartPage = StartPage.SPLASH_01
        App.preferences.lastLoginPageId = StartPage.SPLASH_01.pageId

        binding.titleSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.title_splash_01))
        binding.descSplash0102.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.desc_splash_01))

        val smm = ScrollingMovementMethod.getInstance()
        binding.descSplash0102.movementMethod = smm
    }

    private var selectedVariant = -1
    private fun setupSplash02() {
        selectedVariant = -1
        unselectAllVariants()

        YandexMetrica.reportEvent("Onboarding2Showen")
        Amplitude.getInstance().logEvent("welcome2ScreenShowen")

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        currentStartPage = StartPage.SPLASH_02
        App.preferences.lastLoginPageId = StartPage.SPLASH_02.pageId

        binding.titleSplash0102.alpha = 1f
        binding.titleSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_quiz_title))
        binding.descSplash0102.setTextAnimation07("")

        binding.variant1Text.text = App.resourcesProvider.getStringLocale(R.string.start_quiz_variant_1)
        binding.variant2Text.text = App.resourcesProvider.getStringLocale(R.string.start_quiz_variant_2)
        binding.variant3Text.text = App.resourcesProvider.getStringLocale(R.string.start_quiz_variant_3)
        binding.variant4Text.text = App.resourcesProvider.getStringLocale(R.string.start_quiz_variant_4)
        binding.variant5Text.text = App.resourcesProvider.getStringLocale(R.string.start_quiz_variant_5)
        binding.variant6Text.text = App.resourcesProvider.getStringLocale(R.string.start_quiz_variant_6)
        binding.variantsBlock.isVisible = true

        binding.variant1Block.setOnClickListener {
            unselectAllVariants()
            binding.variant1Check.setImageResource(
                if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant
                else R.drawable.ic_quiz_variant_dark
            )
            selectedVariant = 1
        }

        binding.variant2Block.setOnClickListener {
            unselectAllVariants()
            binding.variant2Check.setImageResource(
                if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant
                else R.drawable.ic_quiz_variant_dark
            )
            selectedVariant = 2
        }

        binding.variant3Block.setOnClickListener {
            unselectAllVariants()
            binding.variant3Check.setImageResource(
                if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant
                else R.drawable.ic_quiz_variant_dark
            )
            selectedVariant = 3
        }

        binding.variant4Block.setOnClickListener {
            unselectAllVariants()
            binding.variant4Check.setImageResource(
                if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant
                else R.drawable.ic_quiz_variant_dark
            )
            selectedVariant = 4
        }

        binding.variant5Block.setOnClickListener {
            unselectAllVariants()
            binding.variant5Check.setImageResource(
                if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant
                else R.drawable.ic_quiz_variant_dark
            )
            selectedVariant = 5
        }

        binding.variant6Block.setOnClickListener {
            unselectAllVariants()
            binding.variant6Check.setImageResource(
                if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant
                else R.drawable.ic_quiz_variant_dark
            )
            selectedVariant = 6
        }
    }

    private fun unselectAllVariants() {
        binding.variant1Check.setImageResource(
            if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_empty
            else R.drawable.ic_quiz_variant_empty_dark
        )

        binding.variant2Check.setImageResource(
            if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_empty
            else R.drawable.ic_quiz_variant_empty_dark
        )

        binding.variant3Check.setImageResource(
            if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_empty
            else R.drawable.ic_quiz_variant_empty_dark
        )

        binding.variant4Check.setImageResource(
            if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_empty
            else R.drawable.ic_quiz_variant_empty_dark
        )

        binding.variant5Check.setImageResource(
            if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_empty
            else R.drawable.ic_quiz_variant_empty_dark
        )

        binding.variant6Check.setImageResource(
            if (!App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_empty
            else R.drawable.ic_quiz_variant_empty_dark
        )
    }

    private fun setupSplash03() {
        binding.variantsBlock.isVisible = false
        YandexMetrica.reportEvent("Onboarding2Showen")
        Amplitude.getInstance().logEvent("welcome2ScreenShowen")

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        currentStartPage = StartPage.SPLASH_03
        App.preferences.lastLoginPageId = StartPage.SPLASH_03.pageId

        binding.titleSplash0102.alpha = 1f
        binding.titleSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.rave_title))
        binding.descSplash0102.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.desc_splash_02))

        val smm = ScrollingMovementMethod.getInstance()
        binding.descSplash0102.movementMethod = smm

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_rave_btn_text))
    }

    private fun isNetworkConnected(): Boolean {
        val cm = requireContext().getSystemService(DaggerAppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    inner class Handler {

        fun openPlacesView(v: View) {
            binding.placesView.isVisible = true
            binding.startBtn.isVisible = false
        }

        fun onBackClicked(v: View) {
            animateBackCirclesBtwPages()
            when(currentStartPage) {
                StartPage.NAME -> {
                    binding.backBtn.isVisible = false
                    inflatedNameContainer.nameContainer.alpha0(500) {
                        inflatedNameContainer.nameContainer.isVisible = false
                    }
                    binding.indicator5.isVisible = false
                    binding.indicator4.isVisible = false
//                    binding.indicator3.isVisible = false

                    binding.splash0102Container.isVisible = true
                    binding.splash0102Container.alpha1(500)
//                    binding.startBtn.translationY(requireContext().convertDpToPx(0f), 500)
                    setupSplash03()
                }
                StartPage.DATE_BIRTH -> {
                    inflatedNameContainer.date.alpha0(500) {
                        inflatedNameContainer.date.isVisible = false
                    }
                    inflatedNameContainer.nameET.isVisible = true
                    inflatedNameContainer.nameET.alpha1(500)

                    setupName()
                }
                StartPage.TIME_BIRTH -> {
                    inflatedNameContainer.skipTime.isVisible = false
                    inflatedNameContainer.time.alpha0(500) {
                        inflatedNameContainer.time.isVisible = false
                    }
                    setupDateBirth()
                }
                StartPage.PLACE_BIRTH -> {
                    inflatedNameContainer.placeET.alpha0(500) {
                        inflatedNameContainer.placeET.isVisible = false
                    }
                    inflatedNameContainer.skipTime.alpha = 1f
                    setupTimeBirth()
                }
                StartPage.BODYGRAPH -> {
                    setupPlaceBirth()
                }
            }
        }

        fun onBtnClicked(v: View) {
            when (currentStartPage) {
                StartPage.SPLASH_01 -> {
                    Amplitude.getInstance().logEvent("welcome1screen_next_clicked")
                    binding.startHeaderAnim.playAnimation()
//                    android.os.Handler().postDelayed({
//                        LottieCompositionFactory.fromRawResSync(
//                            requireContext(),
//                            if (App.preferences.isDarkTheme) R.raw.start_header_2_dark
//                            else R.raw.start_header_2_light
//                        )?.let { result ->
//                            result.value?.let { composition -> binding.startHeaderAnim.setComposition(composition) }
//                        }
//                    }, 4000)
                    setupSplash03()
                }
                StartPage.SPLASH_02 -> {
                    if (selectedVariant != -1) {
                        binding.startHeaderAnim.playAnimation()

                        Amplitude.getInstance().logEvent("welcome2screen_next_clicked")

                        val identify = Identify()
                        identify.set("answer", selectedVariant.toString())
                        Amplitude.getInstance().identify(identify)

                        setupSplash03()
                    }
                }
                StartPage.SPLASH_03 -> {
                    animateCirclesBtwPages(1000)
                    Amplitude.getInstance().logEvent("welcome2screen_next_clicked")
                    setupName()
                }
                StartPage.SPLASH_05 -> setupName()
                StartPage.RAVE -> {
                    animateCirclesBtwPages(1000)
                    setupName()
                }
                StartPage.NAME -> {
                    Amplitude.getInstance().logEvent("userTappedStart1")

                    if (inflatedNameContainer.nameET.text.toString().replace(" ", "").isNullOrEmpty()) {
                        snackbarName.show()
                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Keyboard.hide(inflatedNameContainer.nameET)
                        }.invokeOnCompletion {
                            requireActivity().runOnUiThread {
                                animateCirclesBtwPages(1000)
                                setupDateBirth()
                            }
                        }
                    }
                }
                StartPage.DATE_BIRTH -> {
                    Amplitude.getInstance().logEvent("userTappedStart2")

                    animateCirclesBtwPages(1000)
                    setupTimeBirth()
                }
                StartPage.TIME_BIRTH -> {
                    Amplitude.getInstance().logEvent(
                        "userTappedStart3",
                        JSONObject(mutableMapOf(
                            "source" to "fromFirstBodygraphCreating"
                        ).toMap())
                    )

                    animateCirclesBtwPages(1000)
                    setupPlaceBirth()
                }
                StartPage.PLACE_BIRTH -> {
                    Amplitude.getInstance().logEvent("userTappedStart4")

                    if (!isNetworkConnected()) {
                        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                        EventBus.getDefault().post(NoInetEvent())

                        return
                    }

                    if (inflatedNameContainer.placeET.text.toString().replace(" ", "").isNullOrEmpty()) {
                        snackbarAddress.show()
                    } else {
                        requestReviewFlow()
                        animateCirclesBtwPages(1000)

                        lifecycleScope.launch {
                            baseViewModel.createNewUser(
                                name = inflatedNameContainer.nameET.text.toString(),
                                place = inflatedNameContainer.placeET.text.toString(),
                                date = inflatedNameContainer.date.date.time,
                                time = String.format(
                                    "%02d",
                                    inflatedNameContainer.time.date.hours
                                ) + ":" + String.format("%02d", inflatedNameContainer.time.minutesPicker.currentMinute),
                                lat = selectedLat,
                                lon = selectedLon
                            )
                            setupBodygraph()
                        }
                    }

                }
                StartPage.BODYGRAPH -> {
                    App.preferences.lastLoginPageId = -1
                    router.navigateTo(Screens.bodygraphScreen(fromStart = true))
//                    Navigator.startToBodygraph(this@StartFragment)
                }
            }
        }
    }
}


fun TextView.makeGradientAppName(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {

                val textShader: Shader = LinearGradient(
                    0f, 0f, width.toFloat(), textSize, intArrayOf(
                        Color.parseColor("#4D8DBC"),
                        Color.parseColor("#5352BD")
                    ), null, Shader.TileMode.CLAMP
                )

                textPaint.shader = textShader
                textPaint.isUnderlineText = false

            }

            override fun onClick(view: View) {
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

enum class StartPage(val pageId: Int) {
    SPLASH_01(0),
    SPLASH_02(1),
    SPLASH_03(2),
    SPLASH_04(3),
    SPLASH_05(4),
    RAVE(5),
    NAME(6),
    DATE_BIRTH(7),
    TIME_BIRTH(8),
    PLACE_BIRTH(9),
    BODYGRAPH(10)
}