package ru.get.hd.ui

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.get.hd.di.scope.ViewModelKey
import ru.get.hd.ui.splash.SplashViewModel
import ru.get.hd.vm.*

@Module
interface CommonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun splashViewModel(m: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BaseViewModel::class)
    fun baseViewModel(m: BaseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun settingsViewModel(m: SettingsViewModel): ViewModel

}