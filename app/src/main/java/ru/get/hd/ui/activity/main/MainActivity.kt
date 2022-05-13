package ru.get.hd.ui.activity.main

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pl.droidsonroids.gif.GifDrawable
import ru.get.hd.App
import ru.get.hd.App.Companion.LOCATION_REQUEST_CODE
import ru.get.hd.R
import ru.get.hd.databinding.ActivityMainBinding
import ru.get.hd.event.LastKnownLocationUpdateEvent
import ru.get.hd.event.PermissionGrantedEvent
import ru.get.hd.event.SetupNavMenuEvent
import ru.get.hd.event.ToBodygraphClickEvent
import ru.get.hd.event.UpdateBalloonBgStateEvent
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.navigation.SupportAppNavigator
import ru.get.hd.push.NotificationReceiver
import ru.get.hd.ui.adduser.awaitCurrentLocation
import ru.get.hd.ui.base.BaseActivity
import ru.get.hd.ui.splash.SplashPage
import ru.get.hd.ui.start.StartPage
import ru.get.hd.vm.*
import java.util.*
import android.net.ConnectivityManager
import android.widget.ImageView
import ru.get.hd.event.NoInetEvent
import android.location.Criteria





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

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

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
            initStartPage()
        }

//        (binding.progressBar.drawable as GifDrawable).setSpeed(3f)

        navigator = SupportAppNavigator(this, R.id.subContainer)
        navigationHolder.setNavigator(navigator)

        binding.navView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_bodygraph -> {
                    router.navigateTo(Screens.bodygraphScreen())
                    true
                }
                R.id.navigation_description -> {
                    router.navigateTo(Screens.bodygraphSecondScreen())
                    true
                }
                R.id.navigation_transit -> {
                    router.navigateTo(Screens.transitScreen())
                    true
                }
                R.id.navigation_compatibility -> {
                    router.navigateTo(Screens.compatibilityScreen())
                    true
                }
                R.id.navigation_affirmation -> {
                    router.navigateTo(Screens.affirmationScreen())
                    true
                }
                else -> {
                    router.navigateTo(Screens.settingsScreen())
                    true
                }
            }

        }

        initNotificationReceiver()

//        geocoder = Geocoder(this, Locale.getDefault())

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
        snackView.findViewById<TextView>(R.id.title).text = App.resourcesProvider.getStringLocale(R.string.snackbar_inet_title)
        snackView.findViewById<TextView>(R.id.desc).text = App.resourcesProvider.getStringLocale(R.string.snackbar_inet)
        snackbar.setBackgroundTint(Color.parseColor("#CE7559"))

        snackbar
    }

    fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        requestLocationPermission()
                    }
                    .create()
                    .show()
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
            App.LOCATION_REQUEST_CODE
        )
    }

    fun setupLocationListener() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationPermission()
            return
        }


        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_MEDIUM
        val provider = locationManager.getBestProvider(criteria, true)

        var location = locationManager.getLastKnownLocation(provider!!)

        while (location == null) {
            location = locationManager.getLastKnownLocation(provider!!)
        }
        binding.viewModel!!.reverseNominatim(location!!.latitude.toString(), location.longitude.toString())

    }

    private fun initNotificationReceiver() {
        val notifyIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            3,
            notifyIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * 60 * 24 , (
                    1000 * 60 * 60 * 24).toLong(), pendingIntent
        )
    }

    private fun initStartPage() {
        router.replaceScreen(
            when(App.preferences.lastLoginPageId) {
                SplashPage.SPLASH_01.pageId,
                SplashPage.SPLASH_02.pageId,
                SplashPage.SPLASH_03.pageId,
                SplashPage.SPLASH_04.pageId,
                SplashPage.SPLASH_05.pageId,
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
                    setupNavMenu()
                    Screens.bodygraphScreen()
                }
            }
        )
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        if (::navigator.isInitialized)
            navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        (this.application as App).startActivityTransitionTimer()
        super.onPause()
    }

    override fun onViewModelReady(viewModel: BaseViewModel) {
        setupLocationListener()

        viewModel.loadFaqs()

        if (App.preferences.currentUserId != -1L && isNetworkConnected()) {
            viewModel.setupCurrentUser()
            onUpdateLoaderStateEvent(UpdateLoaderStateEvent(true))
        }

        viewModel.reverseSuggestions.observe(this) {
            if (!it.isNullOrEmpty()) {
                App.preferences.lastKnownLocationLat = it[0].lat.toString()
                App.preferences.lastKnownLocationLon = it[0].lon.toString()
                App.preferences.lastKnownLocation = "${it[0].address.city}, ${it[0].address.country}"
                EventBus.getDefault().post(LastKnownLocationUpdateEvent())
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
    private fun updateLoaderState(isVisible: Boolean) {
        runOnUiThread {
            binding.progress.isVisible = isVisible
//            (binding.progressBar.drawable as GifDrawable).setSpeed(3f)


            if (isVisible) {
                if (!isFirstAnimationPlayed) {
                    binding.progressBar.setAnimation(
                        if (App.preferences.isDarkTheme) R.raw.logo_transition_black
                        else R.raw.logo_transition_white
                    )
                } else {
                    binding.progressBar.setAnimation(
                        if (App.preferences.isDarkTheme) R.raw.loader_black
                        else R.raw.loader_white
                    )
                }
                binding.progressBar.playAnimation()
            }

//                    (binding.progressBar.drawable as GifDrawable).start()
            else {
                    android.os.Handler().postDelayed({
                        binding.progressBar.pauseAnimation()
                        isFirstAnimationPlayed = true
//                        (binding.progressBar.drawable as GifDrawable).stop()
                    }, 1500)
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
//                        EventBus.getDefault().post(PermissionGrantedEvent(LOCATION_REQUEST_CODE))
                        setupLocationListener()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
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

        window.navigationBarColor = resources.getColor(
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        )

        binding.navViewContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.darkMenu
                else R.color.lightMenu
            )
        )

        binding.navViewBreakline.setBackgroundColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

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
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(
            if (App.preferences.isDarkTheme) R.menu.bottom_nav_menu
            else R.menu.bottom_nav_menu_light
        )

        binding.navViewContainer.isVisible = true
        binding.navViewBreakline.isVisible = true
        binding.navView.isVisible = true

        binding.navView.itemIconTintList = null
    }

    inner class Handler

}