package ru.get.hd.ui.loader.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.loader.LoaderFragment

@Module
interface LoaderModule {
    @ContributesAndroidInjector
    fun inject(): LoaderFragment
}