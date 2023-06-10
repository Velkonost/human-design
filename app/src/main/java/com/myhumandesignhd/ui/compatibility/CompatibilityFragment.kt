package com.myhumandesignhd.ui.compatibility

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentCompatibilityNewBinding
import com.myhumandesignhd.event.AddChildClickEvent
import com.myhumandesignhd.event.AddPartnerClickEvent
import com.myhumandesignhd.event.ChangeCompatibilityViewPagerUserInputEvent
import com.myhumandesignhd.event.CompatibilityChildStartClickEvent
import com.myhumandesignhd.event.CompatibilityStartClickEvent
import com.myhumandesignhd.event.DeleteChildEvent
import com.myhumandesignhd.event.DeletePartnerEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.model.getDateStr
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.compatibility.adapter.CompatibilityAdapter
import com.myhumandesignhd.util.convertDpToPx
import com.myhumandesignhd.vm.BaseViewModel
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import com.yandex.metrica.YandexMetrica
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CompatibilityFragment : BaseFragment<CompatibilityViewModel, FragmentCompatibilityNewBinding>(
    R.layout.fragment_compatibility_new,
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

    private val sheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.aboutBottomSheet.bottomSheetContainer)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
        Amplitude.getInstance().logEvent("tab4_screen_shown")

        val sheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    binding.blur.isVisible = false
