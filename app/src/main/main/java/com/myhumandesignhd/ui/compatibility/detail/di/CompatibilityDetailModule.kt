package com.myhumandesignhd.ui.compatibility.detail.di

import com.myhumandesignhd.ui.compatibility.detail.CompatibilityDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CompatibilityDetailModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityDetailFragment
}