package com.myhumandesignhd.ui.description.di

import com.myhumandesignhd.ui.description.DescriptionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface DescriptionModule {
    @ContributesAndroidInjector
    fun inject(): DescriptionFragment
}