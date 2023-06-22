package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CompatibilityResponse(
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("line") val line: String = "",
    @field:JsonProperty("profileTitle") val profileTitle: String = "",
    @field:JsonProperty("descrTitle") val descrTitle: String = "",
    @field:JsonProperty("descrnext") val descrNext: String = "",
    @field:JsonProperty("profileDescription") val profileDescription: String = "",
    @field:JsonProperty("channels") val channels: ArrayList<CompatibilityChannel> = arrayListOf(),
    @field:JsonProperty("newDescriptions") val newDescriptions: ArrayList<NewDescriptions> = arrayListOf(),
) : Parcelable

@Parcelize
data class NewDescriptions(
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("percentage") val percentage: Int = 0,
    @field:JsonProperty("description") val description: ArrayList<Description> = arrayListOf(),
) : Parcelable

@Parcelize
data class Description(
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("text") val text: String = "",
) : Parcelable

@Parcelize
data class CompatibilityChannel(
    @field:JsonProperty("number") val number: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("type") val type: String = "",
) : Parcelable