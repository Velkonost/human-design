package com.myhumandesignhd.ui.settings.personal.di

import com.myhumandesignhd.ui.settings.personal.PersonalInfoFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface PersonalInfoModule {
    @ContributesAndroidInjector
    fun inject(): PersonalInfoFragment
}