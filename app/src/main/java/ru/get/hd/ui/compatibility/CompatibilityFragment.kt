package ru.get.hd.ui.compatibility

import ru.get.hd.databinding.FragmentCompatibilityBinding
import ru.get.hd.ui.base.BaseFragment

class CompatibilityFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityBinding>(
    ru.get.hd.R.layout.fragment_compatibility,
    CompatibilityViewModel::class,
    Handler::class
) {

    inner class Handler {}

}