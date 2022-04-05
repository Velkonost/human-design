package ru.get.hd.ui.compatibility.detail.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.compatibility.CompatibilityFragment
import ru.get.hd.ui.compatibility.detail.CompatibilityDetailFragment

@Module
interface CompatibilityDetailModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityDetailFragment
}