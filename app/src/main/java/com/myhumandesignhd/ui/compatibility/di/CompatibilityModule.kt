package com.myhumandesignhd.ui.compatibility.di

import com.myhumandesignhd.ui.compatibility.CompatibilityFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CompatibilityModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityFragment
}