package ru.get.hd.ui.compatibility

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonCenterAlign
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.overlay.BalloonOverlayRect
import kotlinx.android.synthetic.main.item_diagram.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentCompatibilityBinding
import ru.get.hd.event.AddChildClickEvent
import ru.get.hd.event.AddPartnerClickEvent
import ru.get.hd.event.CompatibilityChildStartClickEvent
import ru.get.hd.event.CompatibilityStartClickEvent
import ru.get.hd.event.DeleteChildEvent
import ru.get.hd.event.DeletePartnerEvent
import ru.get.hd.event.HelpType
import ru.get.hd.event.ShowHelpEvent
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.model.getDateStr
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.compatibility.adapter.CompatibilityAdapter
import ru.get.hd.ui.transit.TransitFragment
import ru.get.hd.util.convertDpToPx
import ru.get.hd.vm.BaseViewModel

class CompatibilityFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityBinding>(
    ru.get.hd.R.layout.fragment_compatibility,
    CompatibilityViewModel::class,
    Handler::class
) {

    private var isPartnersHelpShowing = false
    private var isChildrenHelpShowing = false

    private val compatibilityAdapter: CompatibilityAdapter by lazy {
        CompatibilityAdapter()
    }

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
    }

    @Subscribe
    fun onDeletePartnerEvent(e: DeletePartnerEvent) {
        baseViewModel.deleteUser(e.partnerId)
    }

    @Subscribe
    fun onDeleteChildEvent(e: DeleteChildEvent) {
        baseViewModel.deleteChild(e.childId)
    }

    @Subscribe
    fun onCompatibilityStartClickEvent(e: CompatibilityStartClickEvent) {
        baseViewModel.setupCompatibility(
            lat1 = e.user.lat,
            lon1 = e.user.lon,
            date = e.user.getDateStr(),
        ) {
            router.navigateTo(Screens.compatibilityDetailScreen(
                name = e.user.name,
                title =
                "${if (App.preferences.locale == "ru") e.user.subtitle1Ru
                else e.user.subtitle1En} • ${e.user.subtitle2}",
                chartResId = e.chartResId
            ))
        }
    }

    @Subscribe
    fun onCompatibilityChildStartClickEvent(e: CompatibilityChildStartClickEvent) {
        router.navigateTo(Screens.compatibilityChildScreen(e.childId))
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

    private fun showPartnersHelp() {
        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setTextGravity(Gravity.CENTER)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_compatibility_partners))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setIsVisibleOverlay(true)
            .setOverlayShape(BalloonOverlayRect)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                isPartnersHelpShowing = false
                App.preferences.isCompatibilityPartnersHelpShown = true
            }
            .build()

        balloon.showAlignBottom(
            binding.partnersTitle,
            xOff = requireContext().convertDpToPx(32f).toInt(),
            yOff = requireContext().convertDpToPx(6f).toInt()
        )
    }

    private fun showChildrenHelp() {
        val balloon = Balloon.Builder(context!!)
            .setArrowSize(15)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setTextGravity(Gravity.CENTER)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setMaxWidth(300)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(12f)
            .setCornerRadius(10f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_compatibility_children))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setIsVisibleOverlay(true)
            .setOverlayShape(BalloonOverlayRect)
            .setBackgroundColor(
                Color.parseColor("#4D494D")
            )
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setOnBalloonDismissListener {
                isChildrenHelpShowing = false
                App.preferences.isCompatibilityChildrenHelpShown = true
            }
            .build()

        balloon.showAlignBottom(
            binding.childrenTitle,
            xOff = requireContext().convertDpToPx(-32f).toInt(),
            yOff = requireContext().convertDpToPx(6f).toInt()
        )
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

        if (!App.preferences.isCompatibilityPartnersHelpShown && !isPartnersHelpShowing) {
            isPartnersHelpShowing = true
            android.os.Handler().postDelayed({
                showPartnersHelp()
            }, 500)

        }

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

        if (!App.preferences.isCompatibilityChildrenHelpShown && !isChildrenHelpShowing) {
            isChildrenHelpShowing = true
            android.os.Handler().postDelayed({
                showChildrenHelp()
            }, 500)

        }

    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = compatibilityAdapter

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val partners = baseViewModel.getAllUsers()
                .toMutableList().filter { it.id != App.preferences.currentUserId }
            val children = baseViewModel.getAllChildren()

            compatibilityAdapter.createList(partners, children.filter { it.parentId == App.preferences.currentUserId })
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

    @Subscribe
    fun onAddPartnerClickEvent(e: AddPartnerClickEvent) {
        router.navigateTo(Screens.addUserScreen(fromCompatibility = true))
    }

    @Subscribe
    fun onAddChildClickEvent(e: AddChildClickEvent) {
        router.navigateTo(Screens.addUserScreen(isChild = true))
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
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