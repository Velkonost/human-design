package ru.get.hd.ui.bodygraph.second.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel
import ru.get.hd.ui.bodygraph.first.BodygraphFirstFragment
import ru.get.hd.ui.bodygraph.second.BodygraphSecondFragment

@Module
interface BodygraphSecondModule {
    @ContributesAndroidInjector
    fun inject(): BodygraphSecondFragment
}