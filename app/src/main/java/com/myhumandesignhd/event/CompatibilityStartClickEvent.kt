package com.myhumandesignhd.event

import com.myhumandesignhd.model.response.BodygraphResponse

data class CompatibilityStartClickEvent(val user: BodygraphResponse, val chartResId: Int)