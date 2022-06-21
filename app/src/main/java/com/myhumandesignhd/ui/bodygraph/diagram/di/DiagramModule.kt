package com.myhumandesignhd.ui.bodygraph.diagram.di

import com.myhumandesignhd.ui.bodygraph.diagram.DiagramFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface DiagramModule {
    @ContributesAndroidInjector
    fun inject(): DiagramFragment
}