package ru.get.hd.ui.compatibility.child.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.compatibility.child.CompatibilityChildFragment
import ru.get.hd.ui.compatibility.detail.CompatibilityDetailFragment

@Module
interface CompatibilityChildModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityChildFragment
}