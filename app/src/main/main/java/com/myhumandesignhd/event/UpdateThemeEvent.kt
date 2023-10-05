package com.myhumandesignhd.event

data class UpdateThemeEvent(
    val isDarkTheme: Boolean,
    val withAnimation: Boolean = false,
    val withTextAnimation: Boolean = false
)