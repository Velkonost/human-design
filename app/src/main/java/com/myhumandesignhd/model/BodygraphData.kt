package com.myhumandesignhd.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BodygraphData(
    val name: String,
    val date: String,
    val lat: String,
    val lon: String,
    val isActive: Boolean = false,
    val isChild: Boolean = false,
    val utcTimestamp: String? = null,
    val children: List<BodygraphChildrenData> = emptyList()
) : Parcelable

@Parcelize
data class BodygraphChildrenData(
    val name: String,
    val date: String,
    val lat: String,
    val lon: String,
    val utcTimestamp: String? = null
) : Parcelable