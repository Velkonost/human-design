package com.myhumandesignhd.model.request

import com.myhumandesignhd.model.BodygraphData

data class LoginEmailBody(
    val email: String,
    val deviceId: String,
    val customerId: String,
    val bodyGraphs: List<BodygraphData>
)