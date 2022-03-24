package ru.get.hd.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.get.hd.App
import ru.get.hd.di.scope.AppScope
import ru.get.hd.push.di.ServiceBuilderModule
import ru.get.hd.rest.di.RetrofitModule
import ru.get.hd.ui.activity.main.ViewModelModule
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.transit.TransitFragment
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