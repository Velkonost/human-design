package com.myhumandesignhd.ui.paywall

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentLoaderBinding
import com.myhumandesignhd.databinding.FragmentPaywallBinding
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.loader.LoaderFragment
import com.myhumandesignhd.ui.loader.LoaderViewModel
import com.myhumandesignhd.ui.paywall.adapter.Pw3ReviewsAdapter
import com.myhumandesignhd.util.ext.scaleXY
import com.myhumandesignhd.vm.BaseViewModel
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import kotlinx.android.synthetic.main.view_paywall_1.view.*
import kotlinx.android.synthetic.main.view_paywall_1.view.breakline1
import kotlinx.android.synthetic.main.view_paywall_1.view.breakline2
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1Duration
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1Price
import kotlinx.android.synthetic.main.view_paywall_1.view.offer1Text
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Price
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Text
import kotlinx.android.synthetic.main.view_paywall_1.view.offer2Title
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Duration
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Price
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Text
import kotlinx.android.synthetic.main.view_paywall_1.view.offer3Title
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Policy
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1Restore
import kotlinx.android.synthetic.main.view_paywall_1.view.paywall1TermsOfUse
import kotlinx.android.synthetic.main.view_paywall_2.view.*
import kotlinx.android.synthetic.main.view_paywall_3.view.*
import org.greenrobot.eventbus.EventBus

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

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(false))
        binding.container.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

//        when {
//            App.preferences.lastPaywall % 3 == 0 -> {
//                setupFirstPaywall()
//            }
//            App.preferences.lastPaywall % 3 == 1 -> {
//                setupSecondPaywall()
//            }
//            else -> setupThirdPaywall()
//        }
        setupFirstPaywall()
        App.preferences.lastPaywall ++
    }

    override fun onViewModelReady(viewModel: LoaderViewModel) {
        super.onViewModelReady(viewModel)
    }

    private fun setupFirstPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL1

        binding.paywall1.isVisible = true
        with(binding.paywall1) {
            paywall1Title.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            text1.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            text2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            text3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            text4.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer1Duration.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer2Duration.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer3Duration.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer1Price.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer2Price.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer3Price.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer1Text.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer2Text.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer3Text.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer2Title.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            offer3Title.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            paywall1TermsOfUse.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            paywall1Policy.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            paywall1Restore.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            breakline1.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            breakline2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            bottomGradient.isVisible = App.preferences.isDarkTheme
            paywall1Close.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1.setOnClickListener { selectFirstOffer() }
            offer2.setOnClickListener { selectSecondOffer() }
            offer3.setOnClickListener { selectThirdOffer() }
            paywall1TermsOfUse.setOnClickListener { openTermsOfUse() }
            paywall1Policy.setOnClickListener { openPrivacyPolicy() }
            paywall1Restore.setOnClickListener { openRestore() }
            paywall1Close.setOnClickListener { close() }

            icBigCircle.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            icMidCircle.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

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

    private fun setupSecondPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL2

        binding.paywall2.isVisible = true
        with(binding.paywall2) {
            title.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text1Pw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text2Pw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text3Pw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text4Pw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            icLogoPw2.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_paywall_2_logo_dark
                else R.drawable.ic_paywall_2_logo_light
            )

            offer1DurationPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2DurationPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3DurationPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1PricePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2PricePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3PricePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1TextPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2TextPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3TextPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1TitlePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2TitlePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3TitlePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            termsOfUsePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            policyPw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            restorePw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            breakline1Pw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            breakline2Pw2.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

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
            closePw2.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1Pw2.setOnClickListener { selectFirstOffer() }
            offer2Pw2.setOnClickListener { selectSecondOffer() }
            offer3Pw2.setOnClickListener { selectThirdOffer() }
            termsOfUsePw2.setOnClickListener { openTermsOfUse() }
            policyPw2.setOnClickListener { openPrivacyPolicy() }
            restorePw2.setOnClickListener { openRestore() }
            closePw2.setOnClickListener { close() }

            selectSecondOffer()
        }
    }

    private fun setupThirdPaywall() {
        selectedPaywall = PAYWALL_TYPE.PAYWALL3

        binding.paywall3.isVisible = true
        with(binding.paywall3) {
            titlePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            subtitlePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1DurationPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2DurationPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3DurationPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1PricePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2PricePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3PricePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1TextPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2TextPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3TextPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1TitlePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer2TitlePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer3TitlePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            termsOfUsePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            policyPw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            restorePw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            breakline1Pw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            breakline2Pw3.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

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
            closePw3.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            offer1Pw3.setOnClickListener { selectFirstOffer() }
            offer2Pw3.setOnClickListener { selectSecondOffer() }
            offer3Pw3.setOnClickListener { selectThirdOffer() }
            termsOfUsePw3.setOnClickListener { openTermsOfUse() }
            policyPw3.setOnClickListener { openPrivacyPolicy() }
            restorePw3.setOnClickListener { openRestore() }
            closePw3.setOnClickListener { close() }

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
                        val visibleItemCount: Int = childCount
                        val totalItemCount: Int = itemCount
                        this as LinearLayoutManager
                        val firstVisibleItemPosition: Int = findFirstVisibleItemPosition()
                        updatePw3IndicatorsState(firstVisibleItemPosition)
//                        binding.viewModel?.apply {
//                            if (!bannerLoadingState.isLoading && !bannerLoadingState.isLastPage()) {
//                                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
//                                    && firstVisibleItemPosition >= 0
//                                    && totalItemCount >= PAGE_SIZE
//                                ) {
//                                    loadBanners()
//                                }
//                            }
//                        }
                    }
                }
            })

            selectSecondOffer()
        }
    }

    private fun selectFirstOffer() {
        selectedOffer = 1

        when(selectedPaywall) {
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

                        offer2Title.setTextColor(ContextCompat.getColor(
                            requireContext(),
                            R.color.darkColor
                        ))
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

        when(selectedPaywall) {
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

                        offer3Title.setTextColor(ContextCompat.getColor(
                            requireContext(),
                            R.color.darkColor
                        ))
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

        when(selectedPaywall) {
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

                        offer2Title.setTextColor(ContextCompat.getColor(
                            requireContext(),
                            R.color.darkColor
                        ))
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


            val activeIndicator = when(activeIndicatorPosition) {
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

    private fun openRestore() {

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

    }
}

enum class PAYWALL_TYPE {
    PAYWALL1,
    PAYWALL2,
    PAYWALL3
}