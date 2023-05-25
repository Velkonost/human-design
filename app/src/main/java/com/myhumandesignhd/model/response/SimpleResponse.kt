package com.myhumandesignhd.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SimpleResponse(
    @field:JsonProperty("code") val code: Int = 0,
    @field:JsonProperty("message") val message: String = "",
)