package com.myhumandesignhd.push

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.myhumandesignhd.App
import com.myhumandesignhd.R

class NotificationWorker(val context: Context, val params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(context)
        val resources = context.resources
        val tags = params.tags
        val title = when {
            tags.contains("first") -> resources.getString(R.string.first_push_title)
            tags.contains("second") -> resources.getString(R.string.second_push_title)
            tags.contains("third") -> resources.getString(R.string.third_push_title)
            else -> resources.getString(R.string.first_push_title)
        }

        val text = when {
            tags.contains("first") -> resources.getString(R.string.first_push_text)
            tags.contains("second") -> resources.getString(R.string.second_push_text)
            tags.contains("third") -> resources.getString(R.string.third_push_text)
            else -> resources.getString(R.string.first_push_text)
        }

        if (!App.preferences.isPremiun) {
            notificationHelper.createNotification(
                title = title,
                message = text,
                link = "open_offer",
                pageTitle = "pageTitle",
                filter = "filter",
                section = "open_offer"
            )
        }
        return Result.success()
    }
}