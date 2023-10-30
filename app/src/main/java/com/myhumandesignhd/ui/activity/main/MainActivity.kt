package com.myhumandesignhd.ui.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.adapty.Adapty
import com.adapty.models.AdaptyAttributionSource
import com.adapty.models.AdaptyProfileParameters
import com.adapty.utils.AdaptyResult
import com.amplitude.api.Amplitude
import com.android.installreferrer.api.InstallReferrerClient
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jaeger.library.StatusBarUtil
import com.myhumandesignhd.App
import com.myhumandesignhd.App.Companion.LOCATION_REQUEST_CODE
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.ActivityMainBinding
import com.myhumandesignhd.event.ActivateAdaptyEvent
import com.myhumandesignhd.event.AdaptyLogShowEvent
import com.myhumandesignhd.event.ContinueFirstLoaderEvent
import com.myhumandesignhd.event.FinishFirstLoaderEvent
import com.myhumandesignhd.event.LastKnownLocationUpdateEvent
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.OpenBodygraphEvent
import com.myhumandesignhd.event.SelectNavItemEvent
import com.myhumandesignhd.event.SetInjuryAlarmEvent
import com.myhumandesignhd.event.SetupLocationEvent
import com.myhumandesignhd.event.SetupNavMenuEvent
import com.myhumandesignhd.event.SetupNotificationsEvent
import com.myhumandesignhd.event.TestResponseEvent
import com.myhumandesignhd.event.UpdateBalloonBgStateEvent
import com.myhumandesignhd.event.UpdateHadrdwareAccelerationStateEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.navigation.SupportAppNavigator
import com.myhumandesignhd.push.NotificationReceiver
import com.myhumandesignhd.ui.activity.main.ext.activateAdapty
import com.myhumandesignhd.ui.base.BaseActivity
import com.myhumandesignhd.ui.bodygraph.BodygraphFragment
import com.myhumandesignhd.ui.start.StartPage
import com.myhumandesignhd.util.MyLocation
import com.myhumandesignhd.util.SingleShotLocationProvider
import com.myhumandesignhd.util.SingleShotLocationProvider.GPSCoordinates
import com.myhumandesignhd.util.SingleShotLocationProvider.requestSingleUpdate
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*


