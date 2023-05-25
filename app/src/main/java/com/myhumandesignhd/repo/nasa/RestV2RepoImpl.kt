package com.myhumandesignhd.repo.nasa

import com.myhumandesignhd.model.response.SimpleResponse
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.rest.RestServiceV2
import com.myhumandesignhd.util.ext.subscribeIoObserveMain
import io.reactivex.Single
import javax.inject.Inject

class RestV2RepoImpl @Inject constructor(
    private val restServiceV2: RestServiceV2
) : RestV2Repo {

    override fun loginFb(accessToken: String): Single<SimpleResponse> =
        restServiceV2.loginFb(accessToken).subscribeIoObserveMain()
}