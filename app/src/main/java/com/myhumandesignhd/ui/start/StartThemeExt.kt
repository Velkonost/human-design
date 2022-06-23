package com.myhumandesignhd.ui.start

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.view_place_select.view.*

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

    binding.startHeaderAnim.setAnimation(
        if (!App.preferences.isDarkTheme) R.raw.start_header_dark
        else R.raw.start_header_light
    )



}