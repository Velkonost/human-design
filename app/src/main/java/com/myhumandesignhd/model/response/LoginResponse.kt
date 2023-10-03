package com.myhumandesignhd.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResponse (
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("data") val data: LoginResponseData? = LoginResponseData(),
    @field:JsonProperty("message") val message: String = "",
)

data class LoginResponseData(
    @field:JsonProperty("code") val code: Int = 0,
    @field:JsonProperty("userId") val userId: String? = "",
    @field:JsonProperty("token") val token: String? = "",
)