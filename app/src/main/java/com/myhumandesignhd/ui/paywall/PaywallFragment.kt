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
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import com.adapty.Adapty
import com.adapty.models.AdaptyProductDiscountPhase
import com.adapty.utils.AdaptyResult
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.facebook.appevents.AppEventsLogger
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
import com.myhumandesignhd.ui.paywall.PaywallFragment.OFFERS.Companion.getEnding
import com.myhumandesignhd.ui.paywall.PaywallFragment.OFFERS.Companion.getPerWeek
import com.myhumandesignhd.ui.paywall.adapter.Pw3ReviewsAdapter
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.vm.BaseViewModel
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.view_paywall_1.view.bodygraphView
import kotlinx.android.synthetic.main.view_paywall_1.view.bottomGradient
import kotlinx.android.synthetic.main.view_paywall_1.view.breakline1
import kotlinx.android.synthetic.main.view_paywall_1.view.breakline2
import kotlinx.android.synthetic.main.view_paywall_1.view.breakline3
import kotlinx.android.synthetic.main.view_paywall_1.view.icBigCircle
import kotlinx.android.synthetic.main.view_paywall_1.view.icMidCircle
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1Duration
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1Price
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1Text
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Duration
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Price
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Text
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Title
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Duration
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Price
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Text
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Title
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Close
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Policy
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Promo
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Restore
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1TermsOfUse
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Title
import kotlinx.android.synthetic.main.view_paywall_1.view.snackbarContainer
import kotlinx.android.synthetic.main.view_paywall_1.view.startBtn
import kotlinx.android.synthetic.main.view_paywall_1.view.text1
import kotlinx.android.synthetic.main.view_paywall_1.view.text2
import kotlinx.android.synthetic.main.view_paywall_1.view.text3
import kotlinx.android.synthetic.main.view_paywall_1.view.text4
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
import kotlinx.android.synthetic.main.view_paywall_3.view.bottomGradientPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.breakline1Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.breakline2Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.breakline3Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.closePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator1
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator2
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator3
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator4
import kotlinx.android.synthetic.main.view_paywall_3.view.indicator5
import kotlinx.android.synthetic.main.view_paywall_3.view.offer1DurationPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer1PricePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer1Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer1TextPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer1TitlePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer2DurationPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer2PricePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer2Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer2TextPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer2TitlePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer3DurationPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer3PricePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer3Pw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer3TextPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.offer3TitlePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.paywall1PromoPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.policyPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.recycler
import kotlinx.android.synthetic.main.view_paywall_3.view.restorePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.startBtnPw3
import kotlinx.android.synthetic.main.view_paywall_3.view.subtitlePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.termsOfUsePw3
import kotlinx.android.synthetic.main.view_paywall_3.view.titlePw3
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
import kotlinx.android.synthetic.main.view_paywall_push.bodygraphViewPush
import kotlinx.android.synthetic.main.view_paywall_push.bottomGradientPush
import kotlinx.android.synthetic.main.view_paywall_push.breakline1Push
import kotlinx.android.synthetic.main.view_paywall_push.breakline2Push
import kotlinx.android.synthetic.main.view_paywall_push.breakline3Push
import kotlinx.android.synthetic.main.view_paywall_push.icBigCirclePush
import kotlinx.android.synthetic.main.view_paywall_push.icMidCirclePush
import kotlinx.android.synthetic.main.view_paywall_push.offer1DurationPush
import kotlinx.android.synthetic.main.view_paywall_push.offer1PricePush
import kotlinx.android.synthetic.main.view_paywall_push.offer1Push
import kotlinx.android.synthetic.main.view_paywall_push.offer1TextPush
import kotlinx.android.synthetic.main.view_paywall_push.offer1TitlePush
import kotlinx.android.synthetic.main.view_paywall_push.offer2DurationPush
import kotlinx.android.synthetic.main.view_paywall_push.offer2PricePush
import kotlinx.android.synthetic.main.view_paywall_push.offer2Push
import kotlinx.android.synthetic.main.view_paywall_push.offer2TextPush
import kotlinx.android.synthetic.main.view_paywall_push.offer2TitlePush
import kotlinx.android.synthetic.main.view_paywall_push.offer3DurationPush
import kotlinx.android.synthetic.main.view_paywall_push.offer3MainBlockPush
import kotlinx.android.synthetic.main.view_paywall_push.offer3PricePush
import kotlinx.android.synthetic.main.view_paywall_push.offer3Push
import kotlinx.android.synthetic.main.view_paywall_push.offer3TextPush
import kotlinx.android.synthetic.main.view_paywall_push.offer3TitlePush
import kotlinx.android.synthetic.main.view_paywall_push.paywall1ClosePush
import kotlinx.android.synthetic.main.view_paywall_push.paywall1PolicyPush
import kotlinx.android.synthetic.main.view_paywall_push.paywall1PromoPush
import kotlinx.android.synthetic.main.view_paywall_push.paywall1RestorePush
import kotlinx.android.synthetic.main.view_paywall_push.paywall1TermsOfUsePush
import kotlinx.android.synthetic.main.view_paywall_push.paywallPushTitle
import kotlinx.android.synthetic.main.view_paywall_push.startBtnPush
import kotlinx.android.synthetic.main.view_paywall_push.text1Push
import kotlinx.android.synthetic.main.view_paywall_push.text2Push
import kotlinx.android.synthetic.main.view_paywall_push.text3Push
import kotlinx.android.synthetic.main.view_paywall_push.text4Push
import kotlinx.android.synthetic.main.view_paywall_push.view.offer2MainBlockPush
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.math.BigDecimal
import java.util.Currency
import java.util.Locale
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

        if (source == "popupPush") {
            Amplitude.getInstance().logEvent("bodygraph_unlock_paywall_shown")
        }

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

