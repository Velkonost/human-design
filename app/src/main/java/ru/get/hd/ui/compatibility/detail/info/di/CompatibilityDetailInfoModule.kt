package ru.get.hd.ui.compatibility.detail.info.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.compatibility.detail.CompatibilityDetailFragment
import ru.get.hd.ui.compatibility.detail.info.CompatibilityDetailInfoFragment

@Module
interface CompatibilityDetailInfoModule {
    @ContributesAndroidInjector
    fun inject(): CompatibilityDetailInfoFragment
}