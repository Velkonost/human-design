package ru.get.hd.ui.settings.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.settings.SettingsFragment
import ru.get.hd.ui.splash.SplashFragment

@Module
interface SettingsModule {
    @ContributesAndroidInjector
    fun inject(): SettingsFragment
}