//                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    binding.blur.isVisible = true
//                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
                }
            }
        }
        sheetBehavior.addBottomSheetCallback(sheetCallback)
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
       YandexMetrica.reportEvent("Tab4AdultsCreatedProfileTapped")

        baseViewModel.setupCompatibility(
            lat1 = e.user.lat,
            lon1 = e.user.lon,
            date = e.user.getDateStr(),
        ) {
            router.navigateTo(
                Screens.compatibilityDetailScreen(
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
        YandexMetrica.reportEvent("Tab4ChildrenCreatedProfileTapped")
        Amplitude.getInstance().logEvent("tab4CheckedFamilyRelationship")

        router.navigateTo(Screens.compatibilityChildScreen(e.childId))
    }

    override fun updateThemeAndLocale() {
        binding.container.setBackgroundColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.title.text = App.resourcesProvider.getStringLocale(R.string.compatibility_title)
        binding.title.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.partnersTitle.text = App.resourcesProvider.getStringLocale(R.string.partners_title)
        binding.childrenTitle.text = App.resourcesProvider.getStringLocale(R.string.children_title)

        binding.selectionBlock.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_selection_block_dark
            else R.drawable.bg_selection_block_light
        )

        if (App.preferences.isCompatibilityFromChild) {
            selectChildren()
//            App.preferences.isCompatibilityFromChild = false
        } else selectPartners()

        setupViewPager()

        with(binding.aboutBottomSheet) {
            sheetTitle.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_sheet_header_dark
                else R.drawable.bg_sheet_header_light
            )
            sheetTitle.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            sheetText.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            icSheetCross.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            sheetContainer.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            ))

            bodygraphTitle.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }


        // add partner btn
        binding.addPartnerBtn.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_add_partner_btn_dark
            else R.drawable.bg_add_partner_btn_light
        )

        binding.addPartnerIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.addPartnerTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
    }

    private fun showSheet(title: String, desc: String = "") {
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        with(binding.aboutBottomSheet) {
            bottomSheetContainer.setBackgroundColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            ))

            backSheet.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_sheet_header_dark
                else R.drawable.bg_sheet_header_light
            )

            closeSheetBtn.setOnClickListener {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            sheetScroll.fullScroll(View.FOCUS_UP)
            sheetTitle.text = title
            sheetText.text = desc
        }
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
            .setCornerRadius(20f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_compatibility_partners))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setOverlayShape(BalloonOverlayRoundRect(40f, 40f))
            .setIsVisibleOverlay(true)
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
            .setCornerRadius(20f)
            .setText(App.resourcesProvider.getStringLocale(R.string.help_compatibility_children))
            .setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.lightColor
                )
            )
            .setTextIsHtml(true)
            .setOverlayColorResource(R.color.helpBgColor)
            .setOverlayShape(BalloonOverlayRoundRect(40f, 40f))
            .setIsVisibleOverlay(true)
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
        Amplitude.getInstance().logEvent("tab4TappedLove")

        binding.addPartnerTitle.text = App.resourcesProvider.getStringLocale(R.string.add_partner_btn_title)

        binding.partnersTitle.setTextColor(ContextCompat.getColor(
            requireContext(), R.color.lightColor
        ))

        binding.childrenTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor0_5
            else R.color.darkColor0_5
        ))

        binding.partnersTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
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
        Amplitude.getInstance().logEvent("tab4TappedFamily")

        binding.addPartnerTitle.text = App.resourcesProvider.getStringLocale(R.string.add_child_btn_title)

        binding.childrenTitle.setTextColor(ContextCompat.getColor(
            requireContext(), R.color.lightColor
        ))

        binding.partnersTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor0_5
            else R.color.darkColor0_5
        ))

        binding.childrenTitle.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bg_selection_item_compatibility
        )

        binding.partnersTitle.background = null

        if (!App.preferences.isCompatibilityChildrenHelpShown && !isChildrenHelpShowing) {
            isChildrenHelpShowing = true
            android.os.Handler().postDelayed({
                showChildrenHelp()
            }, 500)
        }
    }

    @Subscribe
    fun onChangeCompatibilityViewPagerUserInputEvent(e: ChangeCompatibilityViewPagerUserInputEvent) {
//        binding.viewPager.isUserInputEnabled = e.isEnableUserInput
    }

    private fun setupViewPager() {
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.isUserInputEnabled = true

        lifecycleScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val partners = baseViewModel.getAllUsers()
                .toMutableList().filter { it.id != App.preferences.currentUserId }
            val children = baseViewModel.getAllChildren()

            if (!compatibilityAdapter.isCreated)
                compatibilityAdapter.createList(partners, children.filter { it.parentId == App.preferences.currentUserId })
            else compatibilityAdapter.updateList(partners, children.filter { it.parentId == App.preferences.currentUserId })

            val identify = Identify()
            identify.set("partnersadded", partners.size.toString())
            identify.set("kidsadded", children.size.toString())
            Amplitude.getInstance().identify(identify)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (!App.preferences.isCompatibilityFromChild) {
                    when(position) {
                        0 -> selectPartners()
                        else -> selectChildren()
                    }
                }
            }
        })

        binding.viewPager.postDelayed ({
            binding.viewPager.adapter = compatibilityAdapter
            if (App.preferences.isCompatibilityFromChild) {
                App.preferences.isCompatibilityFromChild = false
                binding.viewPager.setCurrentItem(1, false)
                selectChildren()
            }
        }, 150)

    }

    @Subscribe
    fun onAddPartnerClickEvent(e: AddPartnerClickEvent) {
        YandexMetrica.reportEvent("Tab4AddAdultsTapped")
        Amplitude.getInstance().logEvent("tab4TappedAddUser")

        if (App.preferences.isPremiun) {
            router.navigateTo(Screens.addUserScreen(fromCompatibility = true))
        } else {
            router.navigateTo(Screens.paywallScreen(source = "compatibility"))
        }
    }

    @Subscribe
    fun onAddChildClickEvent(e: AddChildClickEvent) {
        YandexMetrica.reportEvent("Tab4AddChildrenTapped")
        Amplitude.getInstance().logEvent("tab4TappedAddKid")

        if (App.preferences.isPremiun) {
            router.navigateTo(Screens.addUserScreen(isChild = true))
        } else {
            router.navigateTo(Screens.paywallScreen(source = "compatibility"))
        }
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

        fun onAddPartnerBtnClicked(v: View) {
            val position = binding.viewPager.currentItem

            if (position == 0) {
                YandexMetrica.reportEvent("Tab4AddAdultsTapped")
                Amplitude.getInstance().logEvent("tab4TappedAddUser")

                if (App.preferences.isPremiun) {
                    router.navigateTo(Screens.addUserScreen(fromCompatibility = true))
                } else {
                    router.navigateTo(Screens.paywallScreen(source = "compatibility"))
                }
            } else {
                YandexMetrica.reportEvent("Tab4AddChildrenTapped")
                Amplitude.getInstance().logEvent("tab4TappedAddKid")

                if (App.preferences.isPremiun) {
                    router.navigateTo(Screens.addUserScreen(isChild = true))
                } else {
                    router.navigateTo(Screens.paywallScreen(source = "compatibility"))
                }
            }
        }

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