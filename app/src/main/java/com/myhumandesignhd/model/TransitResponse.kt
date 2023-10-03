package com.myhumandesignhd.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransitResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("message") val message: String? = null,
    @field:JsonProperty("data") val data: TransitResponseData = TransitResponseData(),
) : Parcelable

@Parcelize
data class TransitResponseData(
    @field:JsonProperty("code") val code: Int? = null,

    @field:JsonProperty("onlyBirthGates") val onlyBirthGates: ArrayList<TransitionGate> = arrayListOf(),
    @field:JsonProperty("onlyCurrentGates") val onlyCurrentGates: ArrayList<TransitionGate> = arrayListOf(),
    @field:JsonProperty("onlyBirthChannels") val onlyBirthChannels: ArrayList<TransitionChannel> = arrayListOf(),
    @field:JsonProperty("onlyCurrentChannels") val onlyCurrentChannels: ArrayList<TransitionChannel> = arrayListOf(),
    @field:JsonProperty("birthDesignPlanets") val birthDesignPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
    @field:JsonProperty("birthPersonalityPlanets") val birthPersonalityPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
    @field:JsonProperty("currentDesignPlanets") val currentDesignPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
    @field:JsonProperty("currentPersonalityPlanets") val currentPersonalityPlanets: ArrayList<TransitionPlanet> = arrayListOf(),
) : Parcelable

@Parcelize
data class TransitionGate(
    @field:JsonProperty("number") val number: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
) : Parcelable

@Parcelize
data class TransitionChannel(
    @field:JsonProperty("number") val number: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("description") val description: String = "",
) : Parcelable

@Parcelize
data class TransitionPlanet(
    @field:JsonProperty("gate") val gate: Int = 0,
    @field:JsonProperty("line") val line: Int = 0,
) : Parcelable