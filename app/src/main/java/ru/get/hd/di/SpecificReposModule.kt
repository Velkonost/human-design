package ru.get.hd.di

import dagger.Module
import dagger.Provides
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.repo.nasa.RestRepoImpl
import ru.get.hd.rest.RestService

@Module
class SpecificReposModule {

    @Provides
    fun nasaRepo(
        restService: RestService
    ): RestRepo = RestRepoImpl(restService)

}