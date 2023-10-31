package com.myhumandesignhd.ui.activity.main.ext

import com.adapty.Adapty
import com.adapty.utils.AdaptyLogLevel
import com.amplitude.api.Amplitude
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.firebase.analytics.FirebaseAnalytics
import com.myhumandesignhd.App
import com.myhumandesignhd.ui.activity.main.MainActivity
import java.util.UUID

fun MainActivity.activateAdapty() {
//    if (App.preferences.uniqueUserId.isNullOrEmpty()) {
//        return
//    }

    FirebaseAnalytics.getInstance(this).appInstanceId.addOnCompleteListener {
        if (!it.isSuccessful || it.result.isNullOrEmpty()) {
            val uniqueId = UUID.randomUUID().toString()
            if (App.preferences.uniqueUserId == null) {
                App.preferences.uniqueUserId = uniqueId
            }

            Amplitude.getInstance().userId = App.preferences.uniqueUserId

            Adapty.activate(
                applicationContext, "public_live_fec6Kl1K.e7EdG5TbzwOPAO55qjDy",
                customerUserId = App.preferences.uniqueUserId
            )
            Adapty.logLevel = AdaptyLogLevel.VERBOSE

            referrerClient = InstallReferrerClient.newBuilder(this).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            // Connection established.
                            kotlin.runCatching {

                                val response: ReferrerDetails = referrerClient.installReferrer
                                val referrerUrl: String = response.installReferrer

                                setupAdapty(referrerUrl)
                            }
                        }

                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            setupAdapty()
                        }

                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            setupAdapty()
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {}
            })

            return@addOnCompleteListener
        }

        if (it.isSuccessful) {
            val appInstanceId = it.result.toString()
            if (App.preferences.uniqueUserId == null) {
                App.preferences.uniqueUserId = appInstanceId
            }

            Amplitude.getInstance().userId = App.preferences.uniqueUserId
            Adapty.activate(
                applicationContext, "public_live_fec6Kl1K.e7EdG5TbzwOPAO55qjDy",
                customerUserId = App.preferences.uniqueUserId
            )
            Adapty.logLevel = AdaptyLogLevel.VERBOSE

            referrerClient = InstallReferrerClient.newBuilder(this).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    if (!referrerClient.isReady) return

                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            // Connection established.
                            val response: ReferrerDetails = referrerClient.installReferrer
                            val referrerUrl: String = response.installReferrer

                            setupAdapty(referrerUrl)

                            if (
                                referrerUrl.isNotEmpty()
                                && appInstanceId.isNotEmpty()
                                && referrerUrl.contains("utm_source=")
                                && referrerUrl.substringAfter("utm_source=")
                                    .split("&")[0] != "google-play"
                            ) {
                                binding.viewModel!!.setUserInfo(
                                    gclid = referrerUrl.substringAfter("utm_source=")
                                        .split("&")[0],
                                    appInstanceId = appInstanceId
                                )
                            }
                        }

                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            setupAdapty()
                        }

                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            setupAdapty()
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {}
            })
        }
    }
}