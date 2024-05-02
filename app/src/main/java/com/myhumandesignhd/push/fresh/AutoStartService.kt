package com.myhumandesignhd.push.fresh

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.util.Timer
import java.util.TimerTask


class AutoStartService : Service() {
    var counter = 0
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    init {
        Log.i(TAG, "AutoStartService: Here we go.....")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Service is destroyed :( ")
        val broadcastIntent = Intent(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    fun startTimer() {
        timer = Timer()
        //initialize the TimerTask's job
        initialiseTimerTask()
        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    fun initialiseTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i(TAG, "Timer is running " + counter++)
            }
        }
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    companion object {
        private const val TAG = "AutoService-KEKE"
    }
}


class RestartBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(
            "${RestartBroadcastReceiver::class.java.simpleName}-KEKE",
            "Service Stopped, but this is a never ending service."
        )
        scheduleJob(context)
    }

   companion object {
       private var jobScheduler: JobScheduler? = null
       private val restartBroadcastReceiver: RestartBroadcastReceiver? = null

       fun scheduleJob(context: Context) {
           if (jobScheduler == null) {
               jobScheduler = context
                   .getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
           }
           val componentName = ComponentName(
               context,
               JobService::class.java
           )
           val jobInfo = JobInfo.Builder(
               1,
               componentName
           ) // setOverrideDeadline runs it immediately - you must have at least one constraint
               // https://stackoverflow.com/questions/51064731/firing-jobservice-without-constraints
//               .setOverrideDeadline(0)
               .setMinimumLatency(3000)
               .setPersisted(true).build()
           jobScheduler?.schedule(jobInfo)
       }
   }

}


class JobService : JobService() {
    override fun onStartJob(_jobParameters: JobParameters): Boolean {
        val serviceAdmin = ServiceAdmin()
        serviceAdmin.launchService(this)
        instance = this
        jobParameters = _jobParameters
        return false
    }

    private fun registerRestarterReceiver() {
        // the context can be null if app just installed and this is called from restartsensorservice
        // https://stackoverflow.com/questions/24934260/intentreceiver-components-are-not-allowed-to-register-to-receive-intents-when
        // Final decision: in case it is called from installation of new version (i.e. from manifest, the application is
        // null. So we must use context.registerReceiver. Otherwise this will crash and we try with context.getApplicationContext
        if (restartBroadcastReceiver == null) restartBroadcastReceiver =
            RestartBroadcastReceiver() else try {
            unregisterReceiver(restartBroadcastReceiver)
        } catch (e: Exception) {
            // not registered
        }
        //give the time to run
        Handler().postDelayed(Runnable {
            val filter = IntentFilter()
            filter.addAction(packageName)
            try {
                registerReceiver(restartBroadcastReceiver, filter)
            } catch (e: Exception) {
                try {
                    applicationContext.registerReceiver(
                        restartBroadcastReceiver,
                        filter
                    )
                } catch (ex: Exception) {
                }
            }
        }, 1000)
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "Stopping job")
        val broadcastIntent = Intent(packageName)
        sendBroadcast(broadcastIntent)
        // give the time to run
        Handler().postDelayed(
            { unregisterReceiver(restartBroadcastReceiver) },
            1000
        )
        return false
    }

    companion object {
        private const val TAG = "JobService-KEKE"
        private var restartBroadcastReceiver: RestartBroadcastReceiver? = null
        private var instance: JobService? = null
        private var jobParameters: JobParameters? = null

        /**
         * called when the tracker is stopped for whatever reason
         * @param context
         */
        fun stopJob(context: Context?) {
            if (instance != null && jobParameters != null) {
                try {
                    instance?.unregisterReceiver(restartBroadcastReceiver)
                } catch (e: Exception) {
                    // not registered
                }
                Log.i(TAG, "Finishing job")
                instance?.jobFinished(jobParameters, true)
            }
        }
    }
}


class ServiceAdmin {
    private fun setServiceIntent(context: Context) {
        if (serviceIntent == null) {
            serviceIntent = Intent(context, AutoStartService::class.java)
        }
    }

    fun launchService(context: Context?) {
        if (context == null) {
            return
        }
        setServiceIntent(context)

        // depending on the version of Android we either launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        Log.d(TAG, "launchService:  Service is starting....")
    }

    companion object {
        private const val TAG = "ServiceAdmin-KEKE"
        private var serviceIntent: Intent? = null
    }
}