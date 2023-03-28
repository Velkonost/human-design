package com.myhumandesignhd.model

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

@Parcelize
data class GeocodingNominatimResponse(
    @field:JsonProperty("lon") val lon: String = "",
): Parcelable

@Parcelize
data class GeocodingNominatimFeature(
    @field:JsonProperty("display_name") val placeName: String = "",
    @field:JsonProperty("lat") val lat: String = "",
    @field:JsonProperty("lon") val lon: String = "",
    @field:JsonProperty("address") val address: GeocodingNominatimAddress = GeocodingNominatimAddress(),
): Parcelable

@Parcelize
data class GeocodingNominatimAddress(
    @field:JsonProperty("city") val city: String = "",
    @field:JsonProperty("country") val country: String = "",
    @field:JsonProperty("municipality") val municipality: String = "",
    @field:JsonProperty("state") val state: String = "",
): Parcelable