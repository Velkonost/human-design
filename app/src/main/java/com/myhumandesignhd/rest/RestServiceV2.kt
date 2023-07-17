package com.myhumandesignhd.rest

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
import io.reactivex.Single
import retrofit2.http.Body
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
    fun loginEmail(@Body loginEmailBody: LoginEmailBody): Single<LoginEmailResponse>

    @POST("api/login/check_login")
    fun checkLogin(@Body checkLoginRequestBody: CheckLoginRequestBody): Single<LoginResponse>

    @POST
    fun getGoogleAccessToken(
        @Url url: String = "https://www.googleapis.com/oauth2/v4/token",
        @Body body: GoogleAccessTokenBody
    ): Single<GoogleAccessTokenResponse>

    // Bodygraphs

    @GET("api/body_graphs")
    fun getAllBodygraphs(): Single<List<BodygraphResponse>>

    @GET("api/body_graph/children")
    fun getChildren(): Single<List<BodygraphResponse>>

    @POST("api/body_graph/new")
    fun createBodygraph(@Body createBodygraphBody: CreateBodygraphBody): Single<BodygraphResponse>

    @GET("api/body_graph")
    fun getBodygraph(
        @Query("date") date: String? = null,
        @Query("lat") lat: String? = null,
        @Query("lon") lon: String? = null,
        @Query("name") name: String? = null,
        @Query("utcTimestamp") utcTimestamp: Long? = null
    ): Single<BodygraphResponse?>

    @GET("api/body_graph/child/{id}")
    fun getChild(@Path("id") childId: Int): Single<BodygraphResponse>

    @PATCH("api/body_graph/{id}/edit")
    fun editBodygraph(
        @Path("id") bodygraphId: Long, @Body editBodygraphBody: EditBodygraphBody
    ): Single<BodygraphResponse>

    @GET("api/transit")
    fun getTransit(@Query("currentDate") currentDate: String): Single<TransitResponse>

    @GET("api/compatibility/{id}")
    fun getCompatibility(
        @Path("id") id: String,
        @Query("light") light: Boolean
    ): Single<CompatibilityResponse>
}