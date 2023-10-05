package com.myhumandesignhd.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.myhumandesignhd.App
import com.myhumandesignhd.di.scope.AppScope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(
    includes = []
)

class AppModule(val app: App) {
    @Provides
    @AppScope
    fun context(): Context = app
}

@Module(includes = [])
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
//
//    @Binds
//    abstract fun bindUserSettingsRepository(context: Context): UserSettingsRepository

}