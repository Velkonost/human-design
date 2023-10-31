package com.myhumandesignhd.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginEmailResponse(
    @field:JsonProperty("status") val status: String = ""
)