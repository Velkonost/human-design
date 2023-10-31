package com.myhumandesignhd.di

import com.myhumandesignhd.di.scope.AppScope
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.repo.nasa.RestRepoImpl
import com.myhumandesignhd.repo.nasa.RestV2RepoImpl
import dagger.Module
import dagger.Provides

@Module
class ReposModule {
//    @AppScope
//    @Provides
//    fun homeRepo(r: HomeRepoImpl): HomeRepo = r

    @AppScope
    @Provides
    fun nasaRepo(r: RestRepoImpl): RestRepo = r

    @AppScope
    @Provides
    fun restV2Repo(r: RestV2RepoImpl): RestV2Repo = r
}