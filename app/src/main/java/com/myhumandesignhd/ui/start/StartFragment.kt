package com.myhumandesignhd.ui.start

import android.animation.Animator
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
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
import android.view.inputmethod.InputMethodManager
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
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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
import com.myhumandesignhd.ui.start.ext.handleLoginGoogleResult
import com.myhumandesignhd.ui.start.ext.setupSignup
import com.myhumandesignhd.util.Keyboard
import com.myhumandesignhd.util.ext.alpha0
import com.myhumandesignhd.util.ext.alpha1
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.container_start_name.view.*
import kotlinx.android.synthetic.main.item_place.view.*
import kotlinx.android.synthetic.main.single_day_and_time_picker.view.*
import kotlinx.android.synthetic.main.view_onboarding.view.*
import kotlinx.android.synthetic.main.view_place_select.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.*


class StartFragment : BaseFragment<StartViewModel, FragmentStartBinding>(
    R.layout.fragment_start,
    StartViewModel::class,
    Handler::class
) {
    
    companion object {
        const val GOOGLE_REQUEST_CODE = 93472
    }

    val facebookCallbackManager = CallbackManager.Factory.create()

    

    var currentStartPage: StartPage = StartPage.RAVE

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

        setupInitTheme()
        setupLoader()
        setupLocale()

        startCirclesRotation(StartPage.RAVE)

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

        binding.nameContainerStub.setOnInflateListener { stub, inflated ->
            if (!App.preferences.lastKnownLocation.isNullOrEmpty()) {
                inflated.placeET.setText(App.preferences.lastKnownLocation)
                selectedLat = App.preferences.lastKnownLocationLat!!
                selectedLon = App.preferences.lastKnownLocationLon!!
            }

            inflated.icCirclesBodygraph.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_splash_circles_dark
                else R.drawable.ic_splash_circles_light
            )

            inflated.skipTime.setOnClickListener {
                Amplitude.getInstance().logEvent("userTappedStart3_dontnow");

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

//            inflated.nameET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
//                requireContext(),
//                if (App.preferences.isDarkTheme) R.color.darkHintColor
//                else R.color.lightHintColor
//            ))

//            inflated.placeET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
//                requireContext(),
//                if (App.preferences.isDarkTheme) R.color.darkHintColor
//                else R.color.lightHintColor
//            ))

            inflated.date.updateTheme()
            inflated.time.updateTheme()
        }
    }

    private fun setupLoader() {
        binding.loaderView.anim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_REQUEST_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleLoginGoogleResult(task)

        }
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
//                inflatedNameContainer.nameET.isVisible = false
                inflatedNameContainer.nameET.requestLayout()

                binding.indicatorsContainer.visibility = View.GONE
                binding.startBtn.visibility = View.GONE

//                (inflatedNameContainer.nameET.layoutParams as ViewGroup.MarginLayoutParams)
//                    .setMargins(
//                        dpToPx(requireContext(), 20f).toInt(),
//                        0,
//                        dpToPx(requireContext(), 20f).toInt(),
//                        0
//                    )

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

//                    (inflatedNameContainer.nameET.layoutParams as ViewGroup.MarginLayoutParams)
//                        .setMargins(
//                            dpToPx(requireContext(), 20f).toInt(),
//                            0,
//                            dpToPx(requireContext(), 20f).toInt(),
//                            dpToPx(requireContext(), 46f).toInt()
//                        )
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
            Amplitude.getInstance().logEvent("first_launch");
            App.preferences.isFirstLaunch = false

            val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language
            App.preferences.locale =
                when (locale) {
                    "ru", "ua", "kz", "be", "uk" -> "ru"
                    "es" -> "es"
                    else -> "en"
                }
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

    fun setupLetsCalculate() {
        binding.backBtn.isVisible = true

        if (!::inflatedNameContainer.isInitialized) {
            inflatedNameContainer = binding.nameContainerStub.viewStub!!.inflate()
            setupPlacesView()
        }

        binding.splash0102Container.alpha0(100) {
            binding.splash0102Container.isVisible = false

            inflatedNameContainer.nameContainer.isVisible = true
            inflatedNameContainer.nameContainer.alpha1(500)
        }

        currentStartPage = StartPage.CALCULATE
        App.preferences.lastLoginPageId = StartPage.CALCULATE.pageId

        binding.indicator4.isVisible = true
        binding.indicator5.isVisible = true
        binding.indicator6.isVisible = true

        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.nameTitle.text = App.resourcesProvider.getStringLocale(R.string.calculate_title)
        inflatedNameContainer.nameDesc.text = App.resourcesProvider.getStringLocale(R.string.calculate_desc)
        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.next))
    }

    lateinit var inflatedNameContainer: View
    private fun setupName() {
        if (!::inflatedNameContainer.isInitialized) {
            inflatedNameContainer = binding.nameContainerStub.viewStub!!.inflate()
            setupPlacesView()
        }

        binding.signupView.isVisible = false

        inflatedNameContainer.nameET.isVisible = true
        inflatedNameContainer.nameET.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_et_dark
            else R.drawable.bg_et_light
        )
        inflatedNameContainer.nameET.requestFocus()
        val imm: InputMethodManager? =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(inflatedNameContainer.nameET, InputMethodManager.SHOW_IMPLICIT)

        YandexMetrica.reportEvent("StartScreenNameShowen")

        binding.backBtn.isVisible = true

        currentStartPage = StartPage.NAME
        App.preferences.lastLoginPageId = StartPage.NAME.pageId

        addKeyboardDetectListener()

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.nameContainer.isVisible = true
        inflatedNameContainer.nameContainer.alpha1(500)
