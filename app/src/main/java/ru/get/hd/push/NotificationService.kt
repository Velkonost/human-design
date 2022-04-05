package ru.get.hd.push

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.get.hd.App
import ru.get.hd.R


class NotificationService(name: String?) : IntentService(name) {

    constructor() : this("servicerykdtr")

    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(
            applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()

        startForeground()
    }

    private fun startForeground() {
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
        startForeground(101, notification)
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

        if (App.preferences.pushNumber > 4)
            return

        val title = resources.getStringArray(R.array.push_titles)[App.preferences.pushNumber]
        val desc = resources.getStringArray(R.array.push_descs)[App.preferences.pushNumber]

        App.preferences.pushNumber ++

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