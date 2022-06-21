package com.myhumandesignhd.ui.compatibility.child.di

import com.myhumandesignhd.ui.compatibility.child.CompatibilityChildFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CompatibilityChildModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityChildFragment
}