package com.myhumandesignhd.ui.loader.di

import com.myhumandesignhd.ui.loader.LoaderFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface LoaderModule {
    @ContributesAndroidInjector
    fun inject(): LoaderFragment
}