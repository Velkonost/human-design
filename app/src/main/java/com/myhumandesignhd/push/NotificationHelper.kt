package com.myhumandesignhd.push

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.myhumandesignhd.R
import com.myhumandesignhd.ui.activity.main.MainActivity
import timber.log.Timber

class NotificationHelper(private val mContext: Context) {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    /**
     * Create and push the notification
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(
        title: String?,
        message: String?,
        link: String?,
        pageTitle: String?,
        filter: String?,
        section: String?
    ) {

        /**Creates an explicit intent for an Activity in your app */
        val resultIntent = Intent(mContext, MainActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        resultIntent.putExtra("link", link)
        resultIntent.putExtra("title", title)
        resultIntent.putExtra("filter", filter)
        resultIntent.putExtra("section", section)

        Timber.d("title: $title")
        Timber.d("message: $message")
        Timber.d("link: $link")
        Timber.d("pageTitle: $pageTitle")
        Timber.d("filter: $filter")

        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            2 /* Request code */, resultIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder = NotificationCompat.Builder(mContext)
            .setSmallIcon(R.drawable.ic_affirmation_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)

        mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder?.setChannelId(mContext.getString(R.string.default_notification_channel_id))
        }
//        if (App.preferences.userId != 0)
        mNotificationManager?.notify(2 /* Request Code */, mBuilder?.build())
    }
}