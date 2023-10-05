package com.myhumandesignhd.ui.affirmation.di

import com.myhumandesignhd.ui.affirmation.AffirmationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AffirmationModule {
    @ContributesAndroidInjector
    fun inject(): AffirmationFragment
}