package com.myhumandesignhd.rest

import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.request.LoginEmailBody
import com.myhumandesignhd.model.request.LoginFbBody
import com.myhumandesignhd.model.response.GoogleAccessTokenResponse
import com.myhumandesignhd.model.response.LoginEmailResponse
import com.myhumandesignhd.model.response.LoginResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Url

interface RestServiceV2 {

    @POST("api/login/facebook")
    fun loginFb(@Body loginFbBody: LoginFbBody): Single<LoginResponse>

    @POST("api/login/google")
    fun loginGoogle(@Body loginFbBody: LoginFbBody): Single<LoginResponse>

    @POST("api/login/email")
    fun loginEmail(@Body loginEmailBody: LoginEmailBody): Single<LoginEmailResponse>

    @POST
    fun getGoogleAccessToken(
        @Url url: String = "https://www.googleapis.com/oauth2/v4/token",
        @Body body: GoogleAccessTokenBody
    ): Single<GoogleAccessTokenResponse>

    @GET("api/user")
    fun getUser()

    @PATCH("api/user/edit")
    @FormUrlEncoded
    fun editUser(
        @Field("name") name: String? = null,
        @Field("email") email: String? = null,
        @Field("password") password: String? = null,
        @Field("birthDatetime") birthDatetime: String? = null,
        @Field("location") location: String? = null,
        @Field("lat") lat: String? = null,
        @Field("lon") lon: String? = null,
        @Field("gender") gender: String? = null
    )
}