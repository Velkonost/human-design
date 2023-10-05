package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Faq(
    @field:JsonProperty("title_ru") val titleRu: String = "",
    @field:JsonProperty("title_en") val titleEn: String = "",
    @field:JsonProperty("title_es") val titleEs: String = "",
    @field:JsonProperty("text_ru") val textRu: String = "",
    @field:JsonProperty("text_en") val textEn: String = "",
    @field:JsonProperty("text_es") val textEs: String = "",
) : Parcelable