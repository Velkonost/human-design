package ru.get.hd.di

import dagger.Module
import ru.get.hd.ui.splash.di.SplashModule
import ru.get.hd.ui.start.di.StartModule

@Module(
    includes = [
        SplashModule::class,
        StartModule::class
    ]
)
class FragmentsModule