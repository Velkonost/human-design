package com.myhumandesignhd.ui.start

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.ui.start.ext.setupSignupTheme
import kotlinx.android.synthetic.main.view_place_select.view.icArrowPlace
import kotlinx.android.synthetic.main.view_place_select.view.icSearch
import kotlinx.android.synthetic.main.view_place_select.view.newPlaceET
import kotlinx.android.synthetic.main.view_place_select.view.placesViewContainer
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersFooter
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersSubtitle
import kotlinx.android.synthetic.main.view_signup_old_users.view.oldUsersTitle

fun StartFragment.updateTheme() {
    binding.loaderView.container.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_splash_dark
        else R.drawable.bg_splash_light
    )

    binding.loaderView.anim.setAnimation(
        if (App.preferences.isDarkTheme) R.raw.logo_transition_black
        else R.raw.logo_transition_white
    )

    binding.loaderView.anim2.setAnimation(
        if (App.preferences.isDarkTheme) R.raw.loader_white
        else R.raw.loader_black
    )

    binding.icArrow.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )
//    binding.icArrow.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.ic_arrow_start_dark
//        else R.drawable.ic_arrow_start_light
//    )

    binding.startContainer.setBackgroundColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        )
    )

    binding.placesView.newPlaceET.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.placesView.newPlaceET.setHintTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        )
    )

    binding.placesView.newPlaceET.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        )
    )

    binding.placesView.placesViewContainer.setBackgroundColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        )
    )

    binding.bodygraphReadyTitle.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

//    binding.bodygraphReadyText.setTextColor(ContextCompat.getColor(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    ))

//    binding.titleSplash0102.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//
//    binding.descSplash0102.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//
//    binding.icSplashBigCircle.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.ic_circle_big_dark
//        else R.drawable.ic_circle_big_light
//    )
//
//    binding.icSplashMidCircle.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.ic_circle_mid_dark
//        else R.drawable.ic_circle_mid_light
//    )

    binding.icSplashBigCircle.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_circle_big_dark
        else R.drawable.ic_circle_big_light
    )

    binding.icSplashMidCircle.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_circle_mid_dark
        else R.drawable.ic_circle_mid_light
    )

    binding.placesView.newPlaceET.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_search_dark
        else R.drawable.bg_search_light
    )

    binding.placesView.icArrowPlace.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.placesView.icSearch.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.searchTintDark
            else R.color.searchTintLight
        )
    )

    binding.bgSplashHeader.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.bg_start_header_dark
        else R.drawable.bg_start_header_light
    )
//    binding.bgSplashHeader.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.bg_start_header_dark
//        else R.drawable.bg_start_header_light
//    )

//    LottieCompositionFactory.fromRawResSync(
//        requireContext(),
//        if (!App.preferences.isDarkTheme) R.raw.start_header_dark
//        else R.raw.start_header_light
//    )?.let { result ->
//        result.value?.let { composition -> binding.startHeaderAnim.setComposition(composition) }
//    }

//    binding.bodygraphReadyText1.setTextColor(ContextCompat.getColor(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    ))
//    binding.bodygraphReadyText2.setTextColor(ContextCompat.getColor(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    ))
//    binding.bodygraphReadyText3.setTextColor(ContextCompat.getColor(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    ))
//    binding.bodygraphReadyText4.setTextColor(ContextCompat.getColor(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    ))
//    binding.bodygraphReadyText5.setTextColor(ContextCompat.getColor(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    ))

//    binding.viewOnboarding.onboardingTitle.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//    binding.viewOnboarding.onboardingDesc.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//    binding.viewOnboarding.titleVariant1.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//    binding.viewOnboarding.titleVariant2.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//    binding.viewOnboarding.titleVariant3.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//    binding.viewOnboarding.breaklineVariant1.background = ContextCompat.getDrawable(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    )
//    binding.viewOnboarding.breaklineVariant2.background = ContextCompat.getDrawable(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    )
//    binding.viewOnboarding.breaklineVariant3.background = ContextCompat.getDrawable(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.lightColor
//        else R.color.darkColor
//    )
//    binding.viewOnboarding.icOnboardingBg.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.ic_onboarding_bg
//        else R.drawable.ic_splash_onboarding_bg_light_svg
//    )
//    binding.viewOnboarding.icCircles.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.ic_splash_circles_dark
//        else R.drawable.ic_splash_circles_light
//    )
//    binding.viewOnboarding.onboardingContainer.background = ContextCompat.getDrawable(
//        requireContext(),
//        if (App.preferences.isDarkTheme) R.color.darkColor
//        else R.color.lightColor
//    )

    setupSignupTheme()

//    old onboarding

    LottieCompositionFactory.fromRawResSync(
        requireContext(),
        if (!App.preferences.isDarkTheme) R.raw.start_header_dark
        else R.raw.start_header_light
    )?.let { result ->
        result.value?.let { composition -> binding.startHeaderAnim.setComposition(composition) }
    }
    binding.titleSplash0102.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )
    binding.descSplash0102.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.bodygraphReadyText1.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )
    binding.bodygraphReadyText2.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )
    binding.bodygraphReadyText3.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )
    binding.bodygraphReadyText4.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )
    binding.bodygraphReadyText5.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    with(binding.oldUsersView) {
        oldUsersTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        oldUsersSubtitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        oldUsersFooter.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )
    }

}