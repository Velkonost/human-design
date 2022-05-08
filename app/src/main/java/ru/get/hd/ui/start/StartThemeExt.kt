package ru.get.hd.ui.start

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_add_user.view.*
import kotlinx.android.synthetic.main.view_place_select.view.*
import ru.get.hd.App
import ru.get.hd.R

fun StartFragment.updateTheme() {
    binding.raveLogo.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.startContainer.setBackgroundColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.darkColor
            else ru.get.hd.R.color.lightColor
        )
    )

    binding.raveTitle.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
            else ru.get.hd.R.color.darkColor
        )
    )

    binding.raveDesc.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
            else ru.get.hd.R.color.darkColor
        )
    )

    binding.nameTitle.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
            else ru.get.hd.R.color.darkColor
        )
    )

    binding.nameDesc.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
            else ru.get.hd.R.color.darkColor
        )
    )

    binding.nameET.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
            else ru.get.hd.R.color.darkColor
        )
    )

    binding.nameET.setHintTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.darkHintColor
            else ru.get.hd.R.color.lightHintColor
        )
    )

    binding.skipTime.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.placeET.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.placeET.setHintTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
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

    binding.titleSplash0304.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.descSplash0304.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.textVariant1.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.textVariant2.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.textVariant3.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.textVariant4.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.textVariant5.setTextColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        )
    )

    binding.cardVariant1.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkCardColor
            else R.color.lightCardColor
        )
    )

    binding.cardVariant2.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkCardColor
            else R.color.lightCardColor
        )
    )

    binding.cardVariant3.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkCardColor
            else R.color.lightCardColor
        )
    )

    binding.cardVariant4.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkCardColor
            else R.color.lightCardColor
        )
    )

    binding.cardVariant5.backgroundTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkCardColor
            else R.color.lightCardColor
        )
    )

    binding.strokeVariant1.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
        else R.drawable.gradient_variant_card_selected_light
    )

    binding.strokeVariant2.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
        else R.drawable.gradient_variant_card_selected_light
    )

    binding.strokeVariant3.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
        else R.drawable.gradient_variant_card_selected_light
    )

    binding.strokeVariant4.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
        else R.drawable.gradient_variant_card_selected_light
    )

    binding.strokeVariant5.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
        else R.drawable.gradient_variant_card_selected_light
    )

//    binding.text1Splash05.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//
//    binding.text2Splash05.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )
//
//    binding.text3Splash05.setTextColor(
//        ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.lightColor
//            else R.color.darkColor
//        )
//    )

    binding.icSplashBigCircle.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_circle_big_dark
        else R.drawable.ic_circle_big_light
    )

    binding.icSplashMidCircle.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_circle_mid_dark
        else R.drawable.ic_circle_mid_light
    )

    binding.titleSplash05.setTextColor(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.descSplash05.setTextColor(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.ic01Splash05.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.ic02Splash05.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.ic03Splash05.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.text01Splash05.setTextColor(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.text02Splash05.setTextColor(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.text03Splash05.setTextColor(
        ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.container1Splash05.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_splash_05_dark
        else R.drawable.bg_splash_05_light
    )

    binding.container2Splash05.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_splash_05_dark
        else R.drawable.bg_splash_05_light
    )

    binding.container3Splash05.background = ContextCompat.getDrawable(
        requireContext(),
        if (App.preferences.isDarkTheme) R.drawable.bg_splash_05_dark
        else R.drawable.bg_splash_05_light
    )

    binding.nameET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.darkHintColor
        else R.color.lightHintColor
    ))

    binding.placeET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.darkHintColor
        else R.color.lightHintColor
    ))

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

}