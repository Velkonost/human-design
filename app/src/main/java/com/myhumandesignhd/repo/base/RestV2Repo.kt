package com.myhumandesignhd.repo.base

import com.myhumandesignhd.model.AffirmationResponse
import com.myhumandesignhd.model.BodygraphData
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.DailyAdviceResponse
import com.myhumandesignhd.model.FaqResponse
import com.myhumandesignhd.model.ForecastResponse
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.response.BodygraphListResponse
import com.myhumandesignhd.model.response.GoogleAccessTokenResponse
import com.myhumandesignhd.model.response.InjuryResponse
import com.myhumandesignhd.model.response.LoginResponse
import com.myhumandesignhd.model.response.SimpleResponse
import com.myhumandesignhd.model.response.SubscriptionStatusResponse
import io.reactivex.Single

interface RestV2Repo {

    fun loginFb(accessToken: String, bodygraphs: List<BodygraphData>): Single<LoginResponse>

    fun loginGoogle(accessToken: String, bodygraphs: List<BodygraphData>): Single<LoginResponse>

    fun loginEmail(email: String, deviceId: String, bodygraphs: List<BodygraphData>): Single<LoginResponse>

    fun checkLogin(deviceId: String, email: String): Single<LoginResponse>

    fun getGoogleAccessToken(body: GoogleAccessTokenBody): Single<GoogleAccessTokenResponse>

    fun getAllBodygraphs(): Single<BodygraphListResponse>

    fun createBodygraph(
        name: String,
        date: String,
        lat: String,
        lon: String,
        isActive: Boolean,
        isChild: Boolean,
        utcTimestamp: Long? = null
    ): Single<BodygraphListResponse>

    fun editBodygraph(
        bodygraphId: Long,
        name: String? = null,
        date: String? = null,
        lat: String? = null,
        lon: String? = null,
        isActive: Boolean? = null,
    ): Single<BodygraphListResponse>

    fun getTransit(currentDate: String): Single<TransitResponse>

    fun getCompatibility(id: String, light: Boolean): Single<CompatibilityResponse>

    fun getForecast(userDate: String): Single<ForecastResponse>

    fun getDailyAdvice(userDate: String): Single<DailyAdviceResponse>

    fun getAffirmation(userDate: String): Single<AffirmationResponse>

    fun startInjury(): Single<InjuryResponse>

    fun checkInjury(): Single<InjuryResponse>

    fun deleteBodygraph(id: Long): Single<BodygraphListResponse>

    fun getFaq(): Single<FaqResponse>

    fun checkSubscriptionStatus(): Single<SubscriptionStatusResponse>

    fun cancelStripeSubscription(): Single<SubscriptionStatusResponse>

    fun deleteAcc(): Single<SimpleResponse>
}