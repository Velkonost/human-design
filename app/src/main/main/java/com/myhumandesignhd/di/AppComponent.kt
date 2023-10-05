package com.myhumandesignhd.di

import android.app.Application
import com.myhumandesignhd.App
import com.myhumandesignhd.di.scope.AppScope
import com.myhumandesignhd.push.di.ServiceBuilderModule
import com.myhumandesignhd.rest.di.RetrofitModule
import com.myhumandesignhd.ui.activity.main.ViewModelModule
import com.myhumandesignhd.ui.transit.TransitFragment
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@AppScope
@Singleton
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
interface AppComponent : AndroidInjector<App> {

    fun inject(app: Application)
    fun inject(fragment: TransitFragment)
//    override fun inject(app: App?)
//
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun application(application: Application?): Builder?
//        fun build(): AppComponent?

}