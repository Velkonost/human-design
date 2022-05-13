package ru.get.hd.ui.start

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.single_day_and_time_picker.view.*
import kotlinx.android.synthetic.main.view_place_select.view.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.App.Companion.LOCATION_REQUEST_CODE
import ru.get.hd.R
import ru.get.hd.databinding.FragmentStartBinding
import ru.get.hd.event.LastKnownLocationUpdateEvent
import ru.get.hd.event.PermissionGrantedEvent
import ru.get.hd.event.PlaceSelectedEvent
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.event.UpdateThemeEvent
import ru.get.hd.model.Place
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.splash.SplashPage
import ru.get.hd.ui.start.adapter.PlacesAdapter
import ru.get.hd.util.Keyboard
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.alpha0
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setImageAnimation
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07
import ru.get.hd.util.ext.translationY
import ru.get.hd.vm.BaseViewModel
import java.util.*
import android.graphics.Shader

import android.graphics.LinearGradient
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import dagger.android.support.DaggerAppCompatActivity
import ru.get.hd.event.NoInetEvent
import ru.get.hd.util.ext.scaleXY
import ru.get.hd.util.ext.setTextAnimationWithGradientAppName


class StartFragment : BaseFragment<StartViewModel, FragmentStartBinding>(
    R.layout.fragment_start,
    StartViewModel::class,
    Handler::class
) {

    var currentStartPage: StartPage = StartPage.RAVE

//    private lateinit var geocoder: Geocoder
    private var isCurrentLocationVariantSet = false

    private var selectedLat = ""
    private var selectedLon = ""

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val placesAdapter: PlacesAdapter by lazy {
        PlacesAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

//        geocoder = Geocoder(requireContext(), Locale.getDefault())



//        mLocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
//        setupLocationListener()

        setupInitTheme()
        setupLocale()

        prepareLogic()
        startCirclesRotation(StartPage.RAVE)

        when (App.preferences.lastLoginPageId) {
            SplashPage.SPLASH_01.pageId -> setupSplash01()
            SplashPage.SPLASH_02.pageId -> setupSplash02()
            SplashPage.SPLASH_03.pageId -> setupSplash03()
            SplashPage.SPLASH_04.pageId -> setupSplash04()
            SplashPage.SPLASH_05.pageId -> setupSplash05()
            StartPage.RAVE.pageId -> setupRave()
//            StartPage.NAME.pageId -> {
//                animateCirclesBtwPages(1000)
//                setupName()
//            }
//            StartPage.DATE_BIRTH.pageId -> {
//                animateCirclesBtwPages(1000)
//                setupDateBirth()
//            }
//            StartPage.TIME_BIRTH.pageId -> {
//                animateCirclesBtwPages(1000)
//                setupTimeBirth()
//            }
//            StartPage.PLACE_BIRTH.pageId -> {
//                animateCirclesBtwPages(1000)
//                setupPlaceBirth()
//            }
//            StartPage.BODYGRAPH.pageId -> {
//                animateCirclesBtwPages(1000)
//                setupBodygraph()
//            }
            else -> setupRave()
        }

        setupPlacesView()

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

        if (!App.preferences.lastKnownLocation.isNullOrEmpty()) {
            binding.placeET.setText(App.preferences.lastKnownLocation)
            selectedLat = App.preferences.lastKnownLocationLat!!
            selectedLon = App.preferences.lastKnownLocationLon!!
        }
    }

    private fun addKeyboardDetectListener(){
        binding.startContainer.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDifference = binding.startContainer.rootView.height - binding.startContainer.height
            if(heightDifference > dpToPx(requireContext(), 200F)){
                binding.indicatorsContainer.visibility = View.GONE
                binding.startBtn.visibility = View.GONE

                (binding.nameET.layoutParams as ViewGroup.MarginLayoutParams)
                    .setMargins(
                        dpToPx(requireContext(), 20f).toInt(),
                        0,
                        dpToPx(requireContext(), 20f).toInt(),
                        0
                    )
                binding.nameET.requestLayout()

            } else {
                android.os.Handler().postDelayed({
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
                }, 10)
            }
        }
    }

    private fun dpToPx(context: Context, valueInDp: Float) : Float{
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics)
    }

    @Subscribe
    fun onPermissionGrantedEvent(e: PermissionGrantedEvent) {
//        setupLocationListener()
    }

    @Subscribe
    fun onLastKnownLocationUpdateEvent(e: LastKnownLocationUpdateEvent) {
        if (!App.preferences.lastKnownLocation.isNullOrEmpty()) {
            requireActivity().runOnUiThread { binding.placeET.setText(App.preferences.lastKnownLocation) }
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
            binding.date.updateTheme()
            binding.time.updateTheme()
        } else {
//            EventBus.getDefault().post(
//                UpdateThemeEvent(
//                    App.preferences.isDarkTheme
//                )
//            )
        }
    }

    private fun setupLocale() {
        if (App.preferences.isFirstLaunch || App.preferences.locale.isNullOrEmpty()) {
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
                else "en"
        }

        Locale.setDefault(Locale(App.preferences.locale))
        val resources = requireActivity().resources
        val config = resources.configuration
        config.setLocale(Locale(App.preferences.locale))
        resources.updateConfiguration(config, resources.displayMetrics)
    }