class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
) {

    private lateinit var navigator: SupportAppNavigator

    private val navigationHolder by lazy { App.instance.navigatorHolder }

    private val router by lazy { App.instance.router }

    private var navController: NavController? = null
    lateinit var referrerClient: InstallReferrerClient

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        Amplitude.getInstance().initialize(this, "179ff8d2378573275e642a2ecf49e3b3")
            .enableForegroundTracking(application)
        Amplitude.getInstance().logEvent("start_session")

        App.preferences.isCompatibilityFromChild = false
        if (!isNetworkConnected()) {
            snackbarInet.show()
            android.os.Handler().postDelayed({
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }, 4000)

            return
        }

        if (savedInstanceState == null) {
            activateAdapty()
            receiveLinkData()
            initStartPage()
        }

        navigator = SupportAppNavigator(this, R.id.subContainer)
        navigationHolder.setNavigator(navigator)

        binding.navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_bodygraph -> {
                    YandexMetrica.reportEvent("Tab1Tapped")
                    Amplitude.getInstance().logEvent("tab1Tapped")

                    if (supportFragmentManager.fragments.last() !is BodygraphFragment)
                        router.navigateTo(Screens.bodygraphScreen(needUpdateNavMenu = false))

//                    if (supportFragmentManager.fragments.last() !is DescriptionFragment)
//                        router.navigateTo(Screens.descriptionScreen(needUpdateNavMenu = false))

                    true
                }

                R.id.navigation_description -> {
                    YandexMetrica.reportEvent("Tab2Tapped")
                    Amplitude.getInstance().logEvent("tab2Tapped")

                    if (App.preferences.isPremiun) {
                        router.navigateTo(Screens.bodygraphSecondScreen())
                        true
                    } else {
                        router.navigateTo(Screens.paywallScreen(source = "description"))
                        false
                    }
                }

                R.id.navigation_transit -> {
                    YandexMetrica.reportEvent("Tab3Tapped")
                    Amplitude.getInstance().logEvent("tab3Tapped")

                    if (App.preferences.isPremiun) {
                        router.navigateTo(Screens.transitScreen())
                        true
                    } else {
                        router.navigateTo(Screens.paywallScreen(source = "transit"))
                        false
                    }
                }

                R.id.navigation_compatibility -> {
                    YandexMetrica.reportEvent("Tab4Tapped")
                    Amplitude.getInstance().logEvent("tab4Tapped")

                    if (App.preferences.isPremiun) {
                        router.navigateTo(Screens.compatibilityScreen())
                        true
                    } else {
                        router.navigateTo(Screens.paywallScreen(source = "compatibility"))
                        false
                    }
                }

                R.id.navigation_affirmation -> {
                    YandexMetrica.reportEvent("Tab5Tapped")
                    Amplitude.getInstance().logEvent("tab5Tapped")

                    if (App.preferences.isPremiun) {
                        router.navigateTo(Screens.affirmationScreen())
                        true
                    } else {
                        router.navigateTo(Screens.paywallScreen(source = "insights"))
                        false
                    }
                }

                else -> {
                    router.navigateTo(Screens.settingsScreen())
                    true
                }
            }

        }

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale(PERMISSION_REQUEST_CODE.toString())){
                getNotificationPermission()
            }
        }
    }

    @Subscribe
    fun onActivateAdaptyEvent(e: ActivateAdaptyEvent) {
        activateAdapty()
    }

    private val PERMISSION_REQUEST_CODE = 112
    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private var navigateToInjury = false
    private fun receiveLinkData() {
        intent?.let { intent ->
            intent.extras?.let { data ->
                kotlin.runCatching {
                    val section = intent.extras?.getString("section")
                    val link = data.toString()

                    val linkUri = Uri.parse(link)

                    if (link.contains("token=")) {
                        val token = linkUri.getQueryParameter("token")
                        App.preferences.authToken = token
                    }

                    if (section == "trauma") {
                        navigateToInjury = true
                    }
                }
            }
        }
    }

    @Subscribe
    fun onTestResponseEvent(e: TestResponseEvent) {
    }

    @Subscribe
    fun onOpenBodygraphEvent(e: OpenBodygraphEvent) {
        binding.navView.selectedItemId = R.id.navigation_bodygraph
    }

    @Subscribe
    fun onSelectNavItemEvent(e: SelectNavItemEvent) {
        binding.navView.menu.getItem(e.itemPosition).isChecked = true
    }

    @Subscribe
    fun onNoInetEvent(e: NoInetEvent) {
        snackbarInet.show()
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private val snackbarInet: Snackbar by lazy {
        val snackView = View.inflate(this, R.layout.view_snackbar, null)
        val snackbar = Snackbar.make(binding.snackbarContainer, "", Snackbar.LENGTH_LONG)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        val view = snackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params

        (snackbar.view as ViewGroup).removeAllViews()
        (snackbar.view as ViewGroup).addView(snackView)

        snackView.findViewById<ImageView>(R.id.icon).setImageResource(R.drawable.ic_snackbar_close)
        snackView.findViewById<TextView>(R.id.title).text =
            App.resourcesProvider.getStringLocale(R.string.snackbar_inet_title)
        snackView.findViewById<TextView>(R.id.desc).text =
            App.resourcesProvider.getStringLocale(R.string.snackbar_inet)
        snackbar.setBackgroundTint(Color.parseColor("#CE7559"))

        snackbar
    }

    fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
            } else {
                requestLocationPermission()
            }
        }
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    fun setupLocationListener() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationPermission()
            return
        }


        kotlin.runCatching {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = true
            criteria.powerRequirement = Criteria.POWER_MEDIUM
            val provider = locationManager.getBestProvider(criteria, true)

            provider?.let {
                val location = locationManager.getLastKnownLocation(provider)

                if (location != null && location.latitude != 0.0) {
                    binding.viewModel!!.reverseNominatim(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        type = 1
                    )
                }
            }

            val locationResult: MyLocation.LocationResult = object : MyLocation.LocationResult() {
                override fun gotLocation(location: Location?) {
                    if (location != null)
                        binding.viewModel!!.reverseNominatim(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                }
            }
            val myLocation = MyLocation()
            myLocation.getLocation(this, locationResult)

            requestSingleUpdate(this,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onNewLocationAvailable(location: GPSCoordinates?) {
                        if (location != null)
                            binding.viewModel!!.reverseNominatim(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                    }
                })

            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            val task = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener { location ->
                if (location != null)
                    binding.viewModel!!.reverseNominatim(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
            }
        }.onFailure {
        }
    }

    @Subscribe
    fun onSetupNotificationsEvent(e: SetupNotificationsEvent) {
        initNotificationReceiver()
    }

    @SuppressLint("ScheduleExactAlarm")
    @Subscribe
    fun onSetInjuryAlarmEvent(e: SetInjuryAlarmEvent) {
        viewModel.currentBodygraph.observe(this) {
            val notifyIntent = Intent(this, NotificationReceiver::class.java)
            notifyIntent.putExtra("userName", it.name)

            val pendingIntent = PendingIntent.getBroadcast(
                this, 4, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (e.remain * 1000),
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (e.remain * 1000),
                    pendingIntent
                )
            }
        }
    }

    private fun initNotificationReceiver() {
        viewModel.currentBodygraph.observe(this) {
            val notifyIntent = Intent(this, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                3,
                notifyIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
                else PendingIntent.FLAG_CANCEL_CURRENT
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * 60 * 24, (
                        1000 * 60 * 60 * 24).toLong(), pendingIntent
            )
            val cal = Calendar.getInstance()

            cal.add(Calendar.DAY_OF_WEEK, -(cal[Calendar.DAY_OF_WEEK]))
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.HOUR_OF_DAY, 14)
            cal.set(Calendar.SECOND, 0)

            if (!it.name.isNullOrEmpty()) {
                val notifyIntentForecasts = Intent(this, NotificationReceiver::class.java)
                notifyIntentForecasts.putExtra("isForecast", "true")
                notifyIntentForecasts.putExtra(
                    "userNameForecast",
                    it.name
                )
                notifyIntentForecasts.putExtra(
                    "forecastPosition", 0
//                    binding.viewModel!!.currentUser.pushForecastIdsList?.get(binding.viewModel!!.currentUser.pushForecastPosition % 10)
//                        ?: 0
                )

                val pendingIntentForecasts = PendingIntent.getBroadcast(
                    this, 948,
                    notifyIntentForecasts,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
                    else PendingIntent.FLAG_CANCEL_CURRENT
                )

                val mult = if (App.preferences.locale == "ru") 8 else 7

                val alarmManagerForecasts = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                alarmManagerForecasts.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis + mult * 86400000,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntentForecasts
                )
            }
        }
    }

    private fun initStartPage() {
        if (isStartPageInitialized) return

        if (App.preferences.authToken.isNullOrEmpty()) {
            router.replaceScreen(Screens.startScreen())
            return
        }

        if (!isFirstAnimationPlayed && App.preferences.currentUserId != -1L) {
            router.replaceScreen(Screens.loaderScreen())
        } else if (navigateToInjury) {
            router.replaceScreen(Screens.bodygraphSecondScreen(needUpdateNavMenu = true))
            navigateToInjury = false
        }

        else {
            isStartPageInitialized = true
            router.replaceScreen(
                when (App.preferences.lastLoginPageId) {
                    StartPage.SPLASH_01.pageId,
                    StartPage.SPLASH_02.pageId,
                    StartPage.SPLASH_03.pageId,
                    StartPage.SPLASH_04.pageId,
                    StartPage.SPLASH_05.pageId,
                    StartPage.RAVE.pageId,
                    StartPage.NAME.pageId,
                    StartPage.CALCULATE.pageId,
                    StartPage.DATE_BIRTH.pageId,
                    StartPage.TIME_BIRTH.pageId,
                    StartPage.PLACE_BIRTH.pageId,
                    StartPage.BODYGRAPH.pageId,
                    StartPage.SIGNUP.pageId
                    -> {
                        Screens.startScreen()
                    }

                    else -> {
                        Screens.bodygraphScreen()
//                        Screens.descriptionScreen()
                    }
                }
            )
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        if (::navigator.isInitialized)
            navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        YandexMetrica.pauseSession(this)

        (this.application as App).startActivityTransitionTimer()
        super.onPause()
    }

    private var isPriorityLocationSet = false
    override fun onViewModelReady(viewModel: BaseViewModel) {
        viewModel.loadFaqs()

        if (App.preferences.currentUserId != -1L && isNetworkConnected()) {
            viewModel.setupCurrentUser()

            onUpdateLoaderStateEvent(UpdateLoaderStateEvent(true))
        } else {
            isFirstAnimationPlayed = true
        }

        viewModel.reverseSuggestions.observe(this) {
            if (isPriorityLocationSet) {
                return@observe
            }

            if (!it.first.isNullOrEmpty()) {
                App.preferences.lastKnownLocationLat = it.first[0].lat.toString()
                App.preferences.lastKnownLocationLon = it.first[0].lon.toString()

                var locationStr = ""
                if (it.first[0].address.city.isNotEmpty()) {
                    locationStr += it.first[0].address.city
                    locationStr += ", "
                } else {
                    if (it.first[0].address.municipality.isNotEmpty()) {
                        locationStr += it.first[0].address.municipality
                        locationStr += ", "
                    }

                    locationStr += it.first[0].address.state
                    locationStr += ", "
                }

                locationStr += it.first[0].address.country

                if (App.preferences.lastKnownLocation.isNullOrEmpty()) {
                    App.preferences.lastKnownLocation = locationStr
                }
//                    "${it[0].address.city}, ${it[0].address.country}"

                EventBus.getDefault().post(LastKnownLocationUpdateEvent())

                if (it.second == 3)
                    isPriorityLocationSet = true
            }
        }
        receiveNotificationData()
    }

    @Subscribe
    fun onSetupLocationEvent(e: SetupLocationEvent) {
        setupLocationListener()
    }

    private fun receiveNotificationData() {
        intent?.let { intent ->
            intent.extras?.let {
                val section = intent.extras?.getString("section")
                val title = intent.extras?.getString("title")
                YandexMetrica.reportEvent("Push Clicked: $title")

                if (section == "trauma") {
                    YandexMetrica.reportEvent("OpenedAfterTraumaGenerated")
                }
            }
        }
    }

    @Subscribe
    fun onSetupNavMenuEvent(e: SetupNavMenuEvent) {
        setupNavMenu()
        updateNavMenuVisible(true)
    }

    @Subscribe
    fun onUpdateNavMenuVisibleStateEvent(e: UpdateNavMenuVisibleStateEvent) {
        updateNavMenuVisible(e.isVisible)
    }

    @Subscribe
    fun onUpdateBalloonBgStateEvent(e: UpdateBalloonBgStateEvent) {
        binding.balloonBg.isVisible = e.isVisible
    }

    private fun updateNavMenuVisible(isVisible: Boolean) {
        binding.navViewContainer.isVisible = isVisible
        binding.navViewBreakline.isVisible = isVisible
    }

    @Subscribe
    fun onUpdateLoaderStateEvent(e: UpdateLoaderStateEvent) {
        updateLoaderState(e.isVisible)
    }

    private var isFirstAnimationPlayed = false
    private var isLoaderEnded = false
    private var isStartPageInitialized = false

    private fun updateLoaderState(isVisible: Boolean) {
        runOnUiThread {
            if (isVisible) {
                if (!isFirstAnimationPlayed) {
                    return@runOnUiThread
                } else {
                    binding.progress.isVisible = isVisible
                    binding.progress.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            if (App.preferences.isDarkTheme) R.color.darkColor
                            else R.color.lightColor
                        )
                    )
                    binding.progressBar.setAnimation(
                        if (App.preferences.isDarkTheme) R.raw.loader_white
                        else R.raw.loader_black
                    )
                    binding.progressBar.playAnimation()
                }
            } else {
                binding.progress.isVisible = isVisible
                android.os.Handler().postDelayed({
                    if (!isFirstAnimationPlayed) {
                        isFirstAnimationPlayed = true
                    } else binding.progressBar.pauseAnimation()
                }, 1500)
            }
        }
    }

    @Subscribe
    fun onFinishFirstLoaderEvent(e: FinishFirstLoaderEvent) {
        isLoaderEnded = true
        if (isFirstAnimationPlayed) {
            initStartPage()
        } else if (!e.isFirst) {
            EventBus.getDefault().post(ContinueFirstLoaderEvent())
        }
    }

    override fun onResume() {
        super.onResume()
        YandexMetrica.resumeSession(this)

        val myApp: App = this.application as App
        if (myApp.wasInBackground) {

            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            //Do specific came-here-from-background code
        }
        myApp.stopActivityTransitionTimer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    || grantResults.contains(0)
                ) {
                    YandexMetrica.reportEvent("userAllowedGps")
                    Amplitude.getInstance().logEvent("userAllowedGeo")
                    setupLocationListener()
                }

