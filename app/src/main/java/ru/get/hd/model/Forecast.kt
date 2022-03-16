package ru.get.hd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forecast(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("ru") val ru: String = "",
    @field:JsonProperty("en") val en: String = "",
): Parcelable