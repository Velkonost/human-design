package ru.get.hd.di

import dagger.Module
import ru.get.hd.ui.faq.detail.di.FaqDetailModule
import ru.get.hd.ui.faq.di.FaqModule
import ru.get.hd.ui.settings.di.SettingsModule
import ru.get.hd.ui.splash.di.SplashModule
import ru.get.hd.ui.start.di.StartModule

@Module(
    includes = [
        SplashModule::class,
        StartModule::class,
        SettingsModule::class,
        FaqModule::class,
        FaqDetailModule::class
    ]
)
class FragmentsModule