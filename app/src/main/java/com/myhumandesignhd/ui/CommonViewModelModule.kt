package com.myhumandesignhd.ui

import androidx.lifecycle.ViewModel
import com.myhumandesignhd.di.scope.ViewModelKey
import com.myhumandesignhd.ui.affirmation.AffirmationViewModel
import com.myhumandesignhd.ui.bodygraph.BodygraphViewModel
import com.myhumandesignhd.ui.compatibility.CompatibilityViewModel
import com.myhumandesignhd.ui.description.DescriptionViewModel
import com.myhumandesignhd.ui.faq.FaqViewModel
import com.myhumandesignhd.ui.loader.LoaderViewModel
import com.myhumandesignhd.ui.settings.SettingsViewModel
import com.myhumandesignhd.ui.start.StartViewModel
import com.myhumandesignhd.ui.transit.TransitViewModel
import com.myhumandesignhd.vm.BaseViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CommonViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    fun startViewModel(m: StartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BaseViewModel::class)
    fun baseViewModel(m: BaseViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun settingsViewModel(m: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FaqViewModel::class)
    fun faqViewModel(m: FaqViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BodygraphViewModel::class)
    fun bodygraphViewModel(m: BodygraphViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransitViewModel::class)
    fun transitViewModel(m: TransitViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AffirmationViewModel::class)
    fun affirmationViewModel(m: AffirmationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CompatibilityViewModel::class)
    fun compatibilityViewModel(m: CompatibilityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoaderViewModel::class)
    fun loaderViewModel(m: LoaderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DescriptionViewModel::class)
    fun descriptionViewModel(m: DescriptionViewModel): ViewModel

}