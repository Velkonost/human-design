package ru.get.hd.event

import ru.get.hd.model.User

data class CompatibilityStartClickEvent(
    val user: User,
    val chartResId: Int
    )