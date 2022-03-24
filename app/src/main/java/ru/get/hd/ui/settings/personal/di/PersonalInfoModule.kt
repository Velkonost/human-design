package ru.get.hd.ui.settings.personal.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.get.hd.ui.settings.SettingsFragment
import ru.get.hd.ui.settings.personal.PersonalInfoFragment

@Module
interface PersonalInfoModule {
    @ContributesAndroidInjector
    fun inject(): PersonalInfoFragment
}