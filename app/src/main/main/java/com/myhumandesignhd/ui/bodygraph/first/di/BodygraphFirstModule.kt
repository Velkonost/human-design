package com.myhumandesignhd.ui.bodygraph.first.di

import com.myhumandesignhd.ui.bodygraph.first.BodygraphFirstFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface BodygraphFirstModule {

    @ContributesAndroidInjector
    fun inject(): BodygraphFirstFragment

}