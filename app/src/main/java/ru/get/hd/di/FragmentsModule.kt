package ru.get.hd.di

import dagger.Module
import ru.get.hd.ui.affirmation.di.AffirmationModule
import ru.get.hd.ui.bodygraph.di.BodygraphModule
import ru.get.hd.ui.compatibility.di.CompatibilityModule
import ru.get.hd.ui.faq.detail.di.FaqDetailModule
import ru.get.hd.ui.faq.di.FaqModule
import ru.get.hd.ui.settings.di.SettingsModule
import ru.get.hd.ui.splash.di.SplashModule
import ru.get.hd.ui.start.di.StartModule
import ru.get.hd.ui.transit.di.TransitModule

@Module(
    includes = [
        SplashModule::class,
        StartModule::class,
        SettingsModule::class,
        FaqModule::class,
        FaqDetailModule::class,
        BodygraphModule::class,
        TransitModule::class,
        AffirmationModule::class,
        CompatibilityModule::class
    ]
)
class FragmentsModule