//    lateinit var mLocationManager: LocationManager
//    var LOCATION_REFRESH_TIME = 5000L
//    var LOCATION_REFRESH_DISTANCE = 500f

//    val mLocationListener: LocationListener = LocationListener {
//        if (::geocoder.isInitialized) {
//            kotlin.runCatching {
//                val currentLocationVariants =
//                    geocoder.getFromLocation(it.latitude, it.longitude, 10)
//
//                if (
//                    currentLocationVariants.isNotEmpty()
//                    && currentLocationVariants.any { variant ->
//                        !variant.locality.isNullOrEmpty() && !variant.countryName.isNullOrEmpty()
//                    }
//                ) {
//                    isCurrentLocationVariantSet = true
//
//                    selectedLat = currentLocationVariants[0].latitude.toString()
//                    selectedLon = currentLocationVariants[0].longitude.toString()
//                    binding.placeET.setText("${currentLocationVariants[0].locality}, ${currentLocationVariants[0].countryName}")
//                }
//            }
//        }
//    }

    override fun updateThemeAndLocale() {
        updateTheme()
    }

    override fun onViewModelReady(viewModel: StartViewModel) {
        super.onViewModelReady(viewModel)

//        viewModel.suggestions.observe(this) {
//            val addresses: MutableList<Place> = mutableListOf()
//
//            it.features.forEach { feature ->
//                if (addresses.none { it.name == feature.placeName }) {
//                    addresses.add(
//                        Place(
//                            name = feature.placeName,
//                            lat = feature.center[0].toString(),
//                            lon = feature.center[1].toString()
//                        )
//
//                    )
//                }
//            }
//            placesAdapter.createList(addresses.toList())
//        }

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
//        placesAdapter.setHasStableIds(true)

        binding.placesView.icArrowPlace.setOnClickListener {
            Keyboard.hide(requireActivity())
            binding.placesView.isVisible = false
            binding.startBtn.isVisible = true
            placesAdapter.createList(emptyList(), false)
        }

        binding.placesView.newPlaceET.addTextChangedListener {
            if (!binding.placesView.newPlaceET.text.isNullOrEmpty()) {
                binding.viewModel!!.geocodingNominatim(binding.placesView.newPlaceET.text.toString())
            }
        }
    }

