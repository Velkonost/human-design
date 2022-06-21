package com.myhumandesignhd.ui.loader

import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import javax.inject.Inject

class LoaderViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}