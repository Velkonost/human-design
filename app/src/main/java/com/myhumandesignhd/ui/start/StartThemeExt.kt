package com.myhumandesignhd.ui.start

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.view_allow_pushes.view.allowPushBackground
import kotlinx.android.synthetic.main.view_allow_pushes.view.allowPushCard
import kotlinx.android.synthetic.main.view_allow_pushes.view.allowPushText
import kotlinx.android.synthetic.main.view_allow_pushes.view.allowPushTitle
import kotlinx.android.synthetic.main.view_place_select.view.icArrowPlace
import kotlinx.android.synthetic.main.view_place_select.view.icSearch
import kotlinx.android.synthetic.main.view_place_select.view.newPlaceET
import kotlinx.android.synthetic.main.view_place_select.view.placesViewContainer

fun StartFragment.updateTheme() {
    binding.variant1Block.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_quiz_variant_dark
        else R.drawable.bg_quiz_variant
    )

    binding.variant2Block.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_quiz_variant_dark
        else R.drawable.bg_quiz_variant
    )

    binding.variant3Block.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_quiz_variant_dark
        else R.drawable.bg_quiz_variant
    )

    binding.variant4Block.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_quiz_variant_dark
        else R.drawable.bg_quiz_variant
    )

    binding.variant5Block.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_quiz_variant_dark
        else R.drawable.bg_quiz_variant
    )

    binding.variant6Block.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_quiz_variant_dark
        else R.drawable.bg_quiz_variant
    )

    binding.variant1Check.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_dark
        else R.drawable.ic_quiz_variant
    )

    binding.variant2Check.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_dark
        else R.drawable.ic_quiz_variant
    )

    binding.variant3Check.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_dark
        else R.drawable.ic_quiz_variant
    )

    binding.variant4Check.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_dark
        else R.drawable.ic_quiz_variant
    )

    binding.variant5Check.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_dark
        else R.drawable.ic_quiz_variant
    )

    binding.variant6Check.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_quiz_variant_dark
        else R.drawable.ic_quiz_variant
    )

    binding.variant1Text.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.variant2Text.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.variant3Text.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.variant4Text.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.variant5Text.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.variant6Text.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

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

    binding.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

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

    binding.placesView.newPlaceET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.darkHintColor
        else R.color.lightHintColor
    ))

    binding.placesView.placesViewContainer.setBackgroundColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        )
    )

    binding.bodygraphReadyTitle.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.bodygraphReadyText.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

//    binding.splashContainer.setBackgroundColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.darkColor
//            else R.color.lightColor
//        )
//    )

//    binding.icSplash01Footer.setImageResource(
//        if (App.preferences.isDarkTheme) R.drawable.ic_splash_01_footer_dark
//        else R.drawable.ic_splash_01_footer_light
//    )

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

    binding.placesView.icArrowPlace.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.placesView.icSearch.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.searchTintDark
        else R.color.searchTintLight
    ))

    binding.bgSplashHeader.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.bg_start_header_dark
        else R.drawable.bg_start_header_light
    )

//    binding.startHeaderAnim.setAnimation(
//        if (App.preferences.isDarkTheme) R.raw.start_header_dark
//        else R.raw.start_header_light
//    )

    LottieCompositionFactory.fromRawResSync(
        requireContext(),
        if (!App.preferences.isDarkTheme) R.raw.start_header_dark
        else R.raw.start_header_light
    )?.let { result ->
        result.value?.let { composition -> binding.startHeaderAnim.setComposition(composition) }
    }

    binding.bodygraphReadyText1.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))
    binding.bodygraphReadyText2.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))
    binding.bodygraphReadyText3.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))
    binding.bodygraphReadyText4.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))
    binding.bodygraphReadyText5.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    with(binding.allowPushView) {
        allowPushBackground.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        allowPushCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkCardColor
            else R.color.lightCardColor
        ))
        allowPushTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        allowPushText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
    }

}