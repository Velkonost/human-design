package com.myhumandesignhd.rest

import com.myhumandesignhd.model.AffirmationResponse
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
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface RestServiceV2 {

    // Register

    @POST("api/login/facebook")
    fun loginFb(@Body loginFbBody: LoginFbBody): Single<LoginResponse>

    @POST("api/login/google")
    fun loginGoogle(@Body loginFbBody: LoginFbBody): Single<LoginResponse>

    @POST("api/login/email")
    fun loginEmail(@Body loginEmailBody: LoginEmailBody): Single<LoginResponse>

    @POST("api/check_login")
    fun checkLogin(@Body checkLoginRequestBody: CheckLoginRequestBody): Single<LoginResponse>

    @POST("api/verify_email")
    fun verifyEmail(
        @Query("device_id") deviceId: String,
        @Query("expires") expires: String,
        @Query("id") id: String,
        @Query("signature") signature: String,
        @Query("token") token: String
    ): Single<LoginResponse>

    @POST
    fun getGoogleAccessToken(
        @Url url: String = "https://www.googleapis.com/oauth2/v4/token",
        @Body body: GoogleAccessTokenBody
    ): Single<GoogleAccessTokenResponse>

    // Bodygraphs

    @GET("api/body_graphs")
    fun getAllBodygraphs(): Single<BodygraphListResponse>

    @POST("api/body_graph")
    fun createBodygraph(@Body createBodygraphBody: CreateBodygraphBody): Single<BodygraphListResponse>

    @PATCH("api/body_graph/{id}")
    fun editBodygraph(
        @Path("id") bodygraphId: Long, @Body editBodygraphBody: EditBodygraphBody
    ): Single<BodygraphListResponse>

    @GET("api/transit")
    fun getTransit(@Query("currentDate") currentDate: String): Single<TransitResponse>

    @GET("api/compatibility/{id}")
    fun getCompatibility(
        @Path("id") id: String, @Query("light") light: Boolean = false
    ): Single<CompatibilityResponse>

    @GET("api/forecast")
    fun getForecast(
        @Query("user_date") userDate: String,
        @Query("week_starts_on") weekStartsOn: String
    ): Single<ForecastResponse>

    @GET("api/daily_advice")
    fun getDailyAdvice(@Query("user_date") userDate: String): Single<DailyAdviceResponse>

    @GET("api/affirmation")
    fun getAffirmation(@Query("user_date") userDate: String): Single<AffirmationResponse>

    @POST("api/injury/identify")
    fun startInjury(): Single<InjuryResponse>

    @GET("api/injury/status")
    fun checkInjury(): Single<InjuryResponse>

    @DELETE("api/body_graph/{id}")
    fun deleteBodygraph(@Path("id") id: Long): Single<BodygraphListResponse>

    @GET("api/faq")
    fun getFaq(): Single<FaqResponse>

    @GET("api/subscription/status")
    fun checkSubscription(): Single<SubscriptionStatusResponse>

    @POST("api/cancel_subscription")
    fun cancelStripeSubscription(): Single<SubscriptionStatusResponse>

    @DELETE("api/user")
    fun deleteAcc(): Single<SimpleResponse>
}