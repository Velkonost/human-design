package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cycle(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("name_ru") val nameRu: String = "",
    @field:JsonProperty("name_en") val nameEn: String = "",
    @field:JsonProperty("description_ru") val descriptionRu: String = "",
    @field:JsonProperty("description_en") val descriptionEn: String = "",
    @field:JsonProperty("age_ru") val ageRu: String = "",
    @field:JsonProperty("age_en") val ageEn: String = ""
): Parcelable