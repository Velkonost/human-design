package com.myhumandesignhd

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.adapty.models.AdaptyPaywall
import com.adapty.models.AdaptyPaywallProduct
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.github.terrakok.cicerone.Cicerone
import com.google.firebase.analytics.FirebaseAnalytics
import com.myhumandesignhd.di.AppModule
import com.myhumandesignhd.di.DaggerAppComponent
import com.myhumandesignhd.repo.AppDatabase
import com.myhumandesignhd.rest.di.RetrofitModule
import com.myhumandesignhd.util.Preferences
import com.myhumandesignhd.util.ResourcesProvider
import com.onesignal.OneSignal
import com.yandex.metrica.ReporterConfig
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import java.util.*

class App : DaggerApplication() {

    private val cicerone = Cicerone.create()

    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    private var mActivityTransitionTimer: Timer? = null
    private var mActivityTransitionTimerTask: TimerTask? = null
    var wasInBackground = false
    private val MAX_ACTIVITY_TRANSITION_TIME_MS: Long = 300000

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

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(BuildConfig.ONESIGNAL_APP_ID)

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
        lateinit var firebaseAnalytics: FirebaseAnalytics

        var isBodygraphWithAnimationShown = false
        var isBodygraphAnimationEnded = false

        @SuppressLint("StaticFieldLeak")
        lateinit var resourcesProvider: ResourcesProvider

        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
        const val LOCATION_REQUEST_CODE = 99

        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        const val DATE_FORMAT_SHORT = "yyyy-MM-dd"
        const val DATE_FORMAT_PERSONAL_INFO = "dd.MM.yyyy"
        const val DATE_FORMAT_PERSONAL_INFO_US = "MM.dd.yyyy"

        const val TARGET_SDK = android.os.Build.VERSION_CODES.M


        val PROMOCODES = arrayListOf<String>(
            "1hxn3eus", "LeRfUjHN", "zOMFmWTK", "FFjOcW8i", "TvH36Qmg",
            "ARxfuK55", "3wEet9gC", "SCiMJODv", "cdtlbCAX", "8x3eMkHD",
            "LieG4c97", "Im38VXbd", "RNNdh3hJ", "tE3q71zG", "idORkAnz",
            "EySZgQEv", "aE7gAoOT", "THibAlmL", "tKblRicY", "oB6cgELb",
            "r1uY8O6B", "poiSITaA", "8RULa6iZ", "dzvcWydz", "NkdNcppv",
            "kiWZTPFl", "2rN3Obel", "4snr17Yg", "SkIJFwPu", "DrSRBF59",
            "zHzfwMvC", "YTLPN8QB", "m16nOjxF", "xWSk4d2W", "qB2CkELy",
            "7ykKoS8g", "Tc6cvOfO", "d15aaQrA", "OvzncMzz", "EXop4FCY",
            "hs4REHTL", "B9rrYsKW", "qI5VHhsU", "UTqvDbvV", "VqYF6M8R",
            "SqHmNwB5", "gpQld7U6", "GD24Wgnt", "qDLxV8hv", "xQ4x3tQ1",
            "HDFREETEST"
        )
        const val PROMOCODE = "HDFREETEST"

        var adaptySplitPwName: String? = null
        var adaptyPaywallModel: AdaptyPaywall? = null
        var adaptyProducts: List<AdaptyPaywallProduct>? = null
    }
}

