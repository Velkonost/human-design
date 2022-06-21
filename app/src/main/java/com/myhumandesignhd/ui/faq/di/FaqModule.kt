package com.myhumandesignhd.ui.faq.di

import com.myhumandesignhd.ui.faq.FaqFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FaqModule {
    @ContributesAndroidInjector
    fun inject(): FaqFragment
}