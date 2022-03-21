package ru.get.hd.ui.settings.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.settings.SettingsFragment

@Module
interface SettingsModule {
    @ContributesAndroidInjector
    fun inject(): SettingsFragment
}