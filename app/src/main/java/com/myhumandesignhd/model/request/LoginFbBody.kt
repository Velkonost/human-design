package com.myhumandesignhd.model.request

import com.myhumandesignhd.model.BodygraphData

data class LoginFbBody(
    val access_token: String,
    val bodyGraphs: List<BodygraphData>
)