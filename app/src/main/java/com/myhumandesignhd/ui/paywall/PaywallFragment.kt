package com.myhumandesignhd.ui.paywall

import android.animation.Animator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import com.adapty.Adapty
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentPaywallBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.loader.LoaderViewModel
import com.myhumandesignhd.ui.paywall.adapter.Pw3ReviewsAdapter
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.vm.BaseViewModel
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.view_paywall_1.view.*
import kotlinx.android.synthetic.main.view_paywall_2.view.*
import kotlinx.android.synthetic.main.view_paywall_3.view.*
import org.greenrobot.eventbus.EventBus
import com.myhumandesignhd.event.AdaptyLogShowEvent
import com.myhumandesignhd.event.AdaptyMakePurchaseEvent
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.random.Random.Default.nextLong


class PaywallFragment : BaseFragment<LoaderViewModel, FragmentPaywallBinding>(
    R.layout.fragment_paywall,
    LoaderViewModel::class,
    Handler::class
) {

    private var selectedPaywall = PAYWALL_TYPE.PAYWALL1
    private var selectedOffer = 1

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val promoBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.promoBottomSheet.bottomSheetContainer)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(false))
        binding.container.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        binding.promoBottomSheet.container.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            )
        )

        binding.promoBottomSheet.ok.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.promoBottomSheet.promoET.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        binding.promoBottomSheet.promoET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.promoBottomSheet.promoET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        val promoCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.blur.isVisible = false
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.blur.isVisible = true
                }
            }
        }
        promoBehavior.addBottomSheetCallback(promoCallback)


        if (
            App.adaptyPaywallModel != null
            && !App.adaptyPaywallModel!!.customPayload.isNullOrEmpty()
            && App.adaptyPaywallModel!!.customPayload!!.containsKey("id")
        ) {
            when (App.adaptyPaywallModel!!.customPayload?.get("id")!!) {
                "pw_1" -> setupFirstPaywall()
                "pw_2" -> setupSecondPaywall()
                "pw_3" -> setupThirdPaywall()
                else -> setupFirstPaywall()
            }
        } else setupFirstPaywall()

        EventBus.getDefault().post(AdaptyMakePurchaseEvent("P1W"))
        App.preferences.lastPaywall++

        EventBus.getDefault().post(AdaptyLogShowEvent(selectedPaywall))
    }

    override fun onViewModelReady(viewModel: LoaderViewModel) {
        super.onViewModelReady(viewModel)
    }

    private fun launchBilling() {
        val billingPeriod =
            if (selectedPaywall == PAYWALL_TYPE.PAYWALL1) {
                when (selectedOffer) {
                    1 -> "P1M"
                    2 -> "P1W"
                    else -> "P1Y"
                }
            } else {
                when (selectedOffer) {
                    2 -> "P1M"
                    1 -> "P1W"
                    else -> "P1Y"
                }
            }

        val vendorProductId = when(billingPeriod) {
            "P1M" -> "hd_month_sub"
            "P1W" -> "hd_week_sub"
            "P1Y" -> "hd_year_sub"
            else -> "hd_month_sub"
        }

        if (!App.adaptyProducts.isNullOrEmpty()) {
            Adapty.makePurchase(
                requireActivity(),
                App.adaptyPaywallModel!!.products.first { productModel ->
                    productModel.vendorProductId == vendorProductId
                }
            ) { purchaserInfo, purchaseToken, googleValidationResult, product, error ->
                if (error == null) {
                    if (purchaserInfo?.accessLevels?.get("premium")?.isActive == true) {
                        App.preferences.isPremiun = true
                        close()
                    }
                }
            }
        }
    }

    private fun restorePurchase() {
        Adapty.restorePurchases { purchaserInfo, googleValidationResultList, error ->
            if (error == null) {
                // successful restore
                if (purchaserInfo?.accessLevels?.get("premium")?.isActive == true) {
                    App.preferences.isPremiun = true
                    close()
                }
            }
        }
    }

    private fun setupFirstPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL1

        binding.paywall1.isVisible = true
        with(binding.paywall1) {

            paywall1Title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer1Duration.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2Duration.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3Duration.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer1Price.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2Price.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3Price.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer1Text.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2Text.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3Text.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2Title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3Title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1TermsOfUse.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1Policy.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1Restore.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1Promo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline1.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            bottomGradient.isVisible = App.preferences.isDarkTheme
            paywall1Close.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1.setOnClickListener { selectFirstOffer() }
            offer2.setOnClickListener { selectSecondOffer() }
            offer3.setOnClickListener { selectThirdOffer() }
            paywall1TermsOfUse.setOnClickListener { openTermsOfUse() }
            paywall1Policy.setOnClickListener { openPrivacyPolicy() }
            paywall1Restore.setOnClickListener { openRestore() }
            paywall1Close.setOnClickListener { close() }
            paywall1Promo.setOnClickListener { promocodeClicked() }
            startBtn.setOnClickListener { launchBilling() }

            icBigCircle.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            icMidCircle.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val rotate = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )

            rotate.repeatCount = Animation.INFINITE
            rotate.fillAfter = true
            rotate.duration = 100000
            rotate.interpolator = LinearInterpolator()

            val rotateNegative = RotateAnimation(
                0f,
                -360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotateNegative.repeatCount = Animation.INFINITE
            rotateNegative.fillAfter = true
            rotateNegative.duration = 100000
//        rotateNegative.interpolator = LinearInterpolator()

            icBigCircle.startAnimation(rotate)
            icMidCircle.startAnimation(rotateNegative)

            bodygraphView.changeIsAllowDrawTextState(false)
            baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                if (
                    !it.design.channels.isNullOrEmpty()
                    && !it.personality.channels.isNullOrEmpty()
                    && !it.activeCentres.isNullOrEmpty()
                    && !it.inactiveCentres.isNullOrEmpty()
                ) {
                    android.os.Handler().postDelayed({

                        bodygraphView.isVisible = true
                        bodygraphView.scaleXY(1.1f, 1.1f, 1500) {
                            bodygraphView.changeSpeedAnimationFactor(3f)
                            bodygraphView.changeIsAllowDrawLinesState(true)

                        }
                    }, 200)

                }

                bodygraphView.setupData(
                    it.design,
                    it.personality,
                    it.activeCentres,
                    it.inactiveCentres
                )
            }

            when (App.adaptyPaywallModel!!.customPayload?.get("autoselect")) {
                "1" -> selectFirstOffer()
                "2" -> selectSecondOffer()
                "3" -> selectThirdOffer()
                else -> selectSecondOffer()
            }
        }
    }

    private fun setupSecondPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL2

        binding.paywall2.isVisible = true
        with(binding.paywall2) {
            title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text1Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text2Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text3Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text4Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            icLogoPw2.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_paywall_2_logo_dark
                else R.drawable.ic_paywall_2_logo_light
            )

            offer1DurationPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2DurationPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3DurationPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1PricePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2PricePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3PricePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TextPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TextPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3TextPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TitlePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3TitlePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            termsOfUsePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            policyPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            restorePw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline1Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline2Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline3Pw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1PromoPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePw2.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer2TitlePw2.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer3TitlePw2.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            bottomGradientPw2.isVisible = App.preferences.isDarkTheme
            closePw2.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1Pw2.setOnClickListener { selectFirstOffer() }
            offer2Pw2.setOnClickListener { selectSecondOffer() }
            offer3Pw2.setOnClickListener { selectThirdOffer() }
            termsOfUsePw2.setOnClickListener { openTermsOfUse() }
            policyPw2.setOnClickListener { openPrivacyPolicy() }
            restorePw2.setOnClickListener { openRestore() }
            closePw2.setOnClickListener { close() }
            paywall1PromoPw2.setOnClickListener { promocodeClicked() }
            startBtnPw2.setOnClickListener { launchBilling() }

            when (App.adaptyPaywallModel!!.customPayload?.get("autoselect")) {
                "1" -> selectFirstOffer()
                "2" -> selectSecondOffer()
                "3" -> selectThirdOffer()
                else -> selectSecondOffer()
            }
        }
    }

    private fun setupThirdPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL3

        binding.paywall3.isVisible = true
        with(binding.paywall3) {
            titlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            subtitlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1DurationPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2DurationPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3DurationPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1PricePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2PricePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3PricePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TextPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TextPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3TextPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TitlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3TitlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            termsOfUsePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            policyPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            restorePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline1Pw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline2Pw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline3Pw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1PromoPw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePw3.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer2TitlePw3.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer3TitlePw3.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            bottomGradientPw3.isVisible = App.preferences.isDarkTheme
            closePw3.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1Pw3.setOnClickListener { selectFirstOffer() }
            offer2Pw3.setOnClickListener { selectSecondOffer() }
            offer3Pw3.setOnClickListener { selectThirdOffer() }
            termsOfUsePw3.setOnClickListener { openTermsOfUse() }
            policyPw3.setOnClickListener { openPrivacyPolicy() }
            restorePw3.setOnClickListener { openRestore() }
            closePw3.setOnClickListener { close() }
            paywall1PromoPw3.setOnClickListener { promocodeClicked() }
            startBtnPw3.setOnClickListener { launchBilling() }

            subtitlePw3.text = getString(R.string.paywall_3_subtitle)

            val reviewsAdapter = Pw3ReviewsAdapter()
            val snapHelper = PagerSnapHelper()

            recycler.adapter = reviewsAdapter
            snapHelper.attachToRecyclerView(recycler)
            reviewsAdapter.createList()

            updatePw3IndicatorsState(0)
            recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    recyclerView.layoutManager?.apply {
                        this as LinearLayoutManager
                        val firstVisibleItemPosition: Int = findFirstVisibleItemPosition()
                        updatePw3IndicatorsState(firstVisibleItemPosition)
                    }
                }
            })

            val random = Random()
            val timer = Timer()

            kotlin.runCatching { timer.purge() }
            timer.schedule(Pw3ReviewsChangeTimer(timer, random), 5000)

            when (App.adaptyPaywallModel!!.customPayload?.get("autoselect")) {
                "1" -> selectFirstOffer()
                "2" -> selectSecondOffer()
                "3" -> selectThirdOffer()
                else -> selectSecondOffer()
            }
        }
    }

    private fun selectFirstOffer() {
        selectedOffer = 1

        when (selectedPaywall) {
            PAYWALL_TYPE.PAYWALL1 -> {
                with(binding.paywall1) {
                    offer1.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_paywall_1_offer_1_active
                    )

                    offer2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                    offer3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )

                    if (!App.preferences.isDarkTheme) {
                        offer3Title.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.darkColor
                            )
                        )

                        offer2Title.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.darkColor
                            )
                        )
                    }
                }
            }
            PAYWALL_TYPE.PAYWALL2 -> {
                with(binding.paywall2) {
                    offer1Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )

                    offer2Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer3Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )
                }
            }
            PAYWALL_TYPE.PAYWALL3 -> {
                with(binding.paywall3) {
                    offer1Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )

                    offer2Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer3Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )
                }
            }
        }
    }

    private fun selectSecondOffer() {
        selectedOffer = 2

        when (selectedPaywall) {
            PAYWALL_TYPE.PAYWALL1 -> {
                with(binding.paywall1) {
                    offer1.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_1_inactive
                        else R.drawable.bg_paywall_1_offer_1_inactive_light
                    )

                    offer2.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_paywall_1_offer_2_active
                    )

                    offer3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )


                    if (!App.preferences.isDarkTheme) {
                        offer2Title.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.lightColor
                            )
                        )

                        offer3Title.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.darkColor
                            )
                        )
                    }
                }
            }
            PAYWALL_TYPE.PAYWALL2 -> {
                with(binding.paywall2) {
                    offer1Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer2Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )

                    offer3Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )
                }
            }
            PAYWALL_TYPE.PAYWALL3 -> {
                with(binding.paywall3) {
                    offer1Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer2Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )

                    offer3Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )
                }
            }
        }
    }

    private fun selectThirdOffer() {
        selectedOffer = 3

        when (selectedPaywall) {
            PAYWALL_TYPE.PAYWALL1 -> {
                with(binding.paywall1) {
                    offer1.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_1_inactive
                        else R.drawable.bg_paywall_1_offer_1_inactive_light
                    )

                    offer2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )

                    offer3.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_paywall_1_offer_2_active
                    )

                    if (!App.preferences.isDarkTheme) {
                        offer3Title.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.lightColor
                            )
                        )

                        offer2Title.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.darkColor
                            )
                        )
                    }
                }
            }
            PAYWALL_TYPE.PAYWALL2 -> {
                with(binding.paywall2) {
                    offer1Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer2Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer3Pw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )
                }
            }
            PAYWALL_TYPE.PAYWALL3 -> {
                with(binding.paywall3) {
                    offer1Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer2Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer3Pw3.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )
                }
            }
        }
    }

    private fun updatePw3IndicatorsState(activeIndicatorPosition: Int) {
        with(binding.paywall3) {
            indicator1.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
                else R.drawable.bg_inactive_indicator_light
            )
            indicator2.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
                else R.drawable.bg_inactive_indicator_light
            )
            indicator3.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
                else R.drawable.bg_inactive_indicator_light
            )
            indicator4.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
                else R.drawable.bg_inactive_indicator_light
            )
            indicator5.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
                else R.drawable.bg_inactive_indicator_light
            )


            val activeIndicator = when (activeIndicatorPosition) {
                0 -> indicator1
                1 -> indicator2
                2 -> indicator3
                3 -> indicator4
                else -> indicator5
            }
            activeIndicator.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
                else R.drawable.bg_active_indicator_light
            )
        }
    }

    private fun openTermsOfUse() {
        val url = "http://humdesign.tilda.ws/terms"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun openPrivacyPolicy() {
        val url = "http://humdesign.tilda.ws/policy"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun promocodeClicked() {
        with(binding.promoBottomSheet) {
            this.ok.setOnClickListener {
                if (this.promoET.text.toString() == App.PROMOCODE) {
                    App.preferences.isPremiun = true
                    promoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    close()
                } else snackbarPromo.show()
            }
            promoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    private fun openRestore() {
        restorePurchase()
    }

    private val snackbarPromo: Snackbar by lazy {
        val snackView = View.inflate(requireContext(), R.layout.view_snackbar, null)
        val snackbar = Snackbar.make(binding.paywall1.snackbarContainer, "", Snackbar.LENGTH_LONG)
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        val view = snackbar.view
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params

        (snackbar.view as ViewGroup).removeAllViews()
        (snackbar.view as ViewGroup).addView(snackView)

        snackView.findViewById<TextView>(R.id.title).text =
            App.resourcesProvider.getStringLocale(R.string.snackbar_title)
        snackView.findViewById<TextView>(R.id.desc).text =
            App.resourcesProvider.getStringLocale(R.string.promocode_error)
        snackbar.setBackgroundTint(Color.parseColor("#F7C52B"))

        snackbar
    }

    fun nextLong(rng: Random, n: Long): Long {
        var bits: Long
        var `val`: Long
        do {
            bits = rng.nextLong() shl 1 ushr 1
            `val` = bits % n
        } while (bits - `val` + (n - 1) < 0L)
        return `val`
    }

    inner class Pw3ReviewsChangeTimer(private val timer: Timer, private val random: Random) :
        TimerTask() {
        override fun run() {
            binding.paywall3.recycler.apply {
                val totalItemCount: Int = layoutManager!!.itemCount
                this.layoutManager as LinearLayoutManager


                val lastVisibleItemPosition: Int =
                    (this.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

                isNestedScrollingEnabled = false

//                if (!isNewsUserClicked)
                    post {
                        if (isAdded)
                            requireActivity().runOnUiThread {
                                if (totalItemCount > lastVisibleItemPosition + 1)
                                    smoothScrollToPosition(lastVisibleItemPosition + 1)
                                else smoothScrollToPosition(0)
                            }
                    }
            }

            timer.schedule(Pw3ReviewsChangeTimer(timer, random), 5000)
        }
    }

    private fun close() {
        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(true))

        router.exit()
//        exit()
    }

    fun exit(): Single<Boolean> {
        var exitAnimResult: SingleSubject<Boolean>? = SingleSubject.create<Boolean>()

//        val views = getViewsToAnimate().filterNotNull() //Crashlytics will disagree with you...

        AdditiveAnimator()
            .setDuration(500)
            .targets(requireView()).scaleX(0f).scaleY(0f)
            .setInterpolator(AccelerateInterpolator(2f))
            .addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    exitAnimResult?.onSuccess(true)
                    exitAnimResult = null
//                    router.exit()
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }
            })
            .start()
        return exitAnimResult!!
    }

    inner class Handler {
        fun onBlurClicked(v: View) {
            if (promoBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                promoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }
}

enum class PAYWALL_TYPE {
    PAYWALL1,
    PAYWALL2,
    PAYWALL3
}