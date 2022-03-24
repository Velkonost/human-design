package ru.get.hd.ui.bodygraph.diagram.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.bodygraph.diagram.DiagramFragment
import ru.get.hd.ui.bodygraph.first.BodygraphFirstFragment

@Module
interface DiagramModule {
    @ContributesAndroidInjector
    fun inject(): DiagramFragment
}