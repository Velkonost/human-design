package com.myhumandesignhd.ui.activity.main

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.adapty.utils.AdaptyLogLevel
import com.adapty.utils.AdaptyResult
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.jaeger.library.StatusBarUtil
import com.myhumandesignhd.App
import com.myhumandesignhd.App.Companion.LOCATION_REQUEST_CODE
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.ActivityMainBinding
import com.myhumandesignhd.event.AdaptyLogShowEvent
import com.myhumandesignhd.event.ContinueFirstLoaderEvent
import com.myhumandesignhd.event.FinishFirstLoaderEvent
import com.myhumandesignhd.event.LastKnownLocationUpdateEvent
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.OpenBodygraphEvent
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
import com.myhumandesignhd.ui.base.BaseActivity
import com.myhumandesignhd.ui.bodygraph.BodygraphFragment
import com.myhumandesignhd.ui.start.StartPage
import com.myhumandesignhd.util.MyLocation
import com.myhumandesignhd.util.SingleShotLocationProvider
import com.myhumandesignhd.util.SingleShotLocationProvider.GPSCoordinates
import com.myhumandesignhd.util.SingleShotLocationProvider.requestSingleUpdate
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Calendar


class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
) {

    lateinit var geocoder: Geocoder

    private lateinit var navigator: SupportAppNavigator

    private val navigationHolder by lazy {
        App.instance.navigatorHolder
    }

    private val router by lazy {
        App.instance.router
    }

    private var navController: NavController? = null
    private lateinit var referrerClient: InstallReferrerClient

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
            FirebaseAnalytics.getInstance(this).appInstanceId.addOnCompleteListener {
                runCatching {
                    if (!it.isSuccessful || it.result.isNullOrEmpty()) {
                        val uniqueId = Settings.Secure.getString(
                            this.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )//UUID.randomUUID().toString()
                        if (App.preferences.uniqueUserId == null) {
                            App.preferences.uniqueUserId = uniqueId
                        }

                        Amplitude.getInstance().userId = App.preferences.uniqueUserId

                        Adapty.activate(
                            applicationContext,
                            "public_live_fec6Kl1K.e7EdG5TbzwOPAO55qjDy",
                            customerUserId = App.preferences.uniqueUserId
                        )
                        Adapty.logLevel = AdaptyLogLevel.VERBOSE

                        referrerClient = InstallReferrerClient.newBuilder(this).build()
                        referrerClient.startConnection(object : InstallReferrerStateListener {

                            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                                when (responseCode) {
                                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                                        // Connection established.
                                        val response: ReferrerDetails =
                                            referrerClient.installReferrer
                                        val referrerUrl: String = response.installReferrer

                                        setupAdapty(referrerUrl)
                                    }

                                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                                        // API not available on the current Play Store app.
                                        setupAdapty()
                                    }

                                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                                        // Connection couldn't be established.
                                        setupAdapty()
                                    }
                                }
                            }

                            override fun onInstallReferrerServiceDisconnected() {
                                // Try to restart the connection on the next request to
                                // Google Play by calling the startConnection() method.
                            }
                        })


//                    setupAdapty()

                        return@addOnCompleteListener
                    }
                    if (it.isSuccessful) {
                        val appInstanceId = it.result.toString()
                        if (App.preferences.uniqueUserId == null) {
                            App.preferences.uniqueUserId = appInstanceId
                        }

                        Amplitude.getInstance().userId = App.preferences.uniqueUserId
                        Adapty.activate(
                            applicationContext,
                            "public_live_fec6Kl1K.e7EdG5TbzwOPAO55qjDy",
                            customerUserId = App.preferences.uniqueUserId
                        )
                        Adapty.logLevel = AdaptyLogLevel.VERBOSE

                        referrerClient = InstallReferrerClient.newBuilder(this).build()
                        referrerClient.startConnection(object : InstallReferrerStateListener {

                            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                                when (responseCode) {
                                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                                        kotlin.runCatching {
                                            // Connection established.
                                            val response: ReferrerDetails =
                                                referrerClient.installReferrer
                                            val referrerUrl: String = response.installReferrer

                                            setupAdapty(referrerUrl)

                                            if (
                                                referrerUrl.isNotEmpty()
                                                && appInstanceId.isNotEmpty()
                                                && referrerUrl.contains("utm_source=")
                                                && referrerUrl.substringAfter("utm_source=")
                                                    .split("&")[0] != "google-play"
                                            ) {
                                                binding.viewModel!!.setUserInfo(
                                                    gclid = referrerUrl.substringAfter("utm_source=")
                                                        .split("&")[0],
                                                    appInstanceId = appInstanceId
                                                )
                                            }
                                        }

                                    }

                                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                                        // API not available on the current Play Store app.
                                        setupAdapty()
                                    }

                                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                                        // Connection couldn't be established.
                                        setupAdapty()
                                    }
                                }
                            }

                            override fun onInstallReferrerServiceDisconnected() {
                                // Try to restart the connection on the next request to
                                // Google Play by calling the startConnection() method.
                            }
                        })

