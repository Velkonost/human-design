package ru.get.hd.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.util.DisplayMetrics




fun Context.convertDpToPx(dip: Float): Float {
    val r: Resources = resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip,
        r.displayMetrics
    )
}

fun Context.convertPixelsToDp(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}