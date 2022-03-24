package ru.get.hd.ui.transit.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.start.StartFragment
import ru.get.hd.ui.transit.TransitFragment
import javax.inject.Singleton

@Module
interface TransitModule {

    @ContributesAndroidInjector
    fun inject(): TransitFragment
}