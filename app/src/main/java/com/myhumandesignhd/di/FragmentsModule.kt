package com.myhumandesignhd.di

import com.myhumandesignhd.ui.adduser.di.AddUserModule
import com.myhumandesignhd.ui.affirmation.di.AffirmationModule
import com.myhumandesignhd.ui.bodygraph.di.BodygraphModule
import com.myhumandesignhd.ui.bodygraph.diagram.di.DiagramModule
import com.myhumandesignhd.ui.bodygraph.first.di.BodygraphFirstModule
import com.myhumandesignhd.ui.bodygraph.second.di.BodygraphSecondModule
import com.myhumandesignhd.ui.compatibility.child.di.CompatibilityChildModule
import com.myhumandesignhd.ui.compatibility.detail.di.CompatibilityDetailModule
import com.myhumandesignhd.ui.compatibility.detail.info.di.CompatibilityDetailInfoModule
import com.myhumandesignhd.ui.compatibility.di.CompatibilityModule
import com.myhumandesignhd.ui.faq.detail.di.FaqDetailModule
import com.myhumandesignhd.ui.faq.di.FaqModule
import com.myhumandesignhd.ui.loader.di.LoaderModule
import com.myhumandesignhd.ui.description.di.DescriptionModule
import com.myhumandesignhd.ui.paywall.di.PaywallModule
import com.myhumandesignhd.ui.settings.di.SettingsModule
import com.myhumandesignhd.ui.settings.personal.di.PersonalInfoModule
import com.myhumandesignhd.ui.start.di.StartModule
import com.myhumandesignhd.ui.transit.di.TransitModule
import dagger.Module

@Module(
    includes = [
        StartModule::class,
        SettingsModule::class,
        FaqModule::class,
        FaqDetailModule::class,
        BodygraphModule::class,
        TransitModule::class,
        AffirmationModule::class,
        CompatibilityModule::class,
        CompatibilityDetailModule::class,
        CompatibilityDetailInfoModule::class,
        CompatibilityChildModule::class,
        BodygraphFirstModule::class,
        BodygraphSecondModule::class,
        DiagramModule::class,
        AddUserModule::class,
        PersonalInfoModule::class,
        LoaderModule::class,
        PaywallModule::class,
        DescriptionModule::class
    ]
)
class FragmentsModule