package com.myhumandesignhd

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.adapty.Adapty
import com.adapty.models.PaywallModel
import com.adapty.models.ProductModel
import com.adapty.utils.AdaptyLogLevel
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import com.myhumandesignhd.di.AppModule
import com.myhumandesignhd.repo.AppDatabase
import com.myhumandesignhd.rest.di.RetrofitModule
import com.myhumandesignhd.util.Preferences
import com.myhumandesignhd.util.ResourcesProvider
import timber.log.Timber
import com.github.terrakok.cicerone.Cicerone
import com.myhumandesignhd.di.DaggerAppComponent
import com.yandex.metrica.ReporterConfig
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import java.util.*
import java.util.logging.Level.ALL

class App : DaggerApplication() {

    private val cicerone = Cicerone.create()

    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    private var mActivityTransitionTimer: Timer? = null
    private var mActivityTransitionTimerTask: TimerTask? = null
    var wasInBackground = false
    private val MAX_ACTIVITY_TRANSITION_TIME_MS: Long = 1200000

    private val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .retrofitModule(RetrofitModule(this))
        .build()

    override fun onCreate() {
        super.onCreate()

        instance = this
        preferences = Preferences(this)
        resourcesProvider = ResourcesProvider(this)
        database = AppDatabase(this)

        Adapty.activate(applicationContext, "public_live_fec6Kl1K.e7EdG5TbzwOPAO55qjDy")
        Adapty.setLogLevel(AdaptyLogLevel.VERBOSE)

        setupAppsFlyer()
        activateAppMetrica()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createNotificationChannel()
    }

    fun getAppComponent() = appComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    getString(R.string.default_notification_channel_id),
                    importance
                )
            notificationManager.createNotificationChannel(notificationChannel)
            Timber.d("isNotificationChannelCreated")
        }
    }

    fun startActivityTransitionTimer() {
        mActivityTransitionTimer = Timer()
        mActivityTransitionTimerTask = object : TimerTask() {
            override fun run() {
                this@App.wasInBackground = true
            }
        }
        mActivityTransitionTimer!!.schedule(
            mActivityTransitionTimerTask,
            MAX_ACTIVITY_TRANSITION_TIME_MS
        )
    }

    fun stopActivityTransitionTimer() {
        mActivityTransitionTimerTask?.cancel()
        mActivityTransitionTimer?.cancel()
        wasInBackground = false
    }

    private fun setupAppsFlyer() {
        AppsFlyerLib.getInstance().init(BuildConfig.APPSFLYER_KEY, null, this)
        AppsFlyerLib.getInstance().start(this, BuildConfig.APPSFLYER_KEY, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Timber.d("Launch sent successfully")

            }

            @SuppressLint("LogNotTimber")
            override fun onError(errorCode: Int, errorDesc: String) {
                Log.d(
                    "appsflyer", "Launch failed to be sent:\n" +
                            "Error code: " + errorCode + "\n"
                            + "Error description: " + errorDesc
                )
            }
        })
        AppsFlyerLib.getInstance().setDebugLog(true)
    }

    private fun activateAppMetrica() {
        val appMetricaConfig: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder(BuildConfig.APPMETRICA_API_KEY)
                .withLocationTracking(true)
                .withCrashReporting(true)
                .withLogs()
                .withStatisticsSending(true)
                .withAppOpenTrackingEnabled(true)
                .build()
        YandexMetrica.activate(applicationContext, appMetricaConfig)
        YandexMetrica.enableActivityAutoTracking(this)

        val reporterConfig = ReporterConfig.newConfigBuilder(BuildConfig.APPMETRICA_API_KEY)
            .withLogs()
            .withStatisticsSending(true)
            .build()

        YandexMetrica.activateReporter(applicationContext, reporterConfig)

        if (preferences.isFirstLaunch) {
            YandexMetrica.reportEvent("Install")
        }
    }

    companion object {
        lateinit var instance: App
        lateinit var preferences: Preferences
        lateinit var database: AppDatabase

        var isBodygraphWithAnimationShown = false
        var isBodygraphAnimationEnded = false

        @SuppressLint("StaticFieldLeak")
        lateinit var resourcesProvider: ResourcesProvider

        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
        const val LOCATION_REQUEST_CODE = 99

        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val DATE_FORMAT_PERSONAL_INFO = "dd.MM.yyyy"

        const val TARGET_SDK = android.os.Build.VERSION_CODES.M

        const val PROMOCODE = "HDFREETEST"

        var adaptyPaywallModel: PaywallModel? = null
        var adaptyProducts: List<ProductModel>? = null

    }
}

