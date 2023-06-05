package com.myhumandesignhd.repo.nasa

import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.request.LoginEmailBody
import com.myhumandesignhd.model.request.LoginFbBody
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

    override fun loginFb(accessToken: String): Single<LoginResponse> =
        restServiceV2.loginFb(LoginFbBody(accessToken)).subscribeIoObserveMain()

    override fun loginGoogle(accessToken: String): Single<LoginResponse> =
        restServiceV2.loginGoogle(LoginFbBody(accessToken)).subscribeIoObserveMain()

    override fun loginEmail(email: String): Single<LoginEmailResponse> =
        restServiceV2.loginEmail(LoginEmailBody(email)).subscribeIoObserveMain()

    override fun getGoogleAccessToken(
        body: GoogleAccessTokenBody
    ): Single<GoogleAccessTokenResponse> = restServiceV2
        .getGoogleAccessToken(body = body).subscribeIoObserveMain()


}