//    private suspend fun getSuggestions(): List<Address> =
//        coroutineScope {
//            withContext(Dispatchers.IO) {
//                geocoder.getFromLocationName(binding.placesView.newPlaceET.text.toString(), 300)
//            }
//        }

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
        binding.splash0304Container.alpha = 0f
        binding.splash05Container.alpha = 0f

        binding.cardVariant1.alpha = 0f
        binding.cardVariant1.isVisible = false

        binding.cardVariant2.alpha = 0f
        binding.cardVariant2.isVisible = false

        binding.cardVariant3.alpha = 0f
        binding.cardVariant3.isVisible = false

        binding.cardVariant4.alpha = 0f
        binding.cardVariant4.isVisible = false

        binding.cardVariant5.alpha = 0f
        binding.cardVariant5.isVisible = false

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
        binding.indicator5.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )


        binding.splash05Container.alpha0(500) {
            binding.splash05Container.isVisible = false
        }
        binding.raveContainer.alpha1(500)


        binding.raveTitle.setTextAnimation(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.rave_title)) {
            binding.raveLogo.alpha1(100)
        }
        binding.raveDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.rave_desc))

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_rave_btn_text))
    }

    private fun setupName() {
        binding.backBtn.isVisible = true

        currentStartPage = StartPage.NAME
        App.preferences.lastLoginPageId = StartPage.NAME.pageId

        addKeyboardDetectListener()

        binding.splash0102Container.alpha0(500) {
            binding.splash0102Container.isVisible = false
        }

        if (App.preferences.isDarkTheme)
            binding.bottomGradient.alpha1(200)

        binding.indicator3.isVisible = true
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

        binding.nameContainer.isVisible = true
        binding.raveContainer.alpha0(500)
        binding.nameContainer.alpha1(500)
//        binding.startBtn.translationY(requireContext().convertDpToPx(-44f), 500)


        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_name_title))
        binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.start_name_desc))
        binding.nameET.hint = App.resourcesProvider.getStringLocale(R.string.start_name_hint)

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.next))
    }

    private fun setupDateBirth() {
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
        binding.date.setDefaultDate(c.time)

        binding.nameET.alpha0(300) {
            binding.nameET.isVisible = false
        }
        binding.date.isVisible = true
        binding.date.alpha1(500)

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_date_title))
        binding.nameDesc.setTextAnimation07(
            binding.nameET.text.toString() + App.resourcesProvider.getStringLocale(
                R.string.start_date_desc
            )
        )
    }

    private fun setupTimeBirth() {
        currentStartPage = StartPage.TIME_BIRTH
        App.preferences.lastLoginPageId = StartPage.TIME_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_time_title))
        binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.start_time_desc))

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

        binding.time.selectDate(c)

        binding.time.isVisible = true
        binding.time.alpha1(500)

    }

    private fun setupPlaceBirth() {
        currentStartPage = StartPage.PLACE_BIRTH
        App.preferences.lastLoginPageId = StartPage.PLACE_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_place_title))
        binding.nameDesc.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_place_desc)) {
            binding.nameDesc.alpha = 0.7f
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
        App.preferences.lastLoginPageId = StartPage.BODYGRAPH.pageId

        binding.backBtn.isVisible = false

        binding.indicatorsContainer.alpha0(300)

//        binding.startBtn.translationY(requireContext().convertDpToPx(0f), 500)
        binding.nameContainer.alpha0(300)

        binding.bodygraphContainer.isVisible = true
        binding.bodygraphContainer.alpha1(500)

        binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_bodygraph_ready_title))
        binding.bodygraphReadyText.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.start_bodygraph_ready_text))

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            if (
                !it.design.channels.isNullOrEmpty()
                && !it.personality.channels.isNullOrEmpty()
                && !it.activeCentres.isNullOrEmpty()
                && !it.inactiveCentres.isNullOrEmpty()
            ) {
              android.os.Handler().postDelayed({
                  binding.bodygraphView.isVisible = true
                  binding.bodygraphView.scaleXY(1f, 1f, 1500) {}
              }, 200)

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

    private fun unselectAllVariants() {
        binding.strokeVariant1.alpha0(500)
        binding.strokeVariant2.alpha0(500)
        binding.strokeVariant3.alpha0(500)
        binding.strokeVariant4.alpha0(500)
        binding.strokeVariant5.alpha0(500)
    }

    private fun setupSplash01() {
        binding.indicatorsContainer.alpha1(500)
        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        currentStartPage = StartPage.SPLASH_01
        App.preferences.lastLoginPageId = StartPage.SPLASH_01.pageId

//        binding.icSplash0102Header.setImageAnimation(
//            resId =
//            if (App.preferences.isDarkTheme) R.drawable.ic_splash_01_header_dark
//            else R.drawable.ic_splash_01_header_light
//        )

        binding.titleSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.title_splash_01))
        binding.descSplash0102.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.desc_splash_01))
    }


    private fun setupSplash02() {
        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )


        currentStartPage = StartPage.SPLASH_02
        App.preferences.lastLoginPageId = StartPage.SPLASH_02.pageId

