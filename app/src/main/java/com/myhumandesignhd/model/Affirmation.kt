package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Affirmation(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("imageL") val imageL: String = "",
    @field:JsonProperty("imageD") val imageD: String = "",
    @field:JsonProperty("text") val text: String = "",
) : Parcelable

@Parcelize
data class AffirmationResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("message") val message: String? = null,
    @field:JsonProperty("data") val data: Affirmation = Affirmation(),
) : Parcelable