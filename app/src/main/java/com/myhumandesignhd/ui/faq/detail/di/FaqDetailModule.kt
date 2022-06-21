package com.myhumandesignhd.ui.faq.detail.di

import com.myhumandesignhd.ui.faq.detail.FaqDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FaqDetailModule {
    @ContributesAndroidInjector
    fun inject(): FaqDetailFragment
}