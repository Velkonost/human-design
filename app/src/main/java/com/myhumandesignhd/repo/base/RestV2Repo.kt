package com.myhumandesignhd.repo.base

import com.myhumandesignhd.model.BodygraphData
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.response.BodygraphResponse
import com.myhumandesignhd.model.response.GoogleAccessTokenResponse
import com.myhumandesignhd.model.response.LoginEmailResponse
import com.myhumandesignhd.model.response.LoginResponse
import io.reactivex.Single

interface RestV2Repo {

    fun loginFb(accessToken: String, bodygraphs: List<BodygraphData>): Single<LoginResponse>

    fun loginGoogle(accessToken: String, bodygraphs: List<BodygraphData>): Single<LoginResponse>

    fun loginEmail(email: String, deviceId: String, bodygraphs: List<BodygraphData>): Single<LoginEmailResponse>

    fun checkLogin(deviceId: String): Single<LoginResponse>

    fun getGoogleAccessToken(body: GoogleAccessTokenBody): Single<GoogleAccessTokenResponse>

    fun getAllBodygraphs(): Single<List<BodygraphResponse>>

    fun getChildren(): Single<List<BodygraphResponse>>

    fun createBodygraph(
        name: String,
        date: String,
        lat: String,
        lon: String,
        isActive: Boolean,
        isChild: Boolean,
        utcTimestamp: Long? = null
    ): Single<BodygraphResponse>

    fun getBodygraph(
        date: String? = null,
        lat: String? = null,
        lon: String? = null,
        name: String? = null,
        utcTimestamp: Long? = null
    ): Single<BodygraphResponse?>

    fun getChild(childId: Int): Single<BodygraphResponse>

    fun editBodygraph(
        bodygraphId: Long,
        name: String? = null,
        date: String? = null,
        lat: String? = null,
        lon: String? = null,
        isActive: Boolean? = null,
    ): Single<BodygraphResponse>

    fun getTransit(currentDate: String): Single<TransitResponse>

    fun getCompatibility(id: String, light: Boolean): Single<CompatibilityResponse>
}