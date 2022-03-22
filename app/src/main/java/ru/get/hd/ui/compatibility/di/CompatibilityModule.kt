package ru.get.hd.ui.compatibility.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.bodygraph.BodygraphFragment
import ru.get.hd.ui.compatibility.CompatibilityFragment

@Module
interface CompatibilityModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityFragment
}