package ru.get.hd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DesignChildResponse(
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
    @field:JsonProperty("kidDescription") val kidDescription: String = "",
    @field:JsonProperty("kidDescription_en") val kidDescriptionEn: String = "",
    @field:JsonProperty("kidDescription_ru") val kidDescriptionRu: String = "",
    @field:JsonProperty("design") val design: Design = Design(),
    @field:JsonProperty("personality") val personality: Personality = Personality(),

    ) : Parcelable