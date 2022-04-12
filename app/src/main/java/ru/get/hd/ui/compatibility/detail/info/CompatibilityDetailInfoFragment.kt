package ru.get.hd.ui.compatibility.detail.info

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentCompatibilityDetailInfoBinding
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.compatibility.CompatibilityViewModel
import ru.get.hd.ui.compatibility.detail.info.adapter.CompatibilityInfoAdapter

class CompatibilityDetailInfoFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityDetailInfoBinding>(
    ru.get.hd.R.layout.fragment_compatibility_detail_info,
    CompatibilityViewModel::class,
    Handler::class
) {

    private val compatibilityInfoAdapter: CompatibilityInfoAdapter by lazy {
        CompatibilityInfoAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        binding.recycler.adapter = compatibilityInfoAdapter
        compatibilityInfoAdapter.createList()
    }

    override fun updateThemeAndLocale() {
        super.updateThemeAndLocale()

        binding.compatibilityContainer.setBackgroundColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.icArrow.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.compatibilityTitle.text = App.resourcesProvider.getStringLocale(R.string.information_title)
        binding.compatibilityTitle.setTextColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.desc.text = App.resourcesProvider.getStringLocale(R.string.compatibility_information_desc)
        binding.desc.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

    }

    inner class Handler {
        fun onBackClicked(v: View) {
            router.exit()
        }
    }
}