//                    setupAdapty()
                    }
                }
            }

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
    }

    @Subscribe
    fun onTestResponseEvent(e: TestResponseEvent) {
    }

    @Subscribe
    fun onOpenBodygraphEvent(e: OpenBodygraphEvent) {
        binding.navView.selectedItemId = R.id.navigation_bodygraph
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
//                AlertDialog.Builder(this)
//                    .setTitle("Location Permission Needed")
//                    .setMessage("This app needs the Location permission, please accept to use location functionality")
//                    .setPositiveButton(
//                        "OK"
//                    ) { _, _ ->
//                        requestLocationPermission()
//                    }
//                    .create()
//                    .show()
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

            var location = locationManager.getLastKnownLocation(provider!!)

            if (location != null && location.latitude != 0.0) {
                binding.viewModel!!.reverseNominatim(
                    location.latitude.toString(),
                    location.longitude.toString()
                )
            } else {

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

            task.addOnSuccessListener {
                if (location != null)
                    binding.viewModel!!.reverseNominatim(
                        location.latitude.toString(),
                        location.longitude.toString()
                    )
            }
        }
    }

    @Subscribe
    fun onSetupNotificationsEvent(e: SetupNotificationsEvent) {
        initNotificationReceiver()
    }

    private fun initNotificationReceiver() {
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + 60 * 60 * 24,
//                pendingIntent
//            )
//        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60 * 60 * 24,
                (1000 * 60 * 60 * 24).toLong(),
                pendingIntent
            )
//        }
        val cal = Calendar.getInstance()

        cal.add(Calendar.DAY_OF_WEEK, -(cal[Calendar.DAY_OF_WEEK]))
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.HOUR_OF_DAY, 14)
        cal.set(Calendar.SECOND, 0)

        if (binding.viewModel!!.currentUser.name.isNotEmpty()) {
            val notifyIntentForecasts = Intent(this, NotificationReceiver::class.java)
            notifyIntentForecasts.putExtra("isForecast", "true")
            notifyIntentForecasts.putExtra("userNameForecast", binding.viewModel!!.currentUser.name)
            notifyIntentForecasts.putExtra(
                "forecastPosition",
                binding.viewModel!!.currentUser.pushForecastIdsList?.get(binding.viewModel!!.currentUser.pushForecastPosition % 10)
                    ?: 0
            )

            binding.viewModel!!.updatePushForecastPosition()

            val pendingIntentForecasts = PendingIntent.getBroadcast(
                this, 948,
                notifyIntentForecasts,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_IMMUTABLE
                else PendingIntent.FLAG_CANCEL_CURRENT
//                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val mult =
                if (App.preferences.locale == "ru") 8
                else 7

            val alarmManagerForecasts = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManagerForecasts.setRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis + mult * 86400000,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntentForecasts
            )
        }
    }

    private fun initStartPage() {
        if (isStartPageInitialized) return

        if (!isFirstAnimationPlayed && App.preferences.currentUserId != -1L) {
            router.replaceScreen(Screens.loaderScreen())
        } else {
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
                    StartPage.DATE_BIRTH.pageId,
                    StartPage.TIME_BIRTH.pageId,
                    StartPage.PLACE_BIRTH.pageId,
                    StartPage.BODYGRAPH.pageId,
                    -> {
                        Screens.startScreen()
                    }

                    else -> {
//                        setupNavMenu()
                        Screens.bodygraphScreen()
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

    override fun onViewModelReady(viewModel: BaseViewModel) {
        viewModel.loadFaqs()

        if (App.preferences.currentUserId != -1L && isNetworkConnected()) {
            viewModel.setupCurrentUser()

            onUpdateLoaderStateEvent(UpdateLoaderStateEvent(true))
        } else {
            isFirstAnimationPlayed = true
        }

        viewModel.reverseSuggestions.observe(this) {
            if (!it.isNullOrEmpty()) {
                App.preferences.lastKnownLocationLat = it[0].lat.toString()
                App.preferences.lastKnownLocationLon = it[0].lon.toString()

                var locationStr = ""
                if (it[0].address.city.isNotEmpty()) {
                    locationStr += it[0].address.city
                    locationStr += ", "
                } else {
                    locationStr += it[0].address.municipality
                    locationStr += ", "
                    locationStr += it[0].address.state
                    locationStr += ", "
                }

                locationStr += it[0].address.country

                App.preferences.lastKnownLocation = locationStr
//                    "${it[0].address.city}, ${it[0].address.country}"
                EventBus.getDefault().post(LastKnownLocationUpdateEvent())
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
            Log.d("keke", "finish first event")
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

    private fun setupAdapty(referrer: String = "") {
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

        Adapty.getProfile { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    val profile = result.value

                    if (profile.accessLevels["premium"]?.isActive == true) {
                        App.preferences.isPremiun = true

                        val identify = Identify()
                        identify.set("Purchased", "yes")
                        Amplitude.getInstance().identify(identify)
                    } else {
                        App.preferences.isPremiun = false

                        val identify = Identify()
                        identify.set("Purchased", "no")
                        Amplitude.getInstance().identify(identify)
                    }
                }

                is AdaptyResult.Error -> {
                    App.preferences.isPremiun = false

                    val identify = Identify()
                    identify.set("Purchased", "no")
                    Amplitude.getInstance().identify(identify)
                }
            }
        }
//        Adapty.getPurchaserInfo { purchaserInfo, error ->
//            if (error == null) {
//                val identify = Identify()
//                identify.set("Purchased", "yes")
//                Amplitude.getInstance().identify(identify)
//
//                App.preferences.isPremiun = purchaserInfo?.accessLevels?.get("premium")?.isActive == true
//            } else {
//                val identify = Identify()
//                identify.set("Purchased", "no")
//                Amplitude.getInstance().identify(identify)
//
//                App.preferences.isPremiun = false
//            }
//        }

        Adapty.setOnProfileUpdatedListener { profile ->
            App.preferences.isPremiun = profile.accessLevels["premium"]?.isActive == true

            val identify = Identify()
            identify.set(
                "Purchased",
                if (profile.accessLevels["premium"]?.isActive == true) "yes"
                else "no"
            )
            Amplitude.getInstance().identify(identify)
        }
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

    inner class Handler

}