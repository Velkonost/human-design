package ru.get.hd.ui.compatibility

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentCompatibilityBinding
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.compatibility.adapter.CompatibilityAdapter
import ru.get.hd.ui.transit.TransitFragment
import ru.get.hd.vm.BaseViewModel

class CompatibilityFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityBinding>(
    ru.get.hd.R.layout.fragment_compatibility,
    CompatibilityViewModel::class,
    Handler::class
) {

    private val compatibilityAdapter: CompatibilityAdapter by lazy {
        CompatibilityAdapter()
    }

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun updateThemeAndLocale() {
        binding.compatibilityContainer.setBackgroundColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.compatibilityTitle.text = App.resourcesProvider.getStringLocale(R.string.compatibility_title)
        binding.compatibilityTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.partnersTitle.text = App.resourcesProvider.getStringLocale(R.string.partners_title)
        binding.childrenTitle.text = App.resourcesProvider.getStringLocale(R.string.children_title)

        binding.selectionCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        binding.selectionLinear.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        selectPartners()
        setupViewPager()
    }

    private fun selectPartners() {
        binding.partnersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.childrenTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.partnersTitle.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_affirmation_bg
        )

        binding.childrenTitle.background = null
    }

    private fun selectChildren() {
        binding.childrenTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.partnersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            R.color.unselectText
        ))

        binding.childrenTitle.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_affirmation_bg
        )

        binding.partnersTitle.background = null
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = compatibilityAdapter

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val partners = baseViewModel.getAllUsers()
                .toMutableList().filter { it.id != App.preferences.currentUserId }
            val children = baseViewModel.getAllChildren()

            compatibilityAdapter.createList(partners, children)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when(position) {
                    0 -> selectPartners()
                    1 -> selectChildren()
                }
            }
        })
    }

    companion object {
        @Volatile
        private var instance: CompatibilityFragment? = null

        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK) {
            instance ?: buildFragment()
                .also { instance = it }
        }

        private fun buildFragment() = CompatibilityFragment()
    }

    inner class Handler {

        fun onPartnersClicked(v: View) {
            binding.viewPager.setCurrentItem(0, true)
            selectPartners()
        }

        fun onChildrenClicked(v: View) {
            binding.viewPager.setCurrentItem(1, true)
            selectChildren()
        }

    }

}