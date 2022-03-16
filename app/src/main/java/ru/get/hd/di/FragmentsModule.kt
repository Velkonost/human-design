package ru.get.hd.di

import dagger.Module
import ru.get.hd.ui.splash.di.SplashModule

@Module(
    includes = [
        SplashModule::class,
    ]
)
class FragmentsModule