//                } else {
//                    YandexMetrica.reportEvent("userDisabledGps")
//                    Amplitude.getInstance().logEvent("userDisabledGeo");
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
////                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
//
//                    // Check if we are in a state where the user has denied the permission and
//                    // selected Don't ask again
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                            this, Manifest.permission.ACCESS_FINE_LOCATION
//                        )
//                    ) {
//                        startActivity(
//                            Intent(
//                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                Uri.fromParts("package", this.packageName, null),
//                            ),
//                        )
//                    }
//                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun updateThemeAndLocale() {
        StatusBarUtil.setColor(
            this, resources.getColor(
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        if (App.preferences.isDarkTheme) {
            window.navigationBarColor = resources.getColor(R.color.darkColor)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.navigationBarColor = resources.getColor(R.color.lightColor)
        }

        if (!App.preferences.isDarkTheme) {
            showLightStatusBar()
            window.statusBarColor = Color.WHITE

//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            showDarkStatusBar()
        }


        binding.navViewContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.darkMenu
                else R.color.lightMenu
            )
        )

        binding.navViewBreakline.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.navView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.darkMenu
                else R.color.lightMenu
            )
        )

        binding.navView.itemTextColor = ContextCompat.getColorStateList(
            this,
            if (App.preferences.isDarkTheme) R.color.nav_button_state_selector
            else R.color.nav_button_state_selector_light
        )

        updateNavMenu()
    }

    private fun updateNavMenu() {
        if (binding.navView.menu.size() != 0) {
            binding.navView.menu.clear()
            binding.navView.inflateMenu(
                if (App.preferences.isDarkTheme) R.menu.bottom_nav_menu
                else R.menu.bottom_nav_menu_light
            )
        }
    }

    private fun setupNavMenu() {
        if (binding.navView.menu.size() != 0) {
            updateNavMenu()
            return
        }

        binding.navView.inflateMenu(
            if (App.preferences.isDarkTheme) R.menu.bottom_nav_menu
            else R.menu.bottom_nav_menu_light
        )

        binding.navViewContainer.isVisible = true
        binding.navViewBreakline.isVisible = true
        binding.navView.isVisible = true

        binding.navView.itemIconTintList = null
    }

    fun setupAdapty(referrer: String = "") {
        Adapty.identify(App.preferences.uniqueUserId!!, {
            Log.d("ekke", "keke")
        })
        if (
            Amplitude.getInstance().deviceId != null
            && Amplitude.getInstance().userId != null
        ) {
            if (referrer.isNullOrEmpty()) {
                val paramsAmplitude = AdaptyProfileParameters.Builder()
                    .withAmplitudeDeviceId(Amplitude.getInstance().deviceId)
                    .withAmplitudeUserId(Amplitude.getInstance().userId)
                    .withFacebookAnonymousId(AppEventsLogger.getAnonymousAppDeviceGUID(this))
                    .build()

//                val paramsAmplitude = ProfileParameterBuilder()
//                    .withAmplitudeDeviceId(Amplitude.getInstance().deviceId)
//                    .withAmplitudeUserId(Amplitude.getInstance().userId)
                Adapty.updateProfile(paramsAmplitude) { error -> }
            } else {
                val paramsAmplitude = AdaptyProfileParameters.Builder()
                    .withAmplitudeDeviceId(Amplitude.getInstance().deviceId)
                    .withAmplitudeUserId(Amplitude.getInstance().userId)
                    .withFacebookAnonymousId(AppEventsLogger.getAnonymousAppDeviceGUID(this))
                    .withCustomAttribute(
                        "referrer",
                        referrer.substringAfter("utm_source=").split("&")[0]
                    )
                    .build()
                Adapty.updateProfile(paramsAmplitude) { error -> }
            }
        }

        val conversionListener: AppsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                // It's important to include the network user ID
                Adapty.updateAttribution(
                    conversionData,
                    AdaptyAttributionSource.APPSFLYER,
                    AppsFlyerLib.getInstance().getAppsFlyerUID(this@MainActivity)
                ) {

                }

//                App.firebaseAnalytics.logEvent("purchase")
            }

            override fun onConversionDataFail(p0: String?) {}

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
            }

            override fun onAttributionFailure(p0: String?) {

            }
        }
        AppsFlyerLib.getInstance().registerConversionListener(this, conversionListener)

