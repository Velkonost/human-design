package ru.get.hd.ui.bodygraph.first

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.databinding.FragmentBodygraphFirstBinding
import ru.get.hd.event.ToDecryptionClickEvent
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.vm.BaseViewModel

class BodygraphFirstFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphFirstBinding>(
    R.layout.fragment_bodygraph_first,
    BodygraphViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupZnaks()

        isFirstFragmentLaunch = false
    }

    override fun updateThemeAndLocale() {
        if (isFirstFragmentLaunch) {
            binding.designTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.design_title))
            binding.transitTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.personality_title))
            binding.toDecryptionText.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.to_decryption_text))
        } else {
            binding.designTitle.text = App.resourcesProvider.getStringLocale(R.string.design_title)
            binding.transitTitle.text = App.resourcesProvider.getStringLocale(R.string.personality_title)
            binding.toDecryptionText.text = App.resourcesProvider.getStringLocale(R.string.to_decryption_text)
        }

        binding.doubleArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))


        binding.toDecryptionText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

    }

    @SuppressLint("SetTextI18n")
    private fun setupZnaks() {
        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            binding.bodygraphView.setupData(
                it.design,
                it.personality,
                it.activeCentres
            )

            if (!it.design.planets.isNullOrEmpty()) {
                binding.rightZnak1.setTextAnimation(
                    "${it.design.planets[0].gate}.${it.design.planets[0].line}"
                ) {
                    binding.designTitle.alpha1(500)
                    binding.znak1Red.alpha1(500)
                }

                binding.rightZnak2.setTextAnimation(
                    "${it.design.planets[1].gate}.${it.design.planets[1].line}"
                ) {
                    binding.znak2Red.alpha1(500)
                }

                binding.rightZnak3.setTextAnimation(
                    "${it.design.planets[2].gate}.${it.design.planets[2].line}"
                ) {
                    binding.znak3Red.alpha1(500)
                }

                binding.rightZnak4.setTextAnimation(
                    "${it.design.planets[3].gate}.${it.design.planets[3].line}"
                ) {
                    binding.znak4Red.alpha1(500)
                }

                binding.rightZnak5.setTextAnimation(
                    "${it.design.planets[4].gate}.${it.design.planets[4].line}"
                ) {
                    binding.znak5Red.alpha1(500)
                }

                binding.rightZnak6.setTextAnimation(
                    "${it.design.planets[5].gate}.${it.design.planets[5].line}"
                ) {
                    binding.znak6Red.alpha1(500)
                }

                binding.rightZnak7.setTextAnimation(
                    "${it.design.planets[6].gate}.${it.design.planets[6].line}"
                ) {
                    binding.znak7Red.alpha1(500)
                }

                binding.rightZnak8.setTextAnimation(
                    "${it.design.planets[7].gate}.${it.design.planets[7].line}"
                ) {
                    binding.znak8Red.alpha1(500)
                }

                binding.rightZnak9.setTextAnimation(
                    "${it.design.planets[8].gate}.${it.design.planets[8].line}"
                ) {
                    binding.znak9Red.alpha1(500)
                }

                binding.rightZnak10.setTextAnimation(
                    "${it.design.planets[9].gate}.${it.design.planets[9].line}"
                ) {
                    binding.znak10Red.alpha1(500)
                }

                binding.rightZnak11.setTextAnimation(
                    "${it.design.planets[10].gate}.${it.design.planets[10].line}"
                ) {
                    binding.znak11Red.alpha1(500)
                }

                binding.rightZnak12.setTextAnimation(
                    "${it.design.planets[11].gate}.${it.design.planets[11].line}"
                ) {
                    binding.znak12Red.alpha1(500)
                }

                binding.rightZnak13.setTextAnimation(
                    "${it.design.planets[12].gate}.${it.design.planets[12].line}"
                ) {
                    binding.znak13Red.alpha1(500)
                }
            }

            if (!it.personality.planets.isNullOrEmpty()) {
                binding.blueZnak1.setTextAnimation(
                    "${it.personality.planets[0].gate}.${it.personality.planets[0].line}"
                ) {
                    binding.transitTitle.alpha1(500)
                    binding.znak1Blue.alpha1(500)
                }

                binding.blueZnak2.setTextAnimation(
                    "${it.personality.planets[1].gate}.${it.personality.planets[1].line}"
                ) {
                    binding.znak2Blue.alpha1(500)
                }

                binding.blueZnak3.setTextAnimation(
                    "${it.personality.planets[2].gate}.${it.personality.planets[2].line}"
                ) {
                    binding.znak3Blue.alpha1(500)
                }

                binding.blueZnak4.setTextAnimation(
                    "${it.personality.planets[3].gate}.${it.personality.planets[3].line}"
                ) {
                    binding.znak4Blue.alpha1(500)
                }

                binding.blueZnak5.setTextAnimation(
                    "${it.personality.planets[4].gate}.${it.personality.planets[4].line}"
                ) {
                    binding.znak5Blue.alpha1(500)
                }

                binding.blueZnak6.setTextAnimation(
                    "${it.personality.planets[5].gate}.${it.personality.planets[5].line}"
                ) {
                    binding.znak6Blue.alpha1(500)
                }

                binding.blueZnak7.setTextAnimation(
                    "${it.personality.planets[6].gate}.${it.personality.planets[6].line}"
                ) {
                    binding.znak7Blue.alpha1(500)
                }

                binding.blueZnak8.setTextAnimation(
                    "${it.personality.planets[7].gate}.${it.personality.planets[7].line}"
                ) {
                    binding.znak8Blue.alpha1(500)
                }

                binding.blueZnak9.setTextAnimation(
                    "${it.personality.planets[8].gate}.${it.personality.planets[8].line}"
                ) {
                    binding.znak9Blue.alpha1(500)
                }

                binding.blueZnak10.setTextAnimation(
                    "${it.personality.planets[9].gate}.${it.personality.planets[9].line}"
                ) {
                    binding.znak10Blue.alpha1(500)
                }

                binding.blueZnak11.setTextAnimation(
                    "${it.personality.planets[10].gate}.${it.personality.planets[10].line}"
                ) {
                    binding.znak11Blue.alpha1(500)
                }

                binding.blueZnak12.setTextAnimation(
                    "${it.personality.planets[11].gate}.${it.personality.planets[11].line}"
                ) {
                    binding.znak12Blue.alpha1(500)
                }

                binding.blueZnak13.setTextAnimation(
                    "${it.personality.planets[12].gate}.${it.personality.planets[12].line}"
                ) {
                    binding.znak13Blue.alpha1(500)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: BodygraphFirstFragment? = null

        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK) {
            instance ?: buildFragment()
                .also { instance = it }
        }

        private fun buildFragment() = BodygraphFirstFragment()
    }

    inner class Handler {
        fun onToDecryptionClicked(v: View) {
            EventBus.getDefault().post(ToDecryptionClickEvent())
        }
    }
}