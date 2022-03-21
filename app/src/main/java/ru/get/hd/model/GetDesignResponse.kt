package ru.get.hd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetDesignResponse(
    @field:JsonProperty("type") val type: String = "",
    @field:JsonProperty("type_en") val typeEn: String = "",
    @field:JsonProperty("type_ru") val typeRu: String = "",
    @field:JsonProperty("type_id") val typeId: Int = 0,
    @field:JsonProperty("profile") val profile: String = "",
    @field:JsonProperty("profile_en") val profileEn: String = "",
    @field:JsonProperty("profile_ru") val profileRu: String = "",
    @field:JsonProperty("line") val line: String = "",
    @field:JsonProperty("parentDescription") val parentDescription: String = "",
    @field:JsonProperty("authority") val authority: Authority = Authority(),
    @field:JsonProperty("active_centres") val activeCentres: List<Center> = listOf(),
    @field:JsonProperty("inactive_centres") val inactiveCentres: List<Center> = listOf(),

    ) : Parcelable

@Parcelize
data class Authority(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String = "",
    @field:JsonProperty("description") val description: String = "",
) : Parcelable

@Parcelize
data class Planet(
    @field:JsonProperty("gate") val gate: Int = 0,
    @field:JsonProperty("line") val line: Int = 0,
) : Parcelable

@Parcelize
data class Center(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String = "",
    @field:JsonProperty("description") val description: String = "",
    @field:JsonProperty("shortDescription") val shortDescription: String = "",
) : Parcelable

@Parcelize
data class GetDesignDescription(
    @field:JsonProperty("type") val type: String = "",
    @field:JsonProperty("type_title") val typeTitle: String = "",
    @field:JsonProperty("profile") val profile: String = "",
    @field:JsonProperty("profile_title") val profileTitle: String = "",
) : Parcelable