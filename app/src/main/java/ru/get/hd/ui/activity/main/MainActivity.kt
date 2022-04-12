package ru.get.hd.ui.activity.main

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pl.droidsonroids.gif.GifDrawable
import ru.get.hd.App
import ru.get.hd.App.Companion.LOCATION_REQUEST_CODE
import ru.get.hd.R
import ru.get.hd.databinding.ActivityMainBinding
import ru.get.hd.event.PermissionGrantedEvent
import ru.get.hd.event.SetupNavMenuEvent
import ru.get.hd.event.ToBodygraphClickEvent
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.navigation.SupportAppNavigator
import ru.get.hd.push.NotificationReceiver
import ru.get.hd.ui.base.BaseActivity
import ru.get.hd.ui.splash.SplashPage
import ru.get.hd.ui.start.StartPage
import ru.get.hd.vm.*
import java.util.*

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
) {

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


        if (savedInstanceState == null) {
            initStartPage()
        }

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
    }

    private fun initNotificationReceiver() {
        val notifyIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            2,
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
                SplashPage.SPLASH_05.pageId -> {
                    Screens.splashScreen()

                }
                StartPage.RAVE.pageId,
                StartPage.NAME.pageId,
                StartPage.DATE_BIRTH.pageId,
                StartPage.TIME_BIRTH.pageId,
                StartPage.PLACE_BIRTH.pageId,
                StartPage.BODYGRAPH.pageId, -> {
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
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigationHolder.removeNavigator()
        super.onPause()
    }

    override fun onViewModelReady(viewModel: BaseViewModel) {
        viewModel.loadFaqs()

        if (App.preferences.currentUserId != -1L)
            viewModel.setupCurrentUser()
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

    private fun updateNavMenuVisible(isVisible: Boolean) {
        binding.navViewContainer.isVisible = isVisible
    }

    @Subscribe
    fun onUpdateLoaderStateEvent(e: UpdateLoaderStateEvent) {
        updateLoaderState(e.isVisible)
    }

    private fun updateLoaderState(isVisible: Boolean) {
        runOnUiThread {
            binding.progress.isVisible = isVisible

            if (isVisible) (binding.progressBar.drawable as GifDrawable).start()
            else (binding.progressBar.drawable as GifDrawable).stop()
        }

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
                        EventBus.getDefault().post(PermissionGrantedEvent(LOCATION_REQUEST_CODE))
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

        binding.navView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.darkMenu
                else R.color.lightMenu
            )
        )
    }

    private fun setupNavMenu() {
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(R.menu.bottom_nav_menu)

        binding.navViewContainer.isVisible = true
        binding.navView.isVisible = true

        binding.navView.itemIconTintList = null
    }

    inner class Handler

}