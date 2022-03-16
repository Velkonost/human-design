package ru.get.hd.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.get.hd.App
import ru.get.hd.di.scope.AppScope

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