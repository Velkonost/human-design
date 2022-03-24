package ru.get.hd.ui.adduser.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.adduser.AddUserFragment
import ru.get.hd.ui.bodygraph.diagram.DiagramFragment

@Module
interface AddUserModule {
    @ContributesAndroidInjector
    fun inject(): AddUserFragment
}