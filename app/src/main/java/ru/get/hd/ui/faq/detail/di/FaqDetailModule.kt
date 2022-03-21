package ru.get.hd.ui.faq.detail.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.faq.detail.FaqDetailFragment

@Module
interface FaqDetailModule {
    @ContributesAndroidInjector
    fun inject(): FaqDetailFragment
}