package ru.get.hd.ui.faq

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentFaqBinding
import ru.get.hd.event.FaqClickedEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.faq.adapter.FaqAdapter
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel

class FaqFragment : BaseFragment<FaqViewModel, FragmentFaqBinding>(
    ru.get.hd.R.layout.fragment_faq,
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
        router.navigateTo(Screens.faqDetailScreen(
            title = if (App.preferences.locale == "ru") e.faq.titleRu
            else e.faq.titleEn,
            desc = if (App.preferences.locale == "ru") e.faq.textRu
            else e.faq.textEn
        ))
    }

    inner class Handler {

        fun onBackClicked(v: View) {
            pressBack()
        }

    }
}