package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forecast(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("ru") val ru: String = "",
    @field:JsonProperty("en") val en: String = "",
    @field:JsonProperty("es") val es: String = "",
    @field:JsonProperty("rutitle") val titleRu: String = "",
    @field:JsonProperty("estitle") val titleEs: String = "",
    @field:JsonProperty("entitle") val titleEn: String = "",
) : Parcelable