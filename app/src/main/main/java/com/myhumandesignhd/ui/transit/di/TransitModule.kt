package com.myhumandesignhd.ui.transit.di

import com.myhumandesignhd.ui.transit.TransitFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface TransitModule {

    @ContributesAndroidInjector
    fun inject(): TransitFragment
}