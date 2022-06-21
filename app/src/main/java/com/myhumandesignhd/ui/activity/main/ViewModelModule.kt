package com.myhumandesignhd.ui.activity.main

import com.myhumandesignhd.di.SpecificReposModule
import com.myhumandesignhd.ui.CommonViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [CommonViewModelModule::class, MainProvidersModule::class, SpecificReposModule::class])
interface ViewModelModule {
    @ContributesAndroidInjector
    fun inject(): MainActivity
}

@Module
interface MainProvidersModule {
//    @Binds
//    @IntoMap
//    @ViewModelKey(MainViewModel::class)
//    fun mainViewModel(m: MainViewModel): ViewModel


}