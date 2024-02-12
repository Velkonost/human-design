package com.myhumandesignhd.ui.paywall

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import com.adapty.Adapty
import com.adapty.models.AdaptyProductDiscountPhase
import com.adapty.utils.AdaptyResult
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentPaywallBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.loader.LoaderViewModel
import com.myhumandesignhd.vm.BaseViewModel
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Title
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Title
import kotlinx.android.synthetic.main.view_paywall_1.view.snackbarContainer
import kotlinx.android.synthetic.main.view_paywall_2.view.bottomGradientPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.breakline1Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.breakline2Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.breakline3Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.closePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.endingPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.icLogoPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer1DurationPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer1PricePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer1Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer1TextPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer1TitlePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer2DurationPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer2PricePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer2Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer2TextPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer2TitlePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer3DurationPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer3PricePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer3Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer3TextPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.offer3TitlePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.paywall1PromoPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.policyPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.restorePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.startBtnPw2
import kotlinx.android.synthetic.main.view_paywall_2.view.startBtnText
import kotlinx.android.synthetic.main.view_paywall_2.view.termsOfUsePw2
import kotlinx.android.synthetic.main.view_paywall_2.view.text1Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.text2Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.text3Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.text4Pw2
import kotlinx.android.synthetic.main.view_paywall_2.view.title
import kotlinx.android.synthetic.main.view_paywall_22.bottomGradientPw22
import kotlinx.android.synthetic.main.view_paywall_22.breakline1Pw22
import kotlinx.android.synthetic.main.view_paywall_22.breakline2Pw22
import kotlinx.android.synthetic.main.view_paywall_22.breakline3Pw22
import kotlinx.android.synthetic.main.view_paywall_22.closePw22
import kotlinx.android.synthetic.main.view_paywall_22.icLogoPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer1DurationPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer1PricePw22
import kotlinx.android.synthetic.main.view_paywall_22.offer1Pw22
import kotlinx.android.synthetic.main.view_paywall_22.offer1TextPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer1TitlePw22
import kotlinx.android.synthetic.main.view_paywall_22.offer2DurationPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer2PricePw22
import kotlinx.android.synthetic.main.view_paywall_22.offer2Pw22
import kotlinx.android.synthetic.main.view_paywall_22.offer2TextPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer2TitlePw22
import kotlinx.android.synthetic.main.view_paywall_22.offer3DurationPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer3PricePw22
import kotlinx.android.synthetic.main.view_paywall_22.offer3Pw22
import kotlinx.android.synthetic.main.view_paywall_22.offer3TextPw22
import kotlinx.android.synthetic.main.view_paywall_22.offer3TitlePw22
import kotlinx.android.synthetic.main.view_paywall_22.paywall1PromoPw22
import kotlinx.android.synthetic.main.view_paywall_22.policyPw22
import kotlinx.android.synthetic.main.view_paywall_22.restorePw22
import kotlinx.android.synthetic.main.view_paywall_22.startBtnPw22
import kotlinx.android.synthetic.main.view_paywall_22.termsOfUsePw22
import kotlinx.android.synthetic.main.view_paywall_22.text1Pw22
import kotlinx.android.synthetic.main.view_paywall_22.text2Pw22
import kotlinx.android.synthetic.main.view_paywall_22.text3Pw22
import kotlinx.android.synthetic.main.view_paywall_22.text4Pw22
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator1
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator2
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator3
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator4
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator5
import kotlinx.android.synthetic.main.view_paywall_3.view.offer1Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer2Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer3Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.recycler
import kotlinx.android.synthetic.main.view_paywall_4.view.bigCirclePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.bottomGradientPw4
import kotlinx.android.synthetic.main.view_paywall_4.view.closePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.endingPw4
import kotlinx.android.synthetic.main.view_paywall_4.view.icPw4
import kotlinx.android.synthetic.main.view_paywall_4.view.linePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.midCirclePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.offerPrevPricePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.offerPw4
import kotlinx.android.synthetic.main.view_paywall_4.view.offerThenPw4
import kotlinx.android.synthetic.main.view_paywall_4.view.offerTitlePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.offerUptitlePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.planSubtitlePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.planTitlePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.renewablePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.startBtnPw4
import kotlinx.android.synthetic.main.view_paywall_4.view.subtitlePw4
import kotlinx.android.synthetic.main.view_paywall_4.view.titlePw4
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.Random
import java.util.Timer
import java.util.TimerTask


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

    private val fromStart: Boolean by lazy {
        arguments?.getBoolean("fromStart") ?: false
    }

    private val source: String by lazy {
        arguments?.getString("source") ?: ""
    }

    private val promoBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.promoBottomSheet.bottomSheetContainer)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        App.preferences.amountPaywallsShown += 1

        Amplitude.getInstance().logEvent(
            "subscription_shown",
            JSONObject(
                mutableMapOf(
                    "source" to "from $source",
                    "type" to if (App.preferences.amountPaywallsShown == 3) "special" else "basic"
                ).toMap()
            )
        )

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


        if (App.adaptyPaywallModel != null)
            Adapty.logShowPaywall(App.adaptyPaywallModel!!)

        setupSecondPaywall()