//        binding.startBtn.translationY(requireContext().convertDpToPx(-44f), 500)


        inflatedNameContainer.nameTitle.text = App.resourcesProvider.getStringLocale(R.string.start_name_title)
        inflatedNameContainer.nameDesc.text = App.resourcesProvider.getStringLocale(R.string.start_name_desc)
        inflatedNameContainer.nameET.hint = App.resourcesProvider.getStringLocale(R.string.start_name_hint)

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.next))
    }

    private fun setupDateBirth() {
        YandexMetrica.reportEvent("StartScreenDateShowen")

        currentStartPage = StartPage.DATE_BIRTH
        App.preferences.lastLoginPageId = StartPage.DATE_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        val c = Calendar.getInstance()
        c.add(Calendar.YEAR, -20)
        inflatedNameContainer.date.maxDate = Calendar.getInstance().time
        inflatedNameContainer.date.setDefaultDate(c.time)

        inflatedNameContainer.nameET.isVisible = false

        inflatedNameContainer.date.isVisible = true
        inflatedNameContainer.date.alpha1(500)

        inflatedNameContainer.nameTitle.text = App.resourcesProvider.getStringLocale(R.string.start_date_title)
        inflatedNameContainer.nameDesc.text =
            inflatedNameContainer.nameET.text.toString()  + ", " + App.resourcesProvider.getStringLocale(
                R.string.start_date_desc
        )
    }

    private fun setupTimeBirth() {
        YandexMetrica.reportEvent("StartScreenTimeShowen")

        currentStartPage = StartPage.TIME_BIRTH
        App.preferences.lastLoginPageId = StartPage.TIME_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.nameTitle.text = App.resourcesProvider.getStringLocale(R.string.start_time_title)

        val desc = App.resourcesProvider.getStringLocale(R.string.start_time_desc)
        inflatedNameContainer.nameDesc.text = desc//.replace("name", inflatedNameContainer.nameET.text.toString())

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
        binding.indicator5.background = ContextCompat.getDrawable(
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

        inflatedNameContainer.placeET.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_et_dark
            else R.drawable.bg_et_light
        )
        inflatedNameContainer.placeET.isVisible = true
        inflatedNameContainer.placeET.alpha1(500) {
            inflatedNameContainer.placeET.requestFocus()
        }
    }

    private var isPaywallOpened = false
    private fun setupBodygraph() {
        YandexMetrica.reportEvent("StartScreenBodygraphShowen")

        currentStartPage = StartPage.BODYGRAPH
        App.preferences.lastLoginPageId = StartPage.BODYGRAPH.pageId

        unselectAllIndicators()
        binding.indicator6.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        inflatedNameContainer.placeET.isVisible = false

        binding.startBtnText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.continue_btn))

        inflatedNameContainer.nameTitle.text = App.resourcesProvider.getStringLocale(R.string.start_bodygraph_ready_title)
        inflatedNameContainer.nameDesc.text = App.resourcesProvider.getStringLocale(R.string.bodygraph_is_ready_desc)

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            if (
                !it.design.channels.isNullOrEmpty()
                && !it.personality.channels.isNullOrEmpty()
            ) {
              android.os.Handler().postDelayed({
                  inflatedNameContainer.bodygraphView.isVisible = true
                  inflatedNameContainer.icCirclesBodygraph.isVisible = true
                  inflatedNameContainer.bodygraphView.scaleXY(1.1f, 1.1f, 1500) {
                      inflatedNameContainer.bodygraphView.changeSpeedAnimationFactor(10f)
                      inflatedNameContainer.bodygraphView.changeIsAllowDrawLinesState(true)
                  }
              }, 700)

            }

            inflatedNameContainer.bodygraphView.setupData(
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

        binding.indicator6.background = ContextCompat.getDrawable(
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
        Amplitude.getInstance().logEvent("welcome1ScreenShowen");

        binding.indicatorsContainer.alpha1(500)
        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        currentStartPage = StartPage.SPLASH_01
        App.preferences.lastLoginPageId = StartPage.SPLASH_01.pageId

        binding.viewOnboarding.icOnboarding.setImageResource(R.drawable.ic_onboarding_1)
        binding.viewOnboarding.onboardingTitle.text = App.resourcesProvider.getStringLocale(R.string.onboarding_title_1)
        binding.viewOnboarding.onboardingDesc.text = App.resourcesProvider.getStringLocale(R.string.onboarding_desc_1)
        binding.viewOnboarding.onboardingDesc.movementMethod = ScrollingMovementMethod()
    }

    private fun setupSplash02() {
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

        binding.viewOnboarding.icOnboarding.setImageResource(R.drawable.ic_onboarding_3)
        binding.viewOnboarding.onboardingTitle.text = App.resourcesProvider.getStringLocale(R.string.onboarding_title_2)
        binding.viewOnboarding.onboardingDesc.text = App.resourcesProvider.getStringLocale(R.string.onboarding_desc_2)
        binding.viewOnboarding.onboardingVariants.isVisible = true
    }

    private fun setupSplash03() {
        YandexMetrica.reportEvent("Onboarding2Showen")
        Amplitude.getInstance().logEvent("welcome2ScreenShowen")

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        currentStartPage = StartPage.SPLASH_03
        App.preferences.lastLoginPageId = StartPage.SPLASH_03.pageId

        binding.viewOnboarding.icOnboarding.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_onboarding_3_dark
            else R.drawable.ic_onboarding_3_light
        )
        binding.viewOnboarding.onboardingTitle.text = App.resourcesProvider.getStringLocale(R.string.onboarding_title_3)
        binding.viewOnboarding.onboardingDesc.text = App.resourcesProvider.getStringLocale(R.string.onboarding_desc_3)
        binding.viewOnboarding.onboardingVariants.isVisible = false

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
                StartPage.CALCULATE -> {
                    binding.backBtn.visibility = View.INVISIBLE
                    inflatedNameContainer.nameContainer.alpha0(500) {
                        inflatedNameContainer.nameContainer.isVisible = false
                    }
                    binding.indicator5.isVisible = false
                    binding.indicator4.isVisible = false

                    binding.splash0102Container.isVisible = true
                    binding.splash0102Container.alpha1(500)
                    setupSplash03()
                }
                StartPage.NAME -> {
                    Keyboard.hide(inflatedNameContainer.nameET)
                    inflatedNameContainer.nameET.isVisible = false
                    setupLetsCalculate()
                }
                StartPage.DATE_BIRTH -> {

                    inflatedNameContainer.date.isVisible = false

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
                    inflatedNameContainer.bodygraphView.isVisible = false
                    inflatedNameContainer.icCirclesBodygraph.isVisible = false
                    setupPlaceBirth()
                }
            }
        }

        fun onBtnClicked(v: View) {
            when (currentStartPage) {
                StartPage.SPLASH_01 -> {
                    Amplitude.getInstance().logEvent("welcome1screen_next_clicked");
                    setupSplash02()
                }
                StartPage.SPLASH_02 -> {
                    Amplitude.getInstance().logEvent("welcome2screen_next_clicked")
                    setupSplash03()
                }
                StartPage.SPLASH_03 -> {
                    animateCirclesBtwPages(1000)
                    Amplitude.getInstance().logEvent("welcome3screen_next_clicked");
                    setupSignup()
                }
                StartPage.SPLASH_05 -> setupSignup()
                StartPage.CALCULATE -> setupName()
                StartPage.RAVE -> {
                    animateCirclesBtwPages(1000)
                    setupName()
                }
                StartPage.SIGNUP -> {
                    setupLetsCalculate()
                }
                StartPage.NAME -> {
                    Amplitude.getInstance().logEvent("userTappedStart1");

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
                    Amplitude.getInstance().logEvent("userTappedStart2");

                    animateCirclesBtwPages(1000)
                    setupTimeBirth()
                }
                StartPage.TIME_BIRTH -> {
                    Amplitude.getInstance().logEvent(
                        "userTappedStart3",
                        JSONObject(mutableMapOf(
                            "source" to "fromFirstBodygraphCreating"
                        ).toMap())
                    );

                    animateCirclesBtwPages(1000)
                    setupPlaceBirth()
                }
                StartPage.PLACE_BIRTH -> {
                    Amplitude.getInstance().logEvent("userTappedStart4");

                    if (!isNetworkConnected()) {
                        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                        EventBus.getDefault().post(NoInetEvent())

                        return
                    }

                    if (inflatedNameContainer.placeET.text.toString().replace(" ", "").isNullOrEmpty()) {
                        snackbarAddress.show()
                    } else {
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
//                    router.navigateTo(Screens.bodygraphScreen(fromStart = true))
                    router.navigateTo(Screens.descriptionScreen(fromStart = true))
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
    CALCULATE(56),
    NAME(6),
    DATE_BIRTH(7),
    TIME_BIRTH(8),
    PLACE_BIRTH(9),
    BODYGRAPH(10),
    SIGNUP(10)
}