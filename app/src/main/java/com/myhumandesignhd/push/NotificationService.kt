package com.myhumandesignhd.push

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.myhumandesignhd.App
import com.myhumandesignhd.R


class NotificationService(name: String?) : IntentService(name) {

    companion object {
        private const val SERVICE_ID = 101
    }

    constructor() : this("servicerykdtr")

    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(
            applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentText("pez")
            .build()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            startForeground(SERVICE_ID, notification)
        } else {
            startForeground(SERVICE_ID, notification, FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onHandleIntent(intent: Intent?) {
        if (!App.preferences.isPushAvailable)
            return

        if (intent != null && !intent.getStringExtra("userName").isNullOrEmpty()) {
            val title = intent.getStringExtra("userName") + App.resourcesProvider.getStringLocale(R.string.injury_push_title)
            val desc =  App.resourcesProvider.getStringLocale(R.string.injury_push_desc)

            notificationHelper.createNotification(
                title = title,
                message = desc,
                link = "google.com",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "trauma"
            )
        } else if (intent != null && !intent.getStringExtra("isForecast").isNullOrEmpty()) {
            var title = resources.getStringArray(R.array.forecast_push_titles)[intent.getStringExtra("forecastPosition")?.toInt()?: 0]
            var desc = resources.getStringArray(R.array.forecast_push_desc)[intent.getStringExtra("forecastPosition")?.toInt()?: 0]

            title = title.replace("Имя", intent.getStringExtra("userNameForecast")!!)
            desc = desc.replace("Имя", intent.getStringExtra("userNameForecast")!!)


            notificationHelper.createNotification(
                title = title,
                message = desc,
                link = "google.com",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "section"
            )
        } else {

            if (App.preferences.pushNumber > 4)
                return

            val title = resources.getStringArray(R.array.push_titles)[App.preferences.pushNumber]
            val desc = resources.getStringArray(R.array.push_descs)[App.preferences.pushNumber]

            App.preferences.pushNumber++

            notificationHelper.createNotification(
                title = title,
                message = desc,
                link = "google.com",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "section"
            )
        }
    }
}