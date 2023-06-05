package com.myhumandesignhd.repo.base

import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.response.GoogleAccessTokenResponse
import com.myhumandesignhd.model.response.LoginEmailResponse
import com.myhumandesignhd.model.response.LoginResponse
import io.reactivex.Single

interface RestV2Repo {

    fun loginFb(accessToken: String): Single<LoginResponse>

    fun loginGoogle(accessToken: String): Single<LoginResponse>

    fun loginEmail(email: String): Single<LoginEmailResponse>

    fun getGoogleAccessToken(body: GoogleAccessTokenBody): Single<GoogleAccessTokenResponse>
}