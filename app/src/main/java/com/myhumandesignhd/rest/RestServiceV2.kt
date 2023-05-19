package com.myhumandesignhd.rest

import com.myhumandesignhd.model.request.LoginBody
import com.myhumandesignhd.model.request.RefreshTokenBody
import com.myhumandesignhd.model.request.RegisterBody
import com.myhumandesignhd.model.request.VerifyEmailBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface RestServiceV2 {

    @POST("api/register")
    fun register(@Body registerBody: RegisterBody): String

    @POST("api/verify_email")
    fun verifyEmail(@Body verifyEmailBody: VerifyEmailBody): String

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

    @POST("api/token/refresh")
    fun refreshToken(@Body refreshTokenBody: RefreshTokenBody)

    @POST("api/login")
    fun login(@Body loginBody: LoginBody)
}