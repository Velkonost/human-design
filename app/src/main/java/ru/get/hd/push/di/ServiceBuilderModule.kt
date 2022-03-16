package ru.get.hd.push.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.push.FirebaseMessagingService

@Module
interface ServiceBuilderModule {
    @ContributesAndroidInjector
    fun inject(): FirebaseMessagingService

}