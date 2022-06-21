package com.myhumandesignhd.event

import com.myhumandesignhd.model.User

data class CompatibilityStartClickEvent(
    val user: User,
    val chartResId: Int
    )