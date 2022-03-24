package ru.get.hd.ui.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
import ru.get.hd.event.UpdateToBodygraphCardStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.navigation.SupportAppNavigator
import ru.get.hd.ui.base.BaseActivity
import ru.get.hd.ui.splash.SplashPage
import ru.get.hd.ui.start.StartPage
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.alpha0
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.translationYalpha0
import ru.get.hd.util.ext.translationYalpha1
import ru.get.hd.vm.*
import java.util.*
import kotlin.reflect.KParameter

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

//        navController = findNavController(R.id.nav_host_fragment)
//
//        val navHostFragment = nav_host_fragment as NavHostFragment
//        val graphInflater = navHostFragment.navController.navInflater
//        val navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
//        navController = navHostFragment.navController


//        navGraph.startDestination =
//            when(App.preferences.lastLoginPageId) {
//                SplashPage.SPLASH_01.pageId,
//                SplashPage.SPLASH_02.pageId,
//                SplashPage.SPLASH_03.pageId,
//                SplashPage.SPLASH_04.pageId,
//                SplashPage.SPLASH_05.pageId -> {
//                    R.id.navigation_splash
//                }
//                StartPage.RAVE.pageId,
//                StartPage.NAME.pageId,
//                StartPage.DATE_BIRTH.pageId,
//                StartPage.TIME_BIRTH.pageId,
//                StartPage.PLACE_BIRTH.pageId,
//                StartPage.BODYGRAPH.pageId, -> {
//                    R.id.navigation_start
//                }
//                else -> {
//                    setupNavMenu()
//                    R.id.navigation_bodygraph
//                }
//            }
//
//        navController!!.graph = navGraph
//        binding.navView.setupWithNavController(navController!!)

//        binding.navView.setOnNavigationItemReselectedListener {
//            if (binding.navView.selectedItemId == it.itemId) {
////                val navGraph =
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//                    .popBackStack(it.itemId, false)
//
//                return@setOnNavigationItemReselectedListener
//            }
//        }

        navigator = SupportAppNavigator(this, R.id.subContainer)
        navigationHolder.setNavigator(navigator)

        binding.navView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_bodygraph -> {
                    router.navigateTo(Screens.bodygraphScreen())
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
//        updateToBodygraphCard(isVisible)
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

        binding.toBodygraphTitle.text = App.resourcesProvider.getStringLocale(R.string.to_bodygraph)
        binding.toBodygraphTitle.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.icToBodygraphArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.toBodygraphCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))
    }

    private fun setupNavMenu() {
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(R.menu.bottom_nav_menu)

        binding.navViewContainer.isVisible = true
        binding.navView.isVisible = true

        binding.navView.itemIconTintList = null
    }

    private fun updateToBodygraphCard(isVisible: Boolean) {
        if (isVisible) {
            binding.toBodygraphCard.translationYalpha1(this.convertDpToPx(0f), 500)
            binding.toBodygraphContainer.translationYalpha1(this.convertDpToPx(0f), 500)
        } else {
            binding.toBodygraphCard.translationYalpha0(this.convertDpToPx(20f), 500)
            binding.toBodygraphContainer.translationYalpha0(this.convertDpToPx(20f), 500)
        }
    }

    @Subscribe
    fun onUpdateToBodygraphCardStateEvent(e: UpdateToBodygraphCardStateEvent) {
        updateToBodygraphCard(e.isVisible)
    }

    inner class Handler {

        fun onToBodygraphClicked(v: View) {
            updateToBodygraphCard(false)
            EventBus.getDefault().post(ToBodygraphClickEvent())
        }
    }

}