//        if (!App.preferences.isDarkTheme) {
//            binding.icSplash0102Header.setImageAnimation(
//                resId = R.drawable.ic_splash_02_header_light
//            )
//        }

        binding.titleSplash0102.alpha = 1f
        binding.descSplash0102.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.desc_splash_02))

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_rave_btn_text))
    }

    private fun setupSplash03() {
        currentStartPage = StartPage.SPLASH_03
        App.preferences.lastLoginPageId = StartPage.SPLASH_03.pageId

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )


        binding.splash0102Container.alpha0(500) {
            binding.splash0102Container.isVisible = false
        }

        binding.splash0304Container.isVisible = true
        binding.splash0304Container.alpha1(500) {
            binding.cardVariant1.alpha1(300)
            binding.cardVariant2.alpha1(300)
            binding.cardVariant3.alpha1(300)
            binding.cardVariant4.alpha1(300)
        }

        binding.titleSplash0304.setTextAnimationWithGradientAppName(App.resourcesProvider.getStringLocale(R.string.title_splash_03))
        binding.descSplash0304.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.desc_splash_03))

        binding.cardVariant1.isVisible = true
        binding.cardVariant2.isVisible = true
        binding.cardVariant3.isVisible = true
        binding.cardVariant4.isVisible = true

        binding.textVariant1.text =
            Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_1_splash_03))
        binding.textVariant2.text =
            Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_2_splash_03))
        binding.textVariant3.text =
            Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_3_splash_03))
        binding.textVariant4.text =
            Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_4_splash_03))


    }

    private fun setupSplash04() {
        currentStartPage = StartPage.SPLASH_04
        App.preferences.lastLoginPageId = SplashPage.SPLASH_04.pageId

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )


        unselectAllVariants()

        binding.titleSplash0304.setTextAnimationWithGradientAppName(App.resourcesProvider.getStringLocale(R.string.title_splash_04))
        binding.descSplash0304.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.desc_splash_04))

        binding.textVariant1.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_1_splash_04))
        binding.textVariant2.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_2_splash_04))
        binding.textVariant3.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_3_splash_04))
        binding.textVariant4.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_4_splash_04))
        binding.textVariant5.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_5_splash_04))

        binding.cardVariant5.isVisible = true
        binding.cardVariant5.alpha1(500)
    }

    private fun setupSplash05() {
        currentStartPage = StartPage.SPLASH_05
        App.preferences.lastLoginPageId = SplashPage.SPLASH_05.pageId
        setupRave()

        binding.splash0304Container.alpha0(500) {
            binding.splash0304Container.isVisible = false
        }
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
                    binding.nameContainer.alpha0(500) {
                        binding.nameContainer.isVisible = false
                    }
                    binding.indicator5.isVisible = false
                    binding.indicator4.isVisible = false
                    binding.indicator3.isVisible = false

                    binding.splash0102Container.isVisible = true
                    binding.splash0102Container.alpha1(500)
//                    binding.startBtn.translationY(requireContext().convertDpToPx(0f), 500)
                    setupSplash02()
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
                StartPage.SPLASH_01 -> {
                    binding.startHeaderAnim.playAnimation()
                    setupSplash02()
                }
                StartPage.SPLASH_02 -> setupName()
                StartPage.SPLASH_03 -> setupSplash04()
                StartPage.SPLASH_04 -> setupSplash05()
                StartPage.SPLASH_05 -> setupRave()
                StartPage.RAVE -> {
                    animateCirclesBtwPages(1000)
                    setupName()
                }
                StartPage.NAME -> {
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
                    animateCirclesBtwPages(1000)
                    setupTimeBirth()
                }
                StartPage.TIME_BIRTH -> {
                    animateCirclesBtwPages(1000)
                    setupPlaceBirth()
                }
                StartPage.PLACE_BIRTH -> {
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
                            baseViewModel.createNewUser(
                                name = binding.nameET.text.toString(),
                                place = binding.placeET.text.toString(),
                                date = binding.date.date.time,
                                time = String.format(
                                    "%02d",
                                    binding.time.hoursPicker.currentHour
                                ) + ":" + String.format("%02d", binding.time.minutesPicker.currentMinute),
                                lat = selectedLat,
                                lon = selectedLon
                            )
                            setupBodygraph()
                        }
                    }

                }
                StartPage.BODYGRAPH -> {
                    App.preferences.lastLoginPageId = -1
                    Log.d("keke", App.preferences.lastLoginPageId.toString())
                    router.navigateTo(Screens.bodygraphScreen(fromStart = true))
//                    Navigator.startToBodygraph(this@StartFragment)
                }
            }
        }

        fun onVariantClicked(v: View) {
            val selectedStroke = when (v.id) {
                binding.cardVariant1.id -> binding.strokeVariant1
                binding.cardVariant2.id -> binding.strokeVariant2
                binding.cardVariant3.id -> binding.strokeVariant3
                binding.cardVariant4.id -> binding.strokeVariant4
                binding.cardVariant5.id -> binding.strokeVariant5
                else -> binding.strokeVariant1
            }

            if (selectedStroke.alpha == 0f) {
//                unselectAllVariants()
                selectedStroke.alpha1(500)
            } else {
                selectedStroke.alpha0(500)
//                unselectAllVariants()
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