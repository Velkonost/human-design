package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CompatibilityResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("message") val message: String? = null,
    @field:JsonProperty("data") val data: CompatibilityResponseData = CompatibilityResponseData(),
) : Parcelable

@Parcelize
data class CompatibilityResponseData(
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("line") val line: String = "",
    @field:JsonProperty("profileTitle") val profileTitle: String = "",
    @field:JsonProperty("descrTitle") val descrTitle: String = "",
    @field:JsonProperty("descrnext") val descrNext: String = "",
    @field:JsonProperty("profileDescription") val profileDescription: String = "",
    @field:JsonProperty("channels") val channels: ArrayList<CompatibilityChannel> = arrayListOf(),
) : Parcelable

@Parcelize
data class CompatibilityChannel(
    @field:JsonProperty("number") val number: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("type") val type: String = "",
) : Parcelable