//        val params = ProfileParameterBuilder().withFacebookAnonymousId(AppEventsLogger.getAnonymousAppDeviceGUID(this))
//        Adapty.updateProfile(params) { error ->
//            if (error == null) {
//                // successful update
//            }
//        }

        Adapty.getPaywall("pw_test") { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    App.adaptySplitPwName = result.value.name
                    App.adaptyPaywallModel = result.value

                    Adapty.getPaywallProducts(result.value) { products ->
                        when (products) {
                            is AdaptyResult.Success -> {
                                App.adaptyProducts = products.value
                            }
                        }
                    }
                }
            }
        }
//        Adapty.getPaywalls { paywalls, products, error ->
////            if (error == null && !paywalls.isNullOrEmpty()) {
////                App.adaptyPaywallModel = paywalls[0]
////                App.adaptyProducts = products
////            }
//            if (error == null && !paywalls.isNullOrEmpty()) {
//                App.adaptySplitPwName = paywalls.first { it.developerId == "pw_test" }.name
//                App.adaptyPaywallModel = paywalls.first { it.developerId == "pw_test" }
//            }
//
//            if (error == null && !products.isNullOrEmpty()) {
//                App.adaptyProducts = products
//            }
//        }

//        Adapty.getProfile { result ->
//            when (result) {
//                is AdaptyResult.Success -> {
//                    val profile = result.value
//
//                    if (profile.accessLevels["premium"]?.isActive == true) {
//                        App.preferences.isPremiun = true
//
//                        val identify = Identify()
//                        identify.set("Purchased", "yes")
//                        Amplitude.getInstance().identify(identify)
//                    } else {
//                        App.preferences.isPremiun = false
//
//                        val identify = Identify()
//                        identify.set("Purchased", "no")
//                        Amplitude.getInstance().identify(identify)
//                    }
//                }
//
//                is AdaptyResult.Error -> {
//                    App.preferences.isPremiun = false
//
//                    val identify = Identify()
//                    identify.set("Purchased", "no")
//                    Amplitude.getInstance().identify(identify)
//                }
//            }
//        }

