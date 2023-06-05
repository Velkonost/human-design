package com.myhumandesignhd.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleAccessTokenResponse(
    @field:JsonProperty("expires_in") val expiresIn: Int = 0,
    @field:JsonProperty("token_type") val tokenType: String = "",
    @field:JsonProperty("refresh_token") val refreshToken: String = "",
    @field:JsonProperty("id_token") val idToken: String = "",
    @field:JsonProperty("access_token") val accessToken: String = ""
)