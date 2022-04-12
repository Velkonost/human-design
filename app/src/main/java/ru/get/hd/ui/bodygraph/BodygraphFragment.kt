package ru.get.hd.ui.bodygraph

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
import androidx.viewpager.widget.ViewPager
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import kotlinx.android.synthetic.main.fragment_bodygraph.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.event.BodygraphCenterClickEvent
import ru.get.hd.event.HelpType
import ru.get.hd.event.SetupNavMenuEvent
import ru.get.hd.event.ShowHelpEvent
import ru.get.hd.event.ToBodygraphClickEvent
import ru.get.hd.event.ToDecryptionClickEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.alpha07
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07
import ru.get.hd.vm.BaseViewModel

class BodygraphFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphBinding>(
    ru.get.hd.R.layout.fragment_bodygraph,
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

        setupUserData()
        setupZnaks()
        EventBus.getDefault().post(SetupNavMenuEvent())

        isFirstFragmentLaunch = false
    }

    override fun onResume() {
        super.onResume()

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    @Subscribe
    fun onShowHelpEvent(e: ShowHelpEvent) {
        showHelp(e.type)
    }

    private fun showHelp(type: HelpType) {
        if (type == HelpType.BodygraphDecryption) {
            if (!App.preferences.bodygraphToDecryptionHelpShown)
                showToDecryptionHelp()
            else EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphAddDiagram))
        } else if (type == HelpType.BodygraphCenters) {
            if (!App.preferences.bodygraphCentersHelpShown)
                showCentersHelp()
        }
        else if (type == HelpType.BodygraphAddDiagram) {
            if (!App.preferences.bodygraphAddDiagramHelpShown)
                showAddDiagramHelp()
            else EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))
        }
    }

    private fun showAddDiagramHelp() {
        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(0.9f)
            .setTextGravity(Gravity.CENTER)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_add_diagram))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setIsVisibleOverlay(true)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                App.preferences.bodygraphAddDiagramHelpShown = true
                EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))
            }
            .build()

        balloon.showAlignBottom(binding.icAddUser, xOff = -requireContext().convertDpToPx(112f).toInt())
    }

    private fun setupUserData() {
        if (baseViewModel.isCurrentUserInitialized()) {
            if (isFirstFragmentLaunch)
                binding.userName.setTextAnimation(baseViewModel.currentUser.name)
            else binding.userName.text = baseViewModel.currentUser.name
        }

        baseViewModel.currentUserSetupEvent.observe(viewLifecycleOwner) {
            if (it) {
                if (isFirstFragmentLaunch)
                    binding.userName.setTextAnimation(baseViewModel.currentUser.name)
                else binding.userName.text = baseViewModel.currentUser.name
            }
        }

        baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
            if (it.typeRu.isNotEmpty() && it.line.isNotEmpty() && it.profileRu.isNotEmpty()) {
                if (isFirstFragmentLaunch) {
                    binding.subtitle1.setTextAnimation07(
                        "${if (App.preferences.locale == "ru") it.typeRu else it.typeEn} • " +
                                "${it.line} • " +
                                "${if (App.preferences.locale == "ru") it.profileRu else it.profileEn}"
                    ) {
                        binding.subtitle1.alpha = 0.5f
                    }
                } else {
                    binding.subtitle1.text =
                        "${if (App.preferences.locale == "ru") it.typeRu else it.typeEn} • " +
                            "${it.line} • " +
                            "${if (App.preferences.locale == "ru") it.profileRu else it.profileEn}"
                }
            }


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

        binding.icAddUser.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.icSettings.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

//        first
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
                it.activeCentres,
                it.inactiveCentres,
                isTouchable = true
            )

            if (!it.design.planets.isNullOrEmpty()) {
                binding.rightZnak1.setTextAnimation07(
                    "${it.design.planets[0].gate}.${it.design.planets[0].line}"
                ) {
                    binding.designTitle.alpha1(500)
                    binding.znak1Red.alpha07(500)
                }

                binding.rightZnak2.setTextAnimation07(
                    "${it.design.planets[1].gate}.${it.design.planets[1].line}"
                ) {
                    binding.znak2Red.alpha07(500)
                }

                binding.rightZnak3.setTextAnimation07(
                    "${it.design.planets[2].gate}.${it.design.planets[2].line}"
                ) {
                    binding.znak3Red.alpha07(500)
                }

                binding.rightZnak4.setTextAnimation07(
                    "${it.design.planets[3].gate}.${it.design.planets[3].line}"
                ) {
                    binding.znak4Red.alpha07(500)
                }

                binding.rightZnak5.setTextAnimation07(
                    "${it.design.planets[4].gate}.${it.design.planets[4].line}"
                ) {
                    binding.znak5Red.alpha07(500)
                }

                binding.rightZnak6.setTextAnimation07(
                    "${it.design.planets[5].gate}.${it.design.planets[5].line}"
                ) {
                    binding.znak6Red.alpha07(500)
                }

                binding.rightZnak7.setTextAnimation07(
                    "${it.design.planets[6].gate}.${it.design.planets[6].line}"
                ) {
                    binding.znak7Red.alpha07(500)
                }

                binding.rightZnak8.setTextAnimation07(
                    "${it.design.planets[7].gate}.${it.design.planets[7].line}"
                ) {
                    binding.znak8Red.alpha07(500)
                }

                binding.rightZnak9.setTextAnimation07(
                    "${it.design.planets[8].gate}.${it.design.planets[8].line}"
                ) {
                    binding.znak9Red.alpha07(500)
                }

                binding.rightZnak10.setTextAnimation07(
                    "${it.design.planets[9].gate}.${it.design.planets[9].line}"
                ) {
                    binding.znak10Red.alpha07(500)
                }

                binding.rightZnak11.setTextAnimation07(
                    "${it.design.planets[10].gate}.${it.design.planets[10].line}"
                ) {
                    binding.znak11Red.alpha07(500)
                }

                binding.rightZnak12.setTextAnimation07(
                    "${it.design.planets[11].gate}.${it.design.planets[11].line}"
                ) {
                    binding.znak12Red.alpha07(500)
                }

                binding.rightZnak13.setTextAnimation07(
                    "${it.design.planets[12].gate}.${it.design.planets[12].line}"
                ) {
                    binding.znak13Red.alpha07(500)
                }
            }

            if (!it.personality.planets.isNullOrEmpty()) {
                binding.blueZnak1.setTextAnimation07(
                    "${it.personality.planets[0].gate}.${it.personality.planets[0].line}"
                ) {
                    binding.transitTitle.alpha1(500)
                    binding.znak1Blue.alpha07(500)
                }

                binding.blueZnak2.setTextAnimation07(
                    "${it.personality.planets[1].gate}.${it.personality.planets[1].line}"
                ) {
                    binding.znak2Blue.alpha07(500)
                }

                binding.blueZnak3.setTextAnimation07(
                    "${it.personality.planets[2].gate}.${it.personality.planets[2].line}"
                ) {
                    binding.znak3Blue.alpha07(500)
                }

                binding.blueZnak4.setTextAnimation07(
                    "${it.personality.planets[3].gate}.${it.personality.planets[3].line}"
                ) {
                    binding.znak4Blue.alpha07(500)
                }

                binding.blueZnak5.setTextAnimation07(
                    "${it.personality.planets[4].gate}.${it.personality.planets[4].line}"
                ) {
                    binding.znak5Blue.alpha07(500)
                }

                binding.blueZnak6.setTextAnimation07(
                    "${it.personality.planets[5].gate}.${it.personality.planets[5].line}"
                ) {
                    binding.znak6Blue.alpha07(500)
                }

                binding.blueZnak7.setTextAnimation07(
                    "${it.personality.planets[6].gate}.${it.personality.planets[6].line}"
                ) {
                    binding.znak7Blue.alpha07(500)
                }

                binding.blueZnak8.setTextAnimation07(
                    "${it.personality.planets[7].gate}.${it.personality.planets[7].line}"
                ) {
                    binding.znak8Blue.alpha07(500)
                }

                binding.blueZnak9.setTextAnimation07(
                    "${it.personality.planets[8].gate}.${it.personality.planets[8].line}"
                ) {
                    binding.znak9Blue.alpha07(500)
                }

                binding.blueZnak10.setTextAnimation07(
                    "${it.personality.planets[9].gate}.${it.personality.planets[9].line}"
                ) {
                    binding.znak10Blue.alpha07(500)
                }

                binding.blueZnak11.setTextAnimation07(
                    "${it.personality.planets[10].gate}.${it.personality.planets[10].line}"
                ) {
                    binding.znak11Blue.alpha07(500)
                }

                binding.blueZnak12.setTextAnimation07(
                    "${it.personality.planets[11].gate}.${it.personality.planets[11].line}"
                ) {
                    binding.znak12Blue.alpha07(500)
                }

                binding.blueZnak13.setTextAnimation07(
                    "${it.personality.planets[12].gate}.${it.personality.planets[12].line}"
                ) {
                    binding.znak13Blue.alpha07(500)
                }
            }
        }
    }

    private fun showCentersHelp() {
        val view = View(
            requireContext()
        )
        view.layoutParams = LinearLayout.LayoutParams(
            1,
            1
        )
        view.x = binding.bodygraphView.getTopPoint().x.toFloat()
        view.y = binding.bodygraphView.getTopPoint().y.toFloat()

        binding.bodygraphContainer.addView(view)

        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(0.5f)
            .setTextGravity(Gravity.CENTER)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_centers))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setIsVisibleOverlay(true)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                App.preferences.bodygraphCentersHelpShown = true
            }
            .build()


        balloon.showAlignTop(view)
    }

    private fun showToDecryptionHelp() {
        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
            .setArrowPosition(0.5f)
            .setTextGravity(Gravity.START)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_bodygraph_to_decryption))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setIsVisibleOverlay(true)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                App.preferences.bodygraphToDecryptionHelpShown = true
                EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphAddDiagram))
            }
            .build()

        balloon.showAlignTop(binding.toDescryptionContainer)
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
        view.x =
            if (e.isXCenter) binding.bodygraphContainer2.width / 2f
            else e.x
        view.y = e.y

        binding.bodygraphContainer2.addView(view)

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
        private var instance: BodygraphFragment? = null

        private val LOCK = Any()

        operator fun invoke() = BodygraphFragment.instance ?: synchronized(LOCK) {
            BodygraphFragment.instance ?: BodygraphFragment.buildBodygraphFragment()
                .also { BodygraphFragment.instance = it }
        }

        private fun buildBodygraphFragment() = BodygraphFragment()
    }

    inner class Handler {

        fun onAddUserClicked(v: View) {
            router.navigateTo(Screens.diagramScreen())
        }

        fun onSettingsClicked(v: View) {
            router.navigateTo(Screens.settingsScreen())
        }

    }

}