package com.myhumandesignhd.model.request

import com.myhumandesignhd.BuildConfig

data class GoogleAccessTokenBody(
    val grant_type: String = "authorization_code",
    val client_id: String = BuildConfig.GOOGLE_CLIENT_ID,
    val client_secret: String = BuildConfig.GOOGLE_CLIENT_SECRET,
    val redirect_uri: String = "",
    val code: String
)