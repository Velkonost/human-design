package com.myhumandesignhd.model.request

data class CreateBodygraphBody(
    val name: String,
    val date: String,
    val lat: String,
    val lon: String,
    val isActive: Boolean,
    val isChild: Boolean,
    val utcTimestamp: Long? = null
)