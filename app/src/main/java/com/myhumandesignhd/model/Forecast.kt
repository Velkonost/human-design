package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forecast(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("text") val text: String = "",
) : Parcelable

@Parcelize
data class ForecastResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("message") val message: String? = null,
    @field:JsonProperty("data") val data: Forecast = Forecast(),
) : Parcelable