package com.myhumandesignhd.ui.adduser.di

import com.myhumandesignhd.ui.adduser.AddUserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AddUserModule {
    @ContributesAndroidInjector
    fun inject(): AddUserFragment
}