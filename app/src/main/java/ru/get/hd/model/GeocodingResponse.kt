package ru.get.hd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeocodingResponse(
    @field:JsonProperty("features") val features: List<GeocodingFeature> = emptyList(),
) : Parcelable

@Parcelize
data class GeocodingFeature(
    @field:JsonProperty("text") val text: String = "",
    @field:JsonProperty("place_name") val placeName: String = "",
    @field:JsonProperty("center") val center: List<Float> = emptyList(),
    @field:JsonProperty("place_type") val placeType: List<String> = emptyList(),

): Parcelable