//        if (App.preferences.lastPaywall % 3 == 0) {
//            setupFirstPaywall()
//        } else if (App.preferences.lastPaywall % 3 == 1) {
//            setupSecond2Paywall()
//        } else {
//            setupThirdPaywall()
//        }

        App.preferences.lastPaywall += 1

        if (source == "popupPush") {
            isTrialEnabled = false
            setupPushPaywall()
        } else {
            when (App.adaptySplitPwName) {
                "openfull_horizontal" -> setupFirstPaywall()
                "start_vertical" -> setupSecond2Paywall()
                "reviews_slider_vertical" -> setupThirdPaywall()
                "second_paywall_no_trial" -> {
                    isTrialEnabled = false
                    setupSecondPaywall()
                }

                else -> setupSecondPaywall()
            }
        }
//        setupPushPaywall()
    }

    private var isTrialEnabled = false

    enum class OFFERS(
        val title: String,
        val priceEn: String, val priceRu: String,
        val offerType: OFFER_TYPE,
        val endingRu: String,
        val endingEn: String,
        val perWeekEn: String,
        val perWeekRu: String
    ) {
        WEEK_899_NO_TRIAL(
            "hd_week_899_no_trial","$8.99", "₽820", OFFER_TYPE.WEEK,
            endingEn = "The subscription automatically renews at \$8.99 per week unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽820 в неделю, если не будет отменена хотя бы за 24 часа до окончания оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "$8.99 per week",
            perWeekRu = "₽820 в неделю"
        ),
        WEEK_899(
            "hd_week_899","$8.99", "₽820", OFFER_TYPE.WEEK,
            endingEn = "The 3 days trial version will automatically convert to a paid subscription at \$8.99 per week unless canceled at least 24 hours before the end of the trial period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Пробная версия на 3 дня, если не будет отменена хотя бы за 24 часа до окончания пробного периода, автоматически переходит в платную подписку за ₽820 в неделю. Вы можете изменить или отменить подписку в любое время в подписках Google Play.",
            perWeekEn = "3 days free, then $8.99 per week",
            perWeekRu = "3 дня бесплатно, затем ₽820 в неделю"
        ),
        WEEK_549_NO_TRIAL(
            "hd_week_599_no_trial","$5.49", "₽500", OFFER_TYPE.WEEK,
            endingEn = "The subscription automatically renews at \$5.49 per week unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽500 в неделю, если не будет отменена хотя бы за 24 часа до окончания оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "$5.49 per week",
            perWeekRu = "₽500 в неделю"
        ),
        YEAR_NO_TRIAL(
            "hd_year_no_trial","$39.99", "₽3660", OFFER_TYPE.YEAR,
            endingEn = "The subscription automatically renews at \$39.99 per year unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽3660 в год, если не будет отменена хотя бы за 24 часа до завершения оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "$0.8 per week",
            perWeekRu = "₽76 в неделю"
        ),
        WEEK(
            "hd_week_sub","$7.49", "₽399", OFFER_TYPE.WEEK,
            endingEn = "The subscription automatically renews at \$7.49 per week unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽399 в неделю, если не будет отменена хотя бы за 24 часа до окончания оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "3 days free, then $7.49 per week",
            perWeekRu = "3 дня бесплатно, затем ₽399 в неделю"
        ),
        WEEK_NO_TRIAL(
            "hd_week_sub_no_trial","$7.49", "₽399", OFFER_TYPE.WEEK,
            endingEn = "The subscription automatically renews at \$7.49 per week unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽399 в неделю, если не будет отменена хотя бы за 24 часа до окончания оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "$7.49 per week",
            perWeekRu = "₽399 в неделю"
        ),
        YEAR(
            "hd_year_sub", "$49.99", "₽2699", OFFER_TYPE.YEAR,
            endingEn = "The subscription automatically renews at \$49.99 per year unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽2699 в год, если не будет отменена хотя бы за 24 часа до завершения оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "$1 per week",
            perWeekRu = "₽56 в неделю"
        ),
        MONTH(
            "hd_month_sub", "$33.99", "₽899", OFFER_TYPE.MONTH,
            endingEn = "The subscription automatically renews at \$33.99 per month unless canceled at least 24 hours before the end of the paid period. You can manage or cancel your membership anytime in Google Play Subscriptions.",
            endingRu = "Подписка автоматически продлевается по цене ₽899 в месяц, если не будет отменена хотя бы за 24 часа до окончания оплаченного периода. Вы можете изменить или отменить свою подписку в любое время в подписках Google Play.",
            perWeekEn = "$8.4 per week",
            perWeekRu = "₽224 в неделю"
        );

        companion object {
            private fun OFFERS.getPrice(): String {
                return if (App.preferences.locale == "ru") priceRu
                else priceEn
            }

            fun getPriceBy(offer: String): Pair<String?, String?> {
                val _offer = OFFERS.values().firstOrNull { it.title == offer }
                return Pair(_offer?.getPrice(), _offer?.getDuration())
            }

            fun getPerWeek(offer: String): String? {
                OFFERS.values().firstOrNull { it.title == offer }?.let {
                    return if (App.preferences.locale == "ru") it.perWeekRu else it.perWeekEn
                }
                return null
            }

            fun OFFERS.getDuration(): String {
                return when(this.offerType) {
                    OFFER_TYPE.WEEK -> App.resourcesProvider.getStringLocale(R.string.paywall_1_week)
                    OFFER_TYPE.MONTH -> App.resourcesProvider.getStringLocale(R.string.paywall_1_month)
                    OFFER_TYPE.YEAR -> App.resourcesProvider.getStringLocale(R.string.paywall_1_year)
                }
            }

            fun String.getEnding(): String? {
                OFFERS.values().firstOrNull { it.title == this }?.let {
                    return if (App.preferences.locale == "ru") it.endingRu
                    else it.endingEn
                }
                return null
            }
        }
    }

    enum class OFFER_TYPE {
        WEEK,
        MONTH,
        YEAR;
    }

    private fun setupFirstPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL1

        binding.paywall1.isVisible = true
        with(binding.paywall1) {

            App.adaptyOffers?.let {
                it["first_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer1Price.text = it }
                            result.second?.let { offer1Duration.text = it }
                        }
                    }
                }
                it["second_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer2Price.text = it }
                            result.second?.let { offer2Duration.text = it }
                        }
                    }
                }
                it["third_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer3Price.text = it }
                            result.second?.let { offer3Duration.text = it }
                        }
                    }
                }
            }

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

            selectSecondOffer()
        }
    }

    private fun setupPushPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL_PUSH

        binding.paywallPush.isVisible = true
        with(binding.paywallPush) {

            paywallPushTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text1Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text2Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text3Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            text4Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer1DurationPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2DurationPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3DurationPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer1PricePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2PricePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3PricePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer1TextPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer2TextPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3TextPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer2TitlePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            offer1TitlePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            offer3TitlePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1TermsOfUsePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1PolicyPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1RestorePush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            paywall1PromoPush.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline1Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline2Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            breakline3Push.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            bottomGradientPush.isVisible = App.preferences.isDarkTheme
            paywall1ClosePush.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )


            offer1Push.setOnClickListener { selectFirstOffer() }
            offer2Push.setOnClickListener { selectSecondOffer() }
            offer3Push.setOnClickListener { selectThirdOffer() }
            paywall1TermsOfUsePush.setOnClickListener { openTermsOfUse() }
            paywall1PolicyPush.setOnClickListener { openPrivacyPolicy() }
            paywall1RestorePush.setOnClickListener { openRestore() }
            paywall1ClosePush.setOnClickListener { close() }
            paywall1PromoPush.setOnClickListener { promocodeClicked() }
            startBtnPush.setOnClickListener { launchBilling() }

            icBigCirclePush.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            icMidCirclePush.imageTintList = ColorStateList.valueOf(
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

            icBigCirclePush.startAnimation(rotate)
            icMidCirclePush.startAnimation(rotateNegative)

            bodygraphViewPush.changeIsAllowDrawTextState(false)
            baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                if (
                    !it.design.channels.isNullOrEmpty()
                    && !it.personality.channels.isNullOrEmpty()
                    && !it.activeCentres.isNullOrEmpty()
                    && !it.inactiveCentres.isNullOrEmpty()
                ) {
                    android.os.Handler().postDelayed({
                        bodygraphViewPush.isVisible = true
                        bodygraphViewPush.scaleXY(1.1f, 1.1f, 1500) {
                            runCatching {
                                bodygraphViewPush.changeSpeedAnimationFactor(3f)
                                bodygraphViewPush.changeIsAllowDrawLinesState(true)
                            }
                        }
                    }, 200)
                }

                bodygraphViewPush.setupData(
                    it.design,
                    it.personality,
                    it.activeCentres,
                    it.inactiveCentres
                )
            }

            selectSecondOffer()
        }
    }

    private fun setupThirdPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL3

        binding.paywall3.isVisible = true
        with(binding.paywall3) {

            App.adaptyOffers?.let {
                it["first_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer1PricePw3.text = it }
                            result.second?.let { offer1DurationPw3.text = it }
                        }
                    }
                }
                it["second_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer2PricePw3.text = it }
                            result.second?.let { offer2DurationPw3.text = it }
                        }
                    }
                }
                it["third_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer3PricePw3.text = it }
                            result.second?.let { offer3DurationPw3.text = it }
                        }
                    }
                }
            }


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
                    else R.color.blackColor
                )
            )

            offer2TitlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
                )
            )

            offer3TitlePw3.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
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

            selectSecondOffer()
        }
    }

    private fun launchBilling() {
        val billingPeriod =

            when (selectedPaywall) {
//                PAYWALL_TYPE.PAYWALL1 -> {
//                    when (selectedOffer) {
//                        1 -> "P1M"
//                        2 -> "P1W"
//                        else -> "P1Y"
//                    }
//                }

                PAYWALL_TYPE.PAYWALL_PUSH -> {
                    when(selectedOffer) {
                        1 -> "P1W"
                        2 -> "P1Y"
                        else -> "P1M"
                    }
                }

//                PAYWALL_TYPE.SPECIAL -> {
//                    "P1Y"
//                }
                else -> {
                    when(selectedOffer) {
                        1 -> App.adaptyOffers?.get("first_offer") as? String ?: "P1M"
                        2 -> App.adaptyOffers?.get("second_offer") as? String ?: "P1W"
                        else -> App.adaptyOffers?.get("third_offer") as? String ?: "P1Y"
                    }
                }

//                else -> {
//                    when (selectedOffer) {
//                        2 -> "P1M"
//                        1 -> "P1W"
//                        else -> "P1Y"
//                    }
//                }
            }

        var vendorProductId = when (billingPeriod) {
            "P1M" -> "hd_month_sub"
            "P1W" -> if (isTrialEnabled) "hd_week_sub" else "hd_week_sub_no_trial"
            "P1Y" -> "hd_year_sub"
            else -> billingPeriod
//            else -> "hd_month_sub"
        }

        if (selectedPaywall == PAYWALL_TYPE.SPECIAL)
            vendorProductId = "hd_year_sub_3d_trial"

        runCatching {
            if (!App.adaptyProducts.isNullOrEmpty()) {
                Adapty.makePurchase(
                    requireActivity(),
                    App.adaptyProducts!!.first { productModel ->
                        productModel.vendorProductId == vendorProductId
                    }
                ) { result ->
                    when (result) {
                        is AdaptyResult.Success -> {
                            val facebookLogger = AppEventsLogger.newLogger(requireContext())

                            facebookLogger.logPurchase(
                                purchaseAmount = App.adaptyProducts!!.firstOrNull { productModel ->
                                    productModel.vendorProductId == vendorProductId
                                }?.price?.amount ?: BigDecimal(10),
                                currency = Currency.getInstance(Locale.getDefault())
                            )

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
                                firebaseAnalytics.logEvent("start_trial", null)
                            } else {
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

//            if (!isTrialEnabled) {
                offer1TitlePw2.isVisible = false
                offer1TextPw2.text = resources.getString(R.string._5_99_per_week_no_trial)
//            }

            App.adaptyOffers?.let {
                it["first_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer1PricePw2.text = it }
                            result.second?.let { offer1DurationPw2.text = it }
                        }
                        getPerWeek(offer)?.let { offer1TextPw2.text = it }

                    }
                }
                it["second_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer2PricePw2.text = it }
                            result.second?.let { offer2DurationPw2.text = it }
                        }
                        getPerWeek(offer)?.let { offer2TextPw2.text = it }
                    }
                }
                it["third_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer3PricePw2.text = it }
                            result.second?.let { offer3DurationPw2.text = it }
                        }
                        getPerWeek(offer)?.let { offer3TextPw2.text = it }
                    }
                }
            }


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

            if (!isTrialEnabled) {
                offer1TitlePw2.isVisible = false
                offer1TextPw2.text = resources.getString(R.string._5_99_per_week_no_trial)
            }

            App.adaptyOffers?.let {
                it["first_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer1PricePw2.text = it }
                            result.second?.let { offer1DurationPw2.text = it }
                        }
                    }
                }
                it["second_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer2PricePw2.text = it }
                            result.second?.let { offer2DurationPw2.text = it }
                        }
                    }
                }
                it["third_offer"]?.let { offer ->
                    if (offer is String) {
                        OFFERS.getPriceBy(offer).let { result ->
                            result.first?.let { offer3PricePw2.text = it }
                            result.second?.let { offer3DurationPw2.text = it }
                        }
                    }
                }
            }


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
                    else R.color.blackColor
                )
            )

            text2Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
                )
            )

            text3Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
                )
            )

            text4Pw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
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
                    else R.color.blackColor
                )
            )

            offer2TitlePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
                )
            )

            offer3TitlePw22.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.blackColor
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

            PAYWALL_TYPE.PAYWALL_PUSH -> {
                with(binding.paywallPush) {
                    offer1Push.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_paywall_push_offer_active
                    )

                    offer1TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.lightColor)
                    )
                    offer2TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )
                    offer3TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )


                    offer1DurationPush.alpha = 1f
                    offer1PricePush.alpha = 1f
                    offer1TextPush.alpha = 1f

                    offer2DurationPush.alpha = 0.5f
                    offer2PricePush.alpha = 0.5f
                    offer2TextPush.alpha = 0.5f

                    offer3DurationPush.alpha = 0.5f
                    offer3PricePush.alpha = 0.5f
                    offer3TextPush.alpha = 0.5f

                    offer1TitlePush.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.bg_pw_offer_title_selected
                    )

                    offer2TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_title_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
