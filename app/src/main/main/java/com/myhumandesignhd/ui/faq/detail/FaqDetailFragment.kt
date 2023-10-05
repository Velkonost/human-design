package com.myhumandesignhd.ui.faq.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentFaqDetailBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.faq.FaqViewModel
import org.greenrobot.eventbus.EventBus

class FaqDetailFragment : BaseFragment<FaqViewModel, FragmentFaqDetailBinding>(
    R.layout.fragment_faq_detail,
    FaqViewModel::class,
    Handler::class
) {

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))

        binding.title.text = arguments?.getString("title")!!
        binding.desc.text = arguments?.getString("desc")!!
    }

    override fun updateThemeAndLocale() {
        binding.faqsTitle.text = App.resourcesProvider.getStringLocale(R.string.faq_title)

        binding.faqDetailContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.faqsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.faqBack.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.desc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )
    }

    inner class Handler {
        fun onBackClicked(v: View) {
            pressBack()
//            ru.get.hd.navigation.Navigator.goBack(this@FaqDetailFragment)
        }
    }
}