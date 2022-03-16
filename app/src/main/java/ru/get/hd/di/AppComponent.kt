package ru.get.hd.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.get.hd.App
import ru.get.hd.di.scope.AppScope
import ru.get.hd.push.di.ServiceBuilderModule
import ru.get.hd.rest.di.RetrofitModule
import ru.get.hd.ui.activity.main.ViewModelModule

@AppScope
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        RetrofitModule::class,
        ServiceBuilderModule::class,
        AppModule::class,
        ReposModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        FragmentsModule::class
    ]
)
interface AppComponent : AndroidInjector<App>