//                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer3TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_title_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
//                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer2MainBlockPush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
//                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                    offer3MainBlockPush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
//                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                }
            }

            PAYWALL_TYPE.PAYWALL2 -> {
                with(binding.paywall2) {
//                    if (isTrialEnabled) {
//                        startBtnText.text =
//                            App.resourcesProvider.getStringLocale(R.string.paywall_btn_trial)
//                    }

                    endingPw2.text =
                        App.adaptyOffers?.let {
                            it["first_offer"]?.let { offer ->
                                if (offer is String) {
                                    offer.getEnding() ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_week_no_trial)
                                } else App.resourcesProvider.getStringLocale(R.string.pw_ending_week_no_trial)
                            } ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_week_no_trial)
                        } ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_week_no_trial)

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
                    if (isTrialEnabled) {
                        startBtnText.text =
                            App.resourcesProvider.getStringLocale(R.string.paywall_btn_trial)
                    }

                    offer1TitlePw22.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.lightColor)
                    )

                    offer2TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.blackColor
                        )
                    )

                    offer3TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.blackColor
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

            PAYWALL_TYPE.PAYWALL_PUSH -> {
                with(binding.paywallPush) {
                    offer1Push.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_light
                    )

                    offer2TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.lightColor)
                    )
                    offer1TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )
                    offer3TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer1DurationPush.alpha = 0.5f
                    offer1PricePush.alpha = 0.5f
                    offer1TextPush.alpha = 0.5f

                    offer2DurationPush.alpha = 1f
                    offer2PricePush.alpha = 1f
                    offer2TextPush.alpha = 1f

                    offer3DurationPush.alpha = 0.5f
                    offer3PricePush.alpha = 0.5f
                    offer3TextPush.alpha = 0.5f

                    offer1TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_title_light
                    )

                    offer2TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_pw_offer_title_selected
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
//                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer3TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_title_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
//                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer2MainBlockPush.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_paywall_push_offer_active
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
//                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                    offer3MainBlockPush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
//                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                }
            }

            PAYWALL_TYPE.PAYWALL2 -> {
                with(binding.paywall2) {
                    startBtnText.text = App.resourcesProvider.getStringLocale(R.string.paywall_btn)
                    endingPw2.text =
                        App.adaptyOffers?.let {
                            it["second_offer"]?.let { offer ->
                                if (offer is String) {
                                    offer.getEnding() ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_month)
                                } else App.resourcesProvider.getStringLocale(R.string.pw_ending_month)
                            } ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_month)
                        } ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_month)

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
                            else R.color.blackColor
                        )
                    )

                    offer3TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.blackColor
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

            PAYWALL_TYPE.PAYWALL_PUSH -> {
                with(binding.paywallPush) {
                    offer1Push.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_light
                    )

                    offer3TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.lightColor)
                    )
                    offer2TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )
                    offer1TitlePush.setTextColor(
                        ContextCompat.getColor(requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.darkColor
                        )
                    )

                    offer1DurationPush.alpha = 0.5f
                    offer1PricePush.alpha = 0.5f
                    offer1TextPush.alpha = 0.5f

                    offer2DurationPush.alpha = 0.5f
                    offer2PricePush.alpha = 0.5f
                    offer2TextPush.alpha = 0.5f

                    offer3DurationPush.alpha = 1f
                    offer3PricePush.alpha = 1f
                    offer3TextPush.alpha = 1f

                    offer1TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_title_light
                    )

                    offer2TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_title_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
