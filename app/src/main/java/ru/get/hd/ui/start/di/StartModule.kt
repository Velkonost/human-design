package ru.get.hd.ui.start.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.start.StartFragment

@Module
interface StartModule {
    @ContributesAndroidInjector
    fun inject(): StartFragment
}