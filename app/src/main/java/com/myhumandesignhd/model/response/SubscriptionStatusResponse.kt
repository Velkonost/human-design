package com.myhumandesignhd.model.response

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SubscriptionStatusResponse(
    @field:JsonProperty("status") val status: String = "",
    @field:JsonProperty("data") val data: SubscriptionStatusData? = null,
): Parcelable
@Parcelize
data class SubscriptionStatusData(
    @field:JsonProperty("isActive") val isActive: Boolean = false,
    @field:JsonProperty("isTrial") val isTrial: Boolean = false,
    @field:JsonProperty("isCanceled") val isCanceled: Boolean = false,
    @field:JsonProperty("type") val type: String = "",
    @field:JsonProperty("expiredAt") val expiredAt: String = "",
): Parcelable
