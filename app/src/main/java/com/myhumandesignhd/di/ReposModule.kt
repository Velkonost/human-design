package com.myhumandesignhd.di

import com.myhumandesignhd.di.scope.AppScope
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.repo.nasa.RestRepoImpl
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
}