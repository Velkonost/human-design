package com.myhumandesignhd.ui.bodygraph.second.di

import com.myhumandesignhd.ui.bodygraph.second.BodygraphSecondFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface BodygraphSecondModule {
    @ContributesAndroidInjector
    fun inject(): BodygraphSecondFragment
}