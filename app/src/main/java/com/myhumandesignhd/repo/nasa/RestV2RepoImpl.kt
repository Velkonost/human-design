package com.myhumandesignhd.repo.nasa

import com.myhumandesignhd.App
import com.myhumandesignhd.model.AffirmationResponse
import com.myhumandesignhd.model.BodygraphData
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.DailyAdviceResponse
import com.myhumandesignhd.model.FaqResponse
import com.myhumandesignhd.model.ForecastResponse
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.model.request.CheckLoginRequestBody
import com.myhumandesignhd.model.request.CreateBodygraphBody
import com.myhumandesignhd.model.request.EditBodygraphBody
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.request.LoginEmailBody
import com.myhumandesignhd.model.request.LoginFbBody
import com.myhumandesignhd.model.response.BodygraphListResponse
import com.myhumandesignhd.model.response.GoogleAccessTokenResponse
import com.myhumandesignhd.model.response.InjuryResponse
import com.myhumandesignhd.model.response.LoginResponse
import com.myhumandesignhd.model.response.SimpleResponse
import com.myhumandesignhd.model.response.SubscriptionStatusResponse
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.rest.RestServiceV2
import com.myhumandesignhd.util.ext.subscribeIoObserveMain
import io.reactivex.Single
import javax.inject.Inject

class RestV2RepoImpl @Inject constructor(private val restServiceV2: RestServiceV2) : RestV2Repo {

    override fun loginFb(
        accessToken: String,
        bodygraphs: List<BodygraphData>
    ): Single<LoginResponse> =
        restServiceV2.loginFb(
            LoginFbBody(
                accessToken,
                App.preferences.uniqueUserId ?: "",
                bodygraphs
            )
        ).subscribeIoObserveMain()

    override fun loginGoogle(
        accessToken: String,
        bodygraphs: List<BodygraphData>
    ): Single<LoginResponse> =
        restServiceV2.loginGoogle(
            LoginFbBody(
                accessToken,
                App.preferences.uniqueUserId ?: "",
                bodygraphs
            )
        ).subscribeIoObserveMain()

    override fun loginEmail(
        email: String,
        deviceId: String,
        bodygraphs: List<BodygraphData>
    ): Single<LoginResponse> =
        restServiceV2.loginEmail(
            LoginEmailBody(
                email,
                deviceId,
                App.preferences.uniqueUserId ?: "",
                bodygraphs
            )
        )
            .subscribeIoObserveMain()

    override fun checkLogin(deviceId: String, email: String): Single<LoginResponse> =
        restServiceV2.checkLogin(CheckLoginRequestBody(deviceId, email)).subscribeIoObserveMain()

    override fun getGoogleAccessToken(
        body: GoogleAccessTokenBody
    ): Single<GoogleAccessTokenResponse> = restServiceV2
        .getGoogleAccessToken(body = body).subscribeIoObserveMain()

    override fun getAllBodygraphs(): Single<BodygraphListResponse> =
        restServiceV2.getAllBodygraphs().subscribeIoObserveMain()

    override fun createBodygraph(
        name: String, date: String, lat: String, lon: String,
        isActive: Boolean, isChild: Boolean, utcTimestamp: Long?
    ): Single<BodygraphListResponse> =
        restServiceV2
            .createBodygraph(
                CreateBodygraphBody(
                    name, date, lat, lon, isActive, isChild, utcTimestamp
                )
            ).subscribeIoObserveMain()

    override fun editBodygraph(
        bodygraphId: Long,
        name: String?,
        date: String?,
        lat: String?,
        lon: String?,
        isActive: Boolean?
    ): Single<BodygraphListResponse> =
        restServiceV2
            .editBodygraph(bodygraphId, EditBodygraphBody(name, date, lat, lon, isActive))
            .subscribeIoObserveMain()

    override fun getTransit(currentDate: String): Single<TransitResponse> =
        restServiceV2.getTransit(currentDate).subscribeIoObserveMain()

    override fun getCompatibility(id: String, light: Boolean): Single<CompatibilityResponse> =
        restServiceV2.getCompatibility(id, light).subscribeIoObserveMain()

    override fun getForecast(userDate: String): Single<ForecastResponse> =
        restServiceV2.getForecast(
            userDate = userDate,
            weekStartsOn = when (App.preferences.locale) {
                "en" -> "sun"
                else -> "mon"
            }
        ).subscribeIoObserveMain()

    override fun getDailyAdvice(userDate: String): Single<DailyAdviceResponse> =
        restServiceV2.getDailyAdvice(userDate).subscribeIoObserveMain()

    override fun getAffirmation(userDate: String): Single<AffirmationResponse> =
        restServiceV2.getAffirmation(userDate).subscribeIoObserveMain()

    override fun startInjury(): Single<InjuryResponse> =
        restServiceV2.startInjury().subscribeIoObserveMain()

    override fun checkInjury(): Single<InjuryResponse> =
        restServiceV2.checkInjury().subscribeIoObserveMain()

    override fun deleteBodygraph(id: Long): Single<BodygraphListResponse> =
        restServiceV2.deleteBodygraph(id).subscribeIoObserveMain()

    override fun getFaq(): Single<FaqResponse> = restServiceV2.getFaq().subscribeIoObserveMain()

    override fun checkSubscriptionStatus(): Single<SubscriptionStatusResponse> =
        restServiceV2.checkSubscription().subscribeIoObserveMain()

    override fun deleteAcc(): Single<SimpleResponse> =
        restServiceV2.deleteAcc().subscribeIoObserveMain()

    override fun cancelStripeSubscription(): Single<SubscriptionStatusResponse> =
        restServiceV2.cancelStripeSubscription().subscribeIoObserveMain()
}
