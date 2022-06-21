package com.myhumandesignhd.ui.bodygraph.di

import com.myhumandesignhd.ui.bodygraph.BodygraphFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface BodygraphModule {
    @ContributesAndroidInjector
    fun inject(): BodygraphFragment
}