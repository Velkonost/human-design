package ru.get.hd.ui.faq.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentFaqDetailBinding
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.faq.FaqViewModel
import ru.get.hd.util.ext.setTextAnimation

class FaqDetailFragment : BaseFragment<FaqViewModel, FragmentFaqDetailBinding>(
    ru.get.hd.R.layout.fragment_faq_detail,
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