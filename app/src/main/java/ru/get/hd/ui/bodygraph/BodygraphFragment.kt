package ru.get.hd.ui.bodygraph

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_bodygraph.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.databinding.FragmentFaqBinding
import ru.get.hd.event.SetupNavMenuEvent
import ru.get.hd.event.ToBodygraphClickEvent
import ru.get.hd.event.ToDecryptionClickEvent
import ru.get.hd.event.UpdateToBodygraphCardStateEvent
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.adapter.VerticalViewPagerAdapter
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.faq.FaqViewModel
import ru.get.hd.ui.view.VerticalViewPager
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel

class BodygraphFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphBinding>(
    ru.get.hd.R.layout.fragment_bodygraph,
    BodygraphViewModel::class,
    Handler::class
) {

    private val verticalViewPagerAdapter by lazy {
        VerticalViewPagerAdapter(fragmentManager = childFragmentManager)
    }

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupUserData()
        setupViewPager()

        EventBus.getDefault().post(SetupNavMenuEvent())
    }

    @Subscribe
    fun onToBodygraphClickEvent(e: ToBodygraphClickEvent) {
        if (binding.verticalViewPager.currentItem == 1)
            binding.verticalViewPager.changeCurrentItem(0, 500)
    }

    private fun setupViewPager() {
        binding.verticalViewPager.adapter = verticalViewPagerAdapter
        binding.verticalViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                EventBus.getDefault().post(UpdateToBodygraphCardStateEvent(isVisible = position == 1))
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setupUserData() {
        if (baseViewModel.isCurrentUserInitialized()) {
            binding.userName.setTextAnimation(baseViewModel.currentUser.name)
        }

        baseViewModel.currentUserSetupEvent.observe(viewLifecycleOwner) {
            if (it) {
                binding.userName.setTextAnimation(baseViewModel.currentUser.name)
            }
        }

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            if (it.typeRu.isNotEmpty() && it.line.isNotEmpty() && it.profileRu.isNotEmpty())
                binding.subtitle1.setTextAnimation(
                    "${if (App.preferences.locale == "ru") it.typeRu else it.typeEn} • " +
                            "${it.line} • " +
                            "${if (App.preferences.locale == "ru") it.profileRu else it.profileEn}"
                )

        }
    }

    override fun updateThemeAndLocale() {
        binding.bodygraphContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.userName.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.subtitle1.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

//        binding.subtitle2.setTextColor(ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        ))
//
//        binding.subtitle3.setTextColor(ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        ))
//
//        binding.subtitleSeparator1.background = ContextCompat.getDrawable(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.drawable.bg_subtitle_separator_dark
//            else R.drawable.bg_subtitle_separator_light
//        )
//
//        binding.subtitleSeparator2.background = ContextCompat.getDrawable(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.drawable.bg_subtitle_separator_dark
//            else R.drawable.bg_subtitle_separator_light
//        )
    }

    @Subscribe
    fun onToDecryptionClickEvent(e: ToDecryptionClickEvent) {
        if (e.toDecryption)
            binding.verticalViewPager.changeCurrentItem(1, 500)
        else binding.verticalViewPager.changeCurrentItem(0, 500)
    }

    inner class Handler {}

}