package com.myhumandesignhd.model.response

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.myhumandesignhd.model.Center
import com.myhumandesignhd.model.ChildrenDescriptions
import com.myhumandesignhd.model.Cycle
import com.myhumandesignhd.model.Design
import com.myhumandesignhd.model.GetDesignDescription
import com.myhumandesignhd.model.Personality
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BodygraphListResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("data") val data: List<BodygraphResponse> = emptyList(),
    @field:JsonProperty("message") val message: String = "",
) : Parcelable

@Parcelize
data class BodygraphResponse(
    @field:JsonProperty("id") val id: Long = 0L,
    @field:JsonProperty("name") val name: String = "",
    @field:JsonProperty("birthDatetime") val birthDatetime: String = "",
    @field:JsonProperty("lat") val lat: String = "",
    @field:JsonProperty("lon") val lon: String = "",
    @field:JsonProperty("isActive") val isActive: Boolean = false,
    @field:JsonProperty("isChild") val isChild: Boolean = false,
    @field:JsonProperty("utcTimestamp") val utcTimestamp: Long = 0L,
    @field:JsonProperty("type") val type: String = "",
    @field:JsonProperty("typeId") val typeId: Int = 0,
    @field:JsonProperty("profile") val profile: String = "",
    @field:JsonProperty("line") val line: String = "",
    @field:JsonProperty("parentDescription") val parentDescription: String = "",
    @field:JsonProperty("kidDescription") val kidDescription: String = "",

    // Bodygraph View
    @field:JsonProperty("personality") val personality: Personality = Personality(),
    @field:JsonProperty("design") val design: Design = Design(),
    @field:JsonProperty("other") val other: Design = Design(),

    @field:JsonProperty("description") val description: GetDesignDescription = GetDesignDescription(),
    @field:JsonProperty("childrenDescriptions") val childrenDescriptions: ChildrenDescriptions = ChildrenDescriptions(),
    @field:JsonProperty("activeCentres") val activeCentres: List<Center> = listOf(),
    @field:JsonProperty("inactiveCentres") val inactiveCentres: List<Center> = listOf(),

    // About
    @field:JsonProperty("authority") val authority: AboutItem = AboutItem(),
    @field:JsonProperty("injury") val injury: AboutItem = AboutItem(),
    @field:JsonProperty("strategy") val strategy: AboutItem = AboutItem(),
    @field:JsonProperty("business") val business: AboutItem = AboutItem(),
    @field:JsonProperty("nutriton") val nutrition: AboutItem = AboutItem(),
    @field:JsonProperty("environment") val environment: AboutItem = AboutItem(),

    @field:JsonProperty("children") val children: List<BodygraphResponse>? = null,
    @field:JsonProperty("cycles") val cycles: List<Cycle> = emptyList(),
) : Parcelable {
    var compatibilityAvg: Int = 0
}

@Parcelize
data class AboutItem(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String? = "",
    @field:JsonProperty("description") val description: String? = "",
) : Parcelable