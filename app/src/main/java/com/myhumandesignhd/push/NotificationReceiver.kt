package com.myhumandesignhd.push

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent1)
            } else {
                context.startService(intent1)
            }


            context.bindService(intent1, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    //retrieve an instance of the service here from the IBinder returned
                    //from the onBind method to communicate with
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            }, Context.BIND_AUTO_CREATE)
        }.onFailure {}
    }
}