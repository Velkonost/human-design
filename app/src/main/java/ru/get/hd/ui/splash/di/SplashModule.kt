package ru.get.hd.ui.splash.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.splash.SplashFragment

@Module
interface SplashModule {

    @ContributesAndroidInjector
    fun inject(): SplashFragment

}