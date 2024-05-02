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

            if (!App.preferences.isPremiun) {
                val type = intent?.getStringExtra("type")
                intent1.putExtra("type", type)
                handleIntent(intent1, context)
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

    private fun handleIntent(intent: Intent?, context: Context) {
        if (!App.preferences.isPushAvailable)
            return

        val notificationHelper = NotificationHelper(context)
        val resources = context.resources

        val type = intent?.getStringExtra("type")
        val title = when (type) {
            "first" -> resources.getString(R.string.first_push_title)
            "second" -> resources.getString(R.string.second_push_title)
            "third" -> resources.getString(R.string.third_push_title)
            else -> null
        }
        val text = when (type) {
            "first" -> resources.getString(R.string.first_push_text)
            "second" -> resources.getString(R.string.second_push_text)
            "third" -> resources.getString(R.string.third_push_text)
            else -> null
        }

        if (title != null) {
            notificationHelper.createNotification(
                title = title,
                message = text,
                link = "google.com",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "trauma"
            )
        }
    }
}


class SecondNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        kotlin.runCatching {
            val intent1 = Intent(context, NotificationService::class.java)

            if (!App.preferences.isPremiun) {
                val type = intent?.getStringExtra("type")
                intent1.putExtra("type", type)
                handleIntent(intent1, context)
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

    private fun handleIntent(intent: Intent?, context: Context) {
        if (!App.preferences.isPushAvailable)
            return

        val notificationHelper = NotificationHelper(context)
        val resources = context.resources

        val type = intent?.getStringExtra("type")
        val title = when (type) {
            "first" -> resources.getString(R.string.first_push_title)
            "second" -> resources.getString(R.string.second_push_title)
            "third" -> resources.getString(R.string.third_push_title)
            else -> null
        }
        val text = when (type) {
            "first" -> resources.getString(R.string.first_push_text)
            "second" -> resources.getString(R.string.second_push_text)
            "third" -> resources.getString(R.string.third_push_text)
            else -> null
        }

        if (title != null) {
            notificationHelper.createNotification(
                title = title,
                message = text,
                link = "google.com",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "trauma"
            )
        }
    }
}


class ThirdNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        kotlin.runCatching {
            val intent1 = Intent(context, NotificationService::class.java)

            if (!App.preferences.isPremiun) {
                val type = intent?.getStringExtra("type")
                intent1.putExtra("type", type)
                handleIntent(intent1, context)
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

    private fun handleIntent(intent: Intent?, context: Context) {
        if (!App.preferences.isPushAvailable)
            return

        val notificationHelper = NotificationHelper(context)
        val resources = context.resources

        val type = intent?.getStringExtra("type")
        val title = when (type) {
            "first" -> resources.getString(R.string.first_push_title)
            "second" -> resources.getString(R.string.second_push_title)
            "third" -> resources.getString(R.string.third_push_title)
            else -> null
        }
        val text = when (type) {
            "first" -> resources.getString(R.string.first_push_text)
            "second" -> resources.getString(R.string.second_push_text)
            "third" -> resources.getString(R.string.third_push_text)
            else -> null
        }

        if (title != null) {
            notificationHelper.createNotification(
                title = title,
                message = text,
                link = "google.com",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "trauma"
            )
        }
    }
}
