package ru.get.hd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransitResponse(
    @field:JsonProperty("only_birth_gates") val onlyBirthGates: ArrayList<TransitionGate> = arrayListOf(),
    @field:JsonProperty("only_current_gates") val onlyCurrentGates: ArrayList<TransitionGate> = arrayListOf(),
    @field:JsonProperty("only_birth_channels") val onlyBirthChannels: ArrayList<TransitionChannel> = arrayListOf(),
    @field:JsonProperty("only_current_channels") val onlyCurrentChannels: ArrayList<TransitionChannel> = arrayListOf(),
    @field:JsonProperty("birth_design_planets") val birthDesignPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
    @field:JsonProperty("birth_personality_planets") val birthPersonalityPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
    @field:JsonProperty("current_design_planets") val currentDesignPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
    @field:JsonProperty("current_personality_planets") val currentPersonalityPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
): Parcelable

@Parcelize
data class TransitionGate(
    @field:JsonProperty("number") val number: Int = 0,
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
): Parcelable

@Parcelize
data class TransitionChannel(
    @field:JsonProperty("number") val number: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
): Parcelable

@Parcelize
data class TransitionPlanet(
    @field:JsonProperty("gate") val gate: Int = 0,
    @field:JsonProperty("line") val line: Int = 0,
): Parcelable