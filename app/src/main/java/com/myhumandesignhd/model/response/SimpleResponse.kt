package com.myhumandesignhd.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SimpleResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("message") val message: String = "",
)