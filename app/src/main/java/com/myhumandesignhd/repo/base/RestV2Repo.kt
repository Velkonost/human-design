package com.myhumandesignhd.repo.base

import com.myhumandesignhd.model.BodygraphData
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
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
}