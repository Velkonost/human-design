package ru.get.hd.ui.bodygraph

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonCenterAlign
import com.skydoves.balloon.BalloonSizeSpec
import kotlinx.android.synthetic.main.fragment_bodygraph.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.databinding.FragmentFaqBinding
import ru.get.hd.event.HelpType
import ru.get.hd.event.SetupNavMenuEvent
import ru.get.hd.event.ShowHelpEvent
import ru.get.hd.event.ToBodygraphClickEvent
import ru.get.hd.event.ToDecryptionClickEvent
import ru.get.hd.event.UpdateToBodygraphCardStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.adapter.VerticalViewPagerAdapter
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.faq.FaqViewModel
import ru.get.hd.ui.transit.TransitFragment
import ru.get.hd.ui.view.VerticalViewPager
import ru.get.hd.util.convertDpToPx
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
        android.os.Handler().postDelayed({
            setupViewPager()
        }, 50)

        EventBus.getDefault().post(SetupNavMenuEvent())

        isFirstFragmentLaunch = false
    }

    @Subscribe
    fun onShowHelpEvent(e: ShowHelpEvent) {
        showHelp(e.type)
    }

    private fun showHelp(type: HelpType) {
        if (type == HelpType.BodygraphAddDiagram) {
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

    @Subscribe
    fun onToBodygraphClickEvent(e: ToBodygraphClickEvent) {
        if (binding.verticalViewPager.currentItem == 1)
            binding.verticalViewPager.changeCurrentItem(0, 500)
    }

    private fun setupViewPager() {
        binding.verticalViewPager.adapter = verticalViewPagerAdapter
        binding.verticalViewPager.offscreenPageLimit = 1
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
        EventBus.getDefault().post(
            UpdateToBodygraphCardStateEvent(
                isVisible = binding.verticalViewPager.currentItem == 1
            )
        )
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
                    binding.subtitle1.setTextAnimation(
                        "${if (App.preferences.locale == "ru") it.typeRu else it.typeEn} • " +
                                "${it.line} • " +
                                "${if (App.preferences.locale == "ru") it.profileRu else it.profileEn}"
                    )
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

    @Subscribe
    fun onToDecryptionClickEvent(e: ToDecryptionClickEvent) {
        if (e.toDecryption)
            binding.verticalViewPager.changeCurrentItem(1, 500)
        else binding.verticalViewPager.changeCurrentItem(0, 500)
    }

    inner class Handler {

        fun onAddUserClicked(v: View) {
            router.navigateTo(Screens.diagramScreen())
        }

    }

}