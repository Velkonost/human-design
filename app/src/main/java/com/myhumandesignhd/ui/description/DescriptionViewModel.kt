package com.myhumandesignhd.ui.description

import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import javax.inject.Inject

class DescriptionViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}