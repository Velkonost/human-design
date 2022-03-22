package ru.get.hd.ui.transit

import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.databinding.FragmentTransitBinding
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel

class TransitFragment : BaseFragment<TransitViewModel, FragmentTransitBinding>(
    ru.get.hd.R.layout.fragment_transit,
    TransitViewModel::class,
    Handler::class
) {

    inner class Handler {}

}