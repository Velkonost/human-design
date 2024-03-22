package com.myhumandesignhd.push

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.myhumandesignhd.App
import com.myhumandesignhd.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        kotlin.runCatching {
            val intent1 = Intent(context, NotificationService::class.java)

            if (intent != null && !intent.getStringExtra("userName").isNullOrEmpty())
                intent1.putExtra("userName", intent.getStringExtra("userName"))

            if (
                intent != null && !intent.getStringExtra("isForecast").isNullOrEmpty()
                && intent.getStringExtra("isForecast") == "true"
            ) {
                intent1.putExtra("isForecast", "true")
                intent1.putExtra("userNameForecast", intent.getStringExtra("userNameForecast"))
                intent1.putExtra("forecastPosition", intent.getStringExtra("forecastPosition"))
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intent1)
//            } else {
//                context.startService(intent1)
//            }
            handleIntent(intent1, context)


            context.bindService(intent1, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    //retrieve an instance of the service here from the IBinder returned
                    //from the onBind method to communicate with
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }.onFailure {}
    }

    private fun handleIntent(intent: Intent?, context: Context) {
        if (!App.preferences.isPushAvailable)
            return

        val notificationHelper = NotificationHelper(context)
        val resources = context.resources

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