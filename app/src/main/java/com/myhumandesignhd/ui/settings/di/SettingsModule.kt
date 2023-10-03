package com.myhumandesignhd.ui.settings.di

import com.myhumandesignhd.ui.settings.SettingsFragment
import com.myhumandesignhd.ui.settings.manageaccount.ManageAccountFragment
import com.myhumandesignhd.ui.settings.managesubscription.ManageSubscriptionFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SettingsModule {
    @ContributesAndroidInjector
    fun inject(): SettingsFragment

    @ContributesAndroidInjector
    fun injectManageAcc(): ManageAccountFragment

    @ContributesAndroidInjector
    fun injectManageSub(): ManageSubscriptionFragment
}