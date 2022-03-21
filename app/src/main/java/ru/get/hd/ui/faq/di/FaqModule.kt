package ru.get.hd.ui.faq.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.faq.FaqFragment

@Module
interface FaqModule {
    @ContributesAndroidInjector
    fun inject(): FaqFragment
}