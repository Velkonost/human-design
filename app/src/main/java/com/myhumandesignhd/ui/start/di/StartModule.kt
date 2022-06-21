package com.myhumandesignhd.ui.start.di

import com.myhumandesignhd.ui.start.StartFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface StartModule {
    @ContributesAndroidInjector
    fun inject(): StartFragment
}