//                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer3TitlePush.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_pw_offer_title_selected
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_2_offer_title_dark
//                        else R.drawable.bg_paywall_2_offer_title_light
                    )

                    offer2MainBlockPush.background = ContextCompat.getDrawable(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_push_offer_title
                        else R.drawable.bg_paywall_push_offer_light
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
//                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                    offer3MainBlockPush.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_paywall_push_offer_active
//                        if (App.preferences.isDarkTheme) R.drawable.bg_paywall_1_offer_2_inactive
//                        else R.drawable.bg_paywall_1_offer_2_inactive_light
                    )
                }
            }

            PAYWALL_TYPE.PAYWALL2 -> {
                with(binding.paywall2) {
                    startBtnText.text = App.resourcesProvider.getStringLocale(R.string.paywall_btn)
                    endingPw2.text =
                        App.adaptyOffers?.let {
                            it["third_offer"]?.let { offer ->
                                if (offer is String) {
                                    offer.getEnding() ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_year)
                                } else App.resourcesProvider.getStringLocale(R.string.pw_ending_year)
                            } ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_year)
                        } ?: App.resourcesProvider.getStringLocale(R.string.pw_ending_year)


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
                            else R.color.blackColor
                        )
                    )

                    offer1TitlePw22.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (App.preferences.isDarkTheme) R.color.lightColor
                            else R.color.blackColor
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
                runCatching {
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
    PAYWALL_PUSH,
    PAYWALL2,
    PAYWALL22,
    PAYWALL3,
    SPECIAL
}