//        when (App.adaptySplitPwName) {
//            "with_scroll" -> setupSecondPaywall()
//            else -> setupSecond2Paywall()
//        }
    }

    private fun launchBilling() {
        val billingPeriod =
            when (selectedPaywall) {
                PAYWALL_TYPE.PAYWALL1 -> {
                    when (selectedOffer) {
                        1 -> "P1M"
                        2 -> "P1W"
                        else -> "P1Y"
                    }
                }

                PAYWALL_TYPE.SPECIAL -> {
                    "P1Y"
                }

                else -> {
                    when (selectedOffer) {
                        2 -> "P1M"
                        1 -> "P1W"
                        else -> "P1Y"
                    }
                }
            }

        var vendorProductId = when (billingPeriod) {
            "P1M" -> "hd_month_sub"
            "P1W" -> "hd_week_sub"
            "P1Y" -> "hd_year_sub"
            else -> "hd_month_sub"
        }

        if (selectedPaywall == PAYWALL_TYPE.SPECIAL)
            vendorProductId = "hd_year_sub_3d_trial"

        if (!App.adaptyProducts.isNullOrEmpty()) {
            Adapty.makePurchase(
                requireActivity(),
                App.adaptyProducts!!.first { productModel ->
                    productModel.vendorProductId == vendorProductId
                }
            ) { result ->
                when (result) {
                    is AdaptyResult.Success -> {
                        val profile = result.value
                        val firebaseAnalytics = Firebase.analytics

                        if (
//                            result.value?.subscriptions?.values?.firstOrNull { it.isActive }?.activePromotionalOfferId == "trial"
//                            App.adaptyProducts!!.first { productModel ->
//                                productModel.vendorProductId == vendorProductId
//                            }.freeTrialPeriod != null
                            App.adaptyProducts!!.first { productModel ->
                                productModel.vendorProductId == vendorProductId
                            }.subscriptionDetails?.introductoryOfferPhases?.any {
                                it.paymentMode == AdaptyProductDiscountPhase.PaymentMode.FREE_TRIAL
                            } == true
                        ) {
                            if (!App.preferences.firstReviewUsed) {
                                App.preferences.showAskReview = true
//                                App.preferences.firstReviewUsed = true
                            }

                            firebaseAnalytics.logEvent("start_trial", null)
                        } else {

                            if (!App.preferences.firstReviewUsed && vendorProductId == "hd_month_sub") {
                                App.preferences.showAskReview = true
//                                App.preferences.firstReviewUsed = true
                            }

                            firebaseAnalytics.logEvent("subscription", null)
                            firebaseAnalytics.logEvent(vendorProductId, null)
                        }

                        if (profile?.accessLevels?.get("premium")?.isActive == true) {
                            Amplitude.getInstance().logEvent(
                                "subscription_purchased",
                                JSONObject(
                                    mutableMapOf(
                                        "type" to when (billingPeriod) {
                                            "P1M" -> "Month"
                                            "P1W" -> "Week"
                                            "P1Y" -> "Year"
                                            else -> "Month"
                                        }
                                    ).toMap()
                                )
                            )
                            App.preferences.isPremiun = true

                            val identify = Identify()
                            identify.set("Purchased", "yes")
                            Amplitude.getInstance().identify(identify)

                            close()
                        }
                    }

                    is AdaptyResult.Error -> {
                        val error = result.error
                        // handle the error
                    }
                }
            }

//            Adapty.makePurchase(
//                requireActivity(),
//                if (App.preferences.locale == "ru" || selectedPaywall == PAYWALL_TYPE.SPECIAL) {
//                    App.adaptyProducts!!.first { productModel ->
//                        productModel.vendorProductId == vendorProductId
//                    }
//                } else {
//                    App.adaptyPaywallModel!!.products.first { productModel ->
//                        productModel.vendorProductId == vendorProductId
//                    }
//                }
//            ) { purchaserInfo, purchaseToken, googleValidationResult, product, error ->
//                if (error == null) {
//                    val firebaseAnalytics = Firebase.analytics
//
//                    if (product.freeTrialPeriod != null) {
//                        firebaseAnalytics.logEvent("start_trial", null)
//                        product.freeTrialPeriod
//                    } else {
//                        firebaseAnalytics.logEvent("subscription", null)
//                        firebaseAnalytics.logEvent(vendorProductId, null)
//                    }
//
//                    if (purchaserInfo?.accessLevels?.get("premium")?.isActive == true) {
//                        Amplitude.getInstance().logEvent(
//                            "subscription_purchased",
//                            JSONObject(mutableMapOf(
//                                "type" to when(billingPeriod) {
//                                    "P1M" -> "Month"
//                                    "P1W" -> "Week"
//                                    "P1Y" -> "Year"
//                                    else -> "Month"
//                                }
//                            ).toMap())
//                        );
//                        App.preferences.isPremiun = true
//
//                        val identify = Identify()
//                        identify.set("Purchased", "yes")
//                        Amplitude.getInstance().identify(identify)
//
//                        close()
//                    }
//                }
//            }
        }
    }

    private fun restorePurchase() {
        Adapty.restorePurchases { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    val profile = result.value

                    if (profile.accessLevels["premium"]?.isActive == true) {
                        val identify = Identify()
                        identify.set("Purchased", "yes")
                        Amplitude.getInstance().identify(identify)

                        App.preferences.isPremiun = true
                        close()
                    }
                }

                is AdaptyResult.Error -> {
                    val error = result.error
                }
            }
        }

