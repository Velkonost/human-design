package ru.get.hd.ui.affirmation.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.affirmation.AffirmationFragment
import ru.get.hd.ui.bodygraph.BodygraphFragment

@Module
interface AffirmationModule {
    @ContributesAndroidInjector
    fun inject(): AffirmationFragment
}