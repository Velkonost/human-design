package ru.get.hd.di

import dagger.Module
import dagger.Provides
import ru.get.hd.di.scope.AppScope
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.repo.nasa.RestRepoImpl

@Module
class ReposModule {
//    @AppScope
//    @Provides
//    fun homeRepo(r: HomeRepoImpl): HomeRepo = r

    @AppScope
    @Provides
    fun nasaRepo(r: RestRepoImpl): RestRepo = r
}