//        Adapty.restorePurchases { purchaserInfo, googleValidationResultList, error ->
//            if (error == null) {
//                // successful restore
//                if (purchaserInfo?.accessLevels?.get("premium")?.isActive == true) {
//                    val identify = Identify()
//                    identify.set("Purchased", "yes")
//                    Amplitude.getInstance().identify(identify)
//
//                    App.preferences.isPremiun = true
//                    close()
//                }
//            }
//        }
    }

    private fun setupSecondPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL2

        binding.paywall2.isVisible = true
        with(binding.paywall2) {
            if (fromStart) {
                title.text =
                    "${App.preferences.userNameFromStart?.strip()},\n" + App.resourcesProvider.getStringLocale(
                        R.string.enjoy_hd
                    )
            }

            endingPw2.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

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

            offer1TitlePw2.background = ContextCompat.getDrawable(
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

//            if (
//                App.adaptyPaywallModel != null
//                && App.adaptyPaywallModel!!.customPayload != null
//                && App.adaptyPaywallModel!!.customPayload?.keys?.contains("autoselect") == true
//            ) {
//                when (App.adaptyPaywallModel!!.customPayload?.get("autoselect")) {
//                    "1" -> selectFirstOffer()
//                    "2" -> selectSecondOffer()
//                    "3" -> selectThirdOffer()
//                    else -> selectSecondOffer()
//                }
//            } else selectSecondOffer()
            selectThirdOffer()
        }
    }

    private fun setupSecond2Paywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL22

        binding.paywall22.isVisible = true
        with(binding.paywall22) {
            if (fromStart) {
                title.text =
                    "${App.preferences.userNameFromStart?.strip()},\n" + App.resourcesProvider.getStringLocale(
                        R.string.enjoy_hd
                    )
            }

            title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text1Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text2Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text3Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            text4Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            icLogoPw22.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_paywall_2_logo_dark
                else R.drawable.ic_paywall_2_logo_light
            )

            offer1DurationPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2DurationPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3DurationPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1PricePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2PricePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3PricePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TextPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TextPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3TextPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TitlePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer3TitlePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            termsOfUsePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            policyPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            restorePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline1Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline2Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline3Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1PromoPw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePw22.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer2TitlePw22.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer3TitlePw22.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            offer1TitlePw22.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                else R.drawable.bg_paywall_2_offer_title_light
            )

            bottomGradientPw22.isVisible = App.preferences.isDarkTheme
            closePw22.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1Pw22.setOnClickListener { selectFirstOffer() }
            offer2Pw22.setOnClickListener { selectSecondOffer() }
            offer3Pw22.setOnClickListener { selectThirdOffer() }
            termsOfUsePw22.setOnClickListener { openTermsOfUse() }
            policyPw22.setOnClickListener { openPrivacyPolicy() }
            restorePw22.setOnClickListener { openRestore() }
            closePw22.setOnClickListener { close() }
            paywall1PromoPw22.setOnClickListener { promocodeClicked() }
            startBtnPw22.setOnClickListener { launchBilling() }

