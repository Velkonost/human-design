package com.myhumandesignhd.repo.base

import com.myhumandesignhd.model.response.SimpleResponse
import io.reactivex.Single

interface RestV2Repo {

    fun loginFb(accessToken: String): Single<SimpleResponse>
}