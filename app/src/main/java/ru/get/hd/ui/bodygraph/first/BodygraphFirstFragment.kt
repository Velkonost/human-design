package ru.get.hd.ui.bodygraph.first

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphFirstBinding
import ru.get.hd.event.BodygraphCenterClickEvent
import ru.get.hd.event.ToDecryptionClickEvent
import ru.get.hd.ui.base.BaseFragment
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

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()

//        binding.bodygraphView.initBase()
//        binding.bodygraphView.refreshDrawableState()
//        binding.bodygraphView.refreshDrawableState()
//        binding.bodygraphView.requestLayout()

    }

    @SuppressLint("SetTextI18n")
    private fun setupZnaks() {
        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            binding.bodygraphView.setupData(
                it.design,
                it.personality,
                it.activeCentres,
                it.inactiveCentres,
                isTouchable = true
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

    @Subscribe
    fun onBodygraphCenterClickEvent(e: BodygraphCenterClickEvent) {
        val view = View(
            requireContext()
        )
        view.layoutParams = LinearLayout.LayoutParams(
            1,
            1
        )
        view.x = e.x
        view.y = e.y

        binding.bodygraphContainer.addView(view)

        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(e.arrowPosition)
            .setTextGravity(Gravity.START)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText("<small><strong>${e.title}</strong></small><br>${e.desc}")
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                binding.balloonBg.isVisible = false
            }
            .build()

        if (e.alignTop) balloon.showAlignTop(view, xOff = e.xOffset)
        else balloon.showAlignBottom(view, xOff = e.xOffset)

        binding.balloonBg.isVisible = true
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