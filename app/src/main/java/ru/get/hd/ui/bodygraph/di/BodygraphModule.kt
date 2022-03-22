package ru.get.hd.ui.bodygraph.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.bodygraph.BodygraphFragment
import ru.get.hd.ui.faq.detail.FaqDetailFragment

@Module
interface BodygraphModule {
    @ContributesAndroidInjector
    fun inject(): BodygraphFragment
}