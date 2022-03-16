package ru.get.hd.ui.splash

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import ru.get.hd.App
import ru.get.hd.R

fun SplashFragment.updateTheme() {
    binding.splashContainer.setBackgroundColor(
        ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        )
    )

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

    binding.icSplash05Header.setImageResource(
        if (App.preferences.isDarkTheme) R.drawable.ic_splash_05_header_dark
        else R.drawable.ic_splash_05_header_light
    )

    binding.text1Splash05.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.text2Splash05.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))

    binding.text3Splash05.setTextColor(ContextCompat.getColor(
        requireContext(),
        if (App.preferences.isDarkTheme) R.color.lightColor
        else R.color.darkColor
    ))
}