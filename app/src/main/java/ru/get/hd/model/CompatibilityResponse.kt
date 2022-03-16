package ru.get.hd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CompatibilityResponse(
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("line") val line: String = "",
    @field:JsonProperty("profileTitle") val profileTitle: String = "",
    @field:JsonProperty("profileDescription") val profileDescription: String = "",
    @field:JsonProperty("channels") val channels: ArrayList<CompatibilityChannel> = arrayListOf(),
): Parcelable

@Parcelize
data class CompatibilityChannel(
    @field:JsonProperty("number") val number: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("type") val type: String = "",
): Parcelable