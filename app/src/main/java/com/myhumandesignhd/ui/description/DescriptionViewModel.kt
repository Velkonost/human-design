package com.myhumandesignhd.ui.description

import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentBodygraphBinding
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.bodygraph.BodygraphFragment
import com.myhumandesignhd.ui.bodygraph.BodygraphViewModel
import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import javax.inject.Inject

class DescriptionViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}