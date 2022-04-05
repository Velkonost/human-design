package ru.get.hd.event

data class BodygraphCenterClickEvent(
    val title: String,
    val desc: String,
    val x: Float,
    val y: Float,
    val alignTop: Boolean,
    val arrowPosition: Float = 0.5f,
    val xOffset: Int = 0
)