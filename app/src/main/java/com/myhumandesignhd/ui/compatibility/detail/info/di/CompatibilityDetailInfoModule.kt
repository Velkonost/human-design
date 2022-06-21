package com.myhumandesignhd.ui.compatibility.detail.info.di

import com.myhumandesignhd.ui.compatibility.detail.info.CompatibilityDetailInfoFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CompatibilityDetailInfoModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityDetailInfoFragment
}