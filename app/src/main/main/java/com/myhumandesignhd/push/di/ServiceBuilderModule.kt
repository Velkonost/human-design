package com.myhumandesignhd.push.di

import com.myhumandesignhd.push.FirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceBuilderModule {
    @ContributesAndroidInjector
    fun inject(): FirebaseMessagingService

}