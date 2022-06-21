package com.myhumandesignhd.ui.paywall.di

import com.myhumandesignhd.ui.loader.LoaderFragment
import com.myhumandesignhd.ui.paywall.PaywallFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface PaywallModule {
    @ContributesAndroidInjector
    fun inject(): PaywallFragment
}