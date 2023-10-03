package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cycle(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("name") val name: String = "",
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("age") val age: String = "",
) : Parcelable