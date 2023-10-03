package com.myhumandesignhd.model.response

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InjuryResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("message") val message: String? = null,
    @field:JsonProperty("data") val data: InjuryData = InjuryData(),
) : Parcelable

@Parcelize
data class InjuryData(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("percent") val percent: Int = 0,
    @field:JsonProperty("endsIn") val endedAt: Long = 0L,
) : Parcelable