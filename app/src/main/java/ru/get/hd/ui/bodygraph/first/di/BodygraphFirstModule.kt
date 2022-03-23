package ru.get.hd.ui.bodygraph.first.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.affirmation.AffirmationFragment
import ru.get.hd.ui.bodygraph.first.BodygraphFirstFragment

@Module
interface BodygraphFirstModule {
    @ContributesAndroidInjector
    fun inject(): BodygraphFirstFragment
}