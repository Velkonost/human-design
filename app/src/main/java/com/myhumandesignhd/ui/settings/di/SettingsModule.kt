package com.myhumandesignhd.ui.settings.di

import com.myhumandesignhd.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SettingsModule {
    @ContributesAndroidInjector
    fun inject(): SettingsFragment
}