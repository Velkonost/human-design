package com.myhumandesignhd.repo.nasa

import com.myhumandesignhd.App
import com.myhumandesignhd.model.BodygraphData
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.model.request.CheckLoginRequestBody
import com.myhumandesignhd.model.request.CreateBodygraphBody
import com.myhumandesignhd.model.request.EditBodygraphBody
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.request.LoginEmailBody
import com.myhumandesignhd.model.request.LoginFbBody
import com.myhumandesignhd.model.response.BodygraphResponse
import com.myhumandesignhd.model.response.GoogleAccessTokenResponse
import com.myhumandesignhd.model.response.LoginEmailResponse
import com.myhumandesignhd.model.response.LoginResponse
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.rest.RestServiceV2
import com.myhumandesignhd.util.ext.subscribeIoObserveMain
import io.reactivex.Single
import javax.inject.Inject

class RestV2RepoImpl @Inject constructor(
    private val restServiceV2: RestServiceV2
) : RestV2Repo {

    override fun loginFb(accessToken: String, bodygraphs: List<BodygraphData>): Single<LoginResponse> =
        restServiceV2.loginFb(LoginFbBody(accessToken, bodygraphs)).subscribeIoObserveMain()

    override fun loginGoogle(accessToken: String, bodygraphs: List<BodygraphData>): Single<LoginResponse> =
        restServiceV2.loginGoogle(LoginFbBody(accessToken, bodygraphs)).subscribeIoObserveMain()

    override fun loginEmail(email: String, deviceId: String, bodygraphs: List<BodygraphData>): Single<LoginEmailResponse> =
        restServiceV2.loginEmail(LoginEmailBody(email, deviceId, bodygraphs)).subscribeIoObserveMain()

    override fun checkLogin(deviceId: String): Single<LoginResponse> =
        restServiceV2.checkLogin(CheckLoginRequestBody(deviceId)).subscribeIoObserveMain()

    override fun getGoogleAccessToken(
        body: GoogleAccessTokenBody
    ): Single<GoogleAccessTokenResponse> = restServiceV2
        .getGoogleAccessToken(body = body).subscribeIoObserveMain()

    override fun getAllBodygraphs(): Single<List<BodygraphResponse>> =
        restServiceV2.getAllBodygraphs().subscribeIoObserveMain()

    override fun getChildren(): Single<List<BodygraphResponse>> =
        restServiceV2.getChildren().subscribeIoObserveMain()

    override fun createBodygraph(
        name: String, date: String, lat: String, lon: String,
        isActive: Boolean, isChild: Boolean, utcTimestamp: Long?
    ): Single<BodygraphResponse> =
        restServiceV2
            .createBodygraph(CreateBodygraphBody(
                name, date, lat, lon, isActive, isChild, utcTimestamp
            )).subscribeIoObserveMain()

    override fun getBodygraph(
        date: String?,
        lat: String?,
        lon: String?,
        name: String?,
        utcTimestamp: Long?
    ): Single<BodygraphResponse?> =
        if (App.preferences.authToken.isNullOrEmpty()) {
            restServiceV2
                .getBodygraph(date, lat, lon, name, utcTimestamp)
                .subscribeIoObserveMain()
        } else {
            restServiceV2
                .getBodygraph()
                .subscribeIoObserveMain()
        }

    override fun getChild(childId: Int): Single<BodygraphResponse> =
        restServiceV2
            .getChild(childId)
            .subscribeIoObserveMain()

    override fun editBodygraph(
        bodygraphId: Long,
        name: String?,
        date: String?,
        lat: String?,
        lon: String?,
        isActive: Boolean?
    ): Single<BodygraphResponse> =
        restServiceV2
            .editBodygraph(bodygraphId, EditBodygraphBody(name, date, lat, lon, isActive))
            .subscribeIoObserveMain()

    override fun getTransit(currentDate: String): Single<TransitResponse> =
        restServiceV2.getTransit(currentDate).subscribeIoObserveMain()

    override fun getCompatibility(id: String, light: Boolean): Single<CompatibilityResponse> =
        restServiceV2.getCompatibility(id, light).subscribeIoObserveMain()
}