//            if (
//                App.adaptyPaywallModel != null
//                && App.adaptyPaywallModel!!.customPayload != null
//                && App.adaptyPaywallModel!!.customPayload?.keys?.contains("autoselect") == true
//            ) {
//                when (App.adaptyPaywallModel!!.customPayload?.get("autoselect")) {
//                    "1" -> selectFirstOffer()
//                    "2" -> selectSecondOffer()
//                    "3" -> selectThirdOffer()
//                    else -> selectSecondOffer()
//                }
//            } else selectSecondOffer()
            selectThirdOffer()
        }
    }

    private fun setupSpecialPaywall() {
        selectedPaywall = PAYWALL_TYPE.SPECIAL

        binding.paywall4.isVisible = true
        with(binding.paywall4) {
            bottomGradientPw4.isVisible = App.preferences.isDarkTheme

            subtitlePw4.text =
                App.resourcesProvider.getStringLocale(R.string.it_s_a_lucky_day_for_manifestor) + " " +
                        if (App.preferences.locale == "ru") {
                            when (baseViewModel.currentUser.subtitle1Ru) {
                                "Манифестор" -> "Манифестора"
                                "Манифестирующий Генератор" -> "Манифестирующего Генератора"
                                "Генератор" -> "Генератора"
                                "Проектор" -> "Проектора"
                                "Рефлектор" -> "Рефлектора"
                                else -> "Манифестора"
                            }
                        } else {
                            baseViewModel.currentUser.subtitle1En
                        } + "!"

            bigCirclePw4.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_circle_big_dark
                else R.drawable.ic_circle_big_light
            )

            midCirclePw4.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_circle_mid_dark
                else R.drawable.ic_circle_mid_light
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

            bigCirclePw4.startAnimation(rotate)
            midCirclePw4.startAnimation(rotateNegative)

            icPw4.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_pw_special_dark
                else R.drawable.ic_pw_special_light
            )

            titlePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            subtitlePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            planTitlePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            planSubtitlePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            endingPw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offerPw4.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                else R.drawable.bg_paywall_2_offer_active_light
            )

            offerUptitlePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.lightColor
                )
            )
            offerUptitlePw4.background = ContextCompat.getDrawable(
                requireContext(), R.drawable.bg_pw_offer_title_selected
            )

            offerTitlePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            renewablePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offerThenPw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offerPrevPricePw4.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            linePw4.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.bg_line_dark
                else R.drawable.bg_line_light
            )

            closePw4.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            startBtnPw4.setOnClickListener { launchBilling() }
            closePw4.setOnClickListener { close() }
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
                    startBtnText.text =
                        App.resourcesProvider.getStringLocale(R.string.paywall_btn_trial)

                    endingPw2.text = App.resourcesProvider.getStringLocale(R.string.pw_ending_week)

                    offer1TitlePw2.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.lightColor
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

                    offer1TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
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

            PAYWALL_TYPE.PAYWALL22 -> {
                with(binding.paywall22) {
                    startBtnText.text =
                        App.resourcesProvider.getStringLocale(R.string.paywall_btn_trial)

                    offer1TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.lightColor
                        )
                    )

                    offer2TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer3TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer1TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
                    )

                    offer2TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer3TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer1Pw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )

                    offer2Pw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer3Pw22.background = ContextCompat.getDrawable(
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
                    startBtnText.text = App.resourcesProvider.getStringLocale(R.string.paywall_btn)
                    endingPw2.text = App.resourcesProvider.getStringLocale(R.string.pw_ending_month)
                    offer2TitlePw2.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.lightColor
                        )
                    )

                    offer1TitlePw2.setTextColor(
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

                    offer2TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
                    )

                    offer1TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer3TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )


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

            PAYWALL_TYPE.PAYWALL22 -> {
                with(binding.paywall22) {
                    startBtnText.text = App.resourcesProvider.getStringLocale(R.string.paywall_btn)

                    offer2TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.lightColor
                        )
                    )

                    offer1TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer3TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer2TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
                    )

                    offer1TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer3TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )


                    offer1Pw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer2Pw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_active_dark
                        else R.drawable.bg_paywall_2_offer_active_light
                    )

                    offer3Pw22.background = ContextCompat.getDrawable(
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
                    startBtnText.text = App.resourcesProvider.getStringLocale(R.string.paywall_btn)
                    endingPw2.text = App.resourcesProvider.getStringLocale(R.string.pw_ending_year)

                    offer3TitlePw2.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.lightColor
                        )
                    )

                    offer2TitlePw2.setTextColor(
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

                    offer3TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
                    )

                    offer2TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer1TitlePw2.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )


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

            PAYWALL_TYPE.PAYWALL22 -> {
                with(binding.paywall22) {
                    startBtnText.text = App.resourcesProvider.getStringLocale(R.string.paywall_btn)

                    offer3TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.lightColor
                        )
                    )

                    offer2TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer1TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer3TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
                    )

                    offer2TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer1TitlePw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer1Pw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer2Pw22.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_inactive_dark
                        else R.drawable.bg_paywall_2_offer_inactive_light
                    )

                    offer3Pw22.background = ContextCompat.getDrawable(
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
        val url = "https://humdesign.info/terms-of-use.php"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun openPrivacyPolicy() {
        val url = "https://humdesign.info/policy.php"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun promocodeClicked() {
        with(binding.promoBottomSheet) {
            this.ok.setOnClickListener {
                if (
                    App.PROMOCODES.contains(this.promoET.text.toString().trim())
//                    this.promoET.text.toString() == App.PROMOCODE
                ) {
                    val identify = Identify()
                    identify.set("Purchased", "yes")
                    Amplitude.getInstance().identify(identify)

                    App.preferences.isPremiun = true
                    hideKeyboard(this.promoET)
                    promoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

//                    if (!App.preferences.firstReviewUsed) {
                        App.preferences.showAskReview = true
//                                App.preferences.firstReviewUsed = true
//                    }

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
        if (!isAdded) return

        runCatching {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(true))
            Amplitude.getInstance().logEvent("subscription_exit_clicked")

            router.replaceScreen(Screens.bodygraphScreen(true))
        }
//        if (fromStart)
//            router.replaceScreen(Screens.bodygraphScreen(true))
//        else router.exit()
    }


    fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun exit(): Single<Boolean> {
        var exitAnimResult: SingleSubject<Boolean>? = SingleSubject.create<Boolean>()

//        val views = getViewsToAnimate().filterNotNull() //Crashlytics will disagree with you...

        AdditiveAnimator()
            .setDuration(500)
            .targets(requireView()).scaleX(0f).scaleY(0f)
            .setInterpolator(AccelerateInterpolator(2f))
            .addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    exitAnimResult?.onSuccess(true)
                    exitAnimResult = null
//                    router.exit()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            .start()
        return exitAnimResult!!
    }

    inner class Handler {
        fun onBlurClicked(v: View) {
            if (promoBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                hideKeyboard(binding.promoBottomSheet.promoET)
                promoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

    }
}

enum class PAYWALL_TYPE {
    PAYWALL1,
    PAYWALL2,
    PAYWALL22,
    PAYWALL3,
    SPECIAL
}