//        Adapty.setOnProfileUpdatedListener { profile ->
//            App.preferences.isPremiun = profile.accessLevels["premium"]?.isActive == true
//
//            val identify = Identify()
//            identify.set(
//                "Purchased",
//                if (profile.accessLevels["premium"]?.isActive == true) "yes"
//                else "no"
//            )
//            Amplitude.getInstance().identify(identify)
//        }
//        Adapty.setOnPurchaserInfoUpdatedListener(object : OnPurchaserInfoUpdatedListener {
//            override fun onPurchaserInfoReceived(purchaserInfo: PurchaserInfoModel) {
//                App.preferences.isPremiun = purchaserInfo.accessLevels["premium"]?.isActive == true
//
//                val identify = Identify()
//                identify.set("Purchased",
//                    if (purchaserInfo.accessLevels["premium"]?.isActive == true) "yes"
//                    else "no"
//                )
//                Amplitude.getInstance().identify(identify)
//            }
//        })

    }

    override fun onDestroy() {
        Amplitude.getInstance().logEvent("end_session")
        super.onDestroy()
    }

    @Subscribe
    fun onAdaptyLogShowEvent(e: AdaptyLogShowEvent) {
        if (App.adaptyPaywallModel != null)
            Adapty.logShowPaywall(App.adaptyPaywallModel!!)
    }

    @Subscribe
    fun onUpdateHardwareAccelerationStateEvent(e: UpdateHadrdwareAccelerationStateEvent) {
//        if (android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.P)
//            return

        if (e.isEnable) enableHardwareAcceleration()
//        else disableHardwareAcceleration()
    }

    private fun enableHardwareAcceleration() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
    }

    private fun disableHardwareAcceleration() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    override fun onBackPressed() {
        var lastFragment = supportFragmentManager.fragments.last()

        if (lastFragment.tag == "SupportLifecycleFragmentImpl") {
            lastFragment = supportFragmentManager.fragments.get(
                supportFragmentManager.fragments.size - 2
            )
        }
        if (lastFragment is FragmentBackPressCallback) {
            lastFragment.backPressed()
        } else {
            router.exit()
        }
//        super.onBackPressed()
    }

    inner class Handler

}