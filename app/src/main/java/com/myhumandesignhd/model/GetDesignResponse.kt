package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetDesignResponse(
    @field:JsonProperty("type") val type: String = "",
    @field:JsonProperty("type_en") val typeEn: String = "",
    @field:JsonProperty("type_ru") val typeRu: String = "",
    @field:JsonProperty("type_es") val typeEs: String = "",
    @field:JsonProperty("type_id") val typeId: Int = 0,
    @field:JsonProperty("profile") val profile: String = "",
    @field:JsonProperty("profile_en") val profileEn: String = "",
    @field:JsonProperty("profile_ru") val profileRu: String = "",
    @field:JsonProperty("profile_es") val profileEs: String = "",
    @field:JsonProperty("line") val line: String = "",
    @field:JsonProperty("parentDescription") val parentDescription: String = "",
    @field:JsonProperty("authority") val authority: Authority = Authority(),
    @field:JsonProperty("active_centres") val activeCentres: List<Center> = listOf(),
    @field:JsonProperty("inactive_centres") val inactiveCentres: List<Center> = listOf(),
    @field:JsonProperty("design") val design: Design = Design(),
    @field:JsonProperty("personality") val personality: Personality = Personality(),
    @field:JsonProperty("description") val description: GetDesignDescription = GetDesignDescription(),
    @field:JsonProperty("injury") val injury: Injury = Injury(),
    @field:JsonProperty("strategy") val strategy: Strategy = Strategy(),
    @field:JsonProperty("nutriton") val nutrition: Nutrition = Nutrition(),
    @field:JsonProperty("environment") val environment: Environment = Environment(),
    ) : Parcelable

@Parcelize
data class Design(
    @field:JsonProperty("planets") val planets: List<Planet> = listOf(),
    @field:JsonProperty("gates") val gates: List<Int> = listOf(),
    @field:JsonProperty("channels") val channels: HashMap<String, DesignChannel> = hashMapOf(),
): Parcelable

@Parcelize
data class Personality(
    @field:JsonProperty("planets") val planets: List<Planet> = listOf(),
    @field:JsonProperty("gates") val gates: List<Int> = listOf(),
    @field:JsonProperty("channels") val channels: HashMap<String, DesignChannel> = hashMapOf(),
): Parcelable

@Parcelize
data class DesignChannel(
    @field:JsonProperty("state") val state: String = "",
    @field:JsonProperty("gate") val gate: String = "",
): Parcelable

@Parcelize
data class Authority(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String = "",
    @field:JsonProperty("description") val description: String = "",
) : Parcelable

@Parcelize
data class Injury(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String? = "",
    @field:JsonProperty("description") val description: String? = "",
): Parcelable

@Parcelize
data class Strategy(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String? = "",
    @field:JsonProperty("description") val description: String? = "",
): Parcelable

@Parcelize
data class Nutrition(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String? = "",
    @field:JsonProperty("description") val description: String? = "",
): Parcelable

@Parcelize
data class Environment(
    @field:JsonProperty("id") val id: Int = 0,
    @field:JsonProperty("name") val name: String? = "",
    @field:JsonProperty("description") val description: String? = "",
): Parcelable

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
    @field:JsonProperty("gates") val gates: HashMap<String, String> = hashMapOf(),
    @field:JsonProperty("gates_titles") val gatesTitles: HashMap<String, String> = hashMapOf(),
    @field:JsonProperty("channels") val channels: HashMap<String, String> = hashMapOf(),
    @field:JsonProperty("channels_titles") val channelsTitles: HashMap<String, String> = hashMapOf(),
) : Parcelable

data class AboutItem(
    val name: String? = "",
    val description: String? = "",
    val type: AboutType,

    val title: String = "",
    val subtitle: String = "",
    val text: String = ""
)

enum class AboutType {
    BODYGRAPH,
    TYPE,
    PROFILE,
    AUTHORITY,
    STRATEGY,
    INJURY,
    NUTRITION,
    ENVIRONMENT
}

