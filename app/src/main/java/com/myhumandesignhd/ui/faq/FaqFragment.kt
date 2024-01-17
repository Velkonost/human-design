package com.myhumandesignhd.ui.faq

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentFaqBinding
import com.myhumandesignhd.event.FaqClickedEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.faq.adapter.FaqAdapter
import com.myhumandesignhd.vm.BaseViewModel
import org.greenrobot.eventbus.Subscribe

class FaqFragment : BaseFragment<FaqViewModel, FragmentFaqBinding>(
    R.layout.fragment_faq,
    FaqViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val faqAdapter by lazy {
        FaqAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

//        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))

        binding.faqsRecycler.adapter = faqAdapter
        baseViewModel.faqsList.firstNotNullOfOrNull {  }
        faqAdapter.createList(baseViewModel.faqsList)
    }

    override fun updateThemeAndLocale() {
        binding.faqsTitle.text = App.resourcesProvider.getStringLocale(R.string.faq_title)

        binding.faqsContainer.setBackgroundColor(
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
    }

    @Subscribe
    fun onFaqClickedEvent(e: FaqClickedEvent) {
        router.navigateTo(
            Screens.faqDetailScreen(
            title = if (App.preferences.locale == "ru") e.faq.titleRu
            else if (App.preferences.locale == "es") e.faq.titleEs
            else e.faq.titleEn,
            desc = if (App.preferences.locale == "ru") e.faq.textRu
            else if (App.preferences.locale == "es") e.faq.textEs
            else e.faq.textEn
        ))
    }

    inner class Handler {

        fun onBackClicked(v: View) {
            pressBack()
        }

    }
}