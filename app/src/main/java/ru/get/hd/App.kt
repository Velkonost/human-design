package ru.get.hd

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasAndroidInjector
import ru.get.hd.di.AppModule
import ru.get.hd.di.DaggerAppComponent
import ru.get.hd.repo.AppDatabase
import ru.get.hd.rest.di.RetrofitModule
import ru.get.hd.util.Preferences
import ru.get.hd.util.ResourcesProvider
import timber.log.Timber
import com.github.terrakok.cicerone.Cicerone
import java.util.*

class App : DaggerApplication() {

    private val cicerone = Cicerone.create()

    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    private var mActivityTransitionTimer: Timer? = null
    private var mActivityTransitionTimerTask: TimerTask? = null
    var wasInBackground = false
    private val MAX_ACTIVITY_TRANSITION_TIME_MS: Long = 60000

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

    }
}

