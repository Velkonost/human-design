package ru.get.hd.ui.adduser

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.animation.doOnEnd
import ru.get.hd.ui.start.StartFragment
import ru.get.hd.ui.start.StartPage
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.convertPixelsToDp

fun AddUserFragment.animateCirclesBtwPages(duration: Long) {
    binding.icSplashBigCircle.clearAnimation()
    binding.icSplashMidCircle.clearAnimation()

    val step = when (currentStartPage) {
        StartPage.RAVE -> 1
        StartPage.NAME -> 2
        StartPage.DATE_BIRTH -> 3
        StartPage.TIME_BIRTH -> 4
        StartPage.PLACE_BIRTH -> 5
        StartPage.BODYGRAPH -> 6
    }

    val newSizeBigCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(848f)
        StartPage.NAME -> requireContext().convertDpToPx(719f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(332f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(332f)
    }

    val newSizeMidCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(790f)
        StartPage.NAME -> requireContext().convertDpToPx(640f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(480f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(380f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(270f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(270f)
    }

    val oldSizeBigCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(848f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(719f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(461f)
    }

    val oldSizeMidCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(790f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(760f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(530f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(400f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(400f)
    }

    val animSizeBigCircle =
        ValueAnimator.ofInt(oldSizeBigCircle.toInt(), newSizeBigCircle.toInt())
    val animSizeMidCircle =
        ValueAnimator.ofInt(oldSizeMidCircle.toInt(), newSizeMidCircle.toInt())
    animSizeBigCircle.addUpdateListener { valueAnimator ->
        val `val` = valueAnimator.animatedValue as Int

        val layoutParamsBigCircle: ViewGroup.LayoutParams =
            binding.icSplashBigCircle.layoutParams
        layoutParamsBigCircle.height = `val`
        layoutParamsBigCircle.width = `val`
        binding.icSplashBigCircle.layoutParams = layoutParamsBigCircle
        binding.icSplashBigCircle.requestLayout()
    }
    animSizeBigCircle.duration = 1000

    animSizeMidCircle.addUpdateListener { valueAnimator ->
        val `val` = valueAnimator.animatedValue as Int

        val layoutParamsMidCircle: ViewGroup.LayoutParams =
            binding.icSplashMidCircle.layoutParams
        layoutParamsMidCircle.height = `val`
        layoutParamsMidCircle.width = `val`
        binding.icSplashMidCircle.layoutParams = layoutParamsMidCircle
        binding.icSplashMidCircle.requestLayout()
    }
    animSizeMidCircle.duration = 1000

    val yAnimatorBigCircle = ObjectAnimator.ofFloat(
        binding.icSplashBigCircle, "translationY", stepTranslationYForBigCircle * step
    )

    val yAnimatorMidCircle = ObjectAnimator.ofFloat(
        binding.icSplashMidCircle,
        "translationY",
        (stepTranslationYForBigCircle - requireContext().convertDpToPx(12f)) * step
    )
    yAnimatorBigCircle.duration = duration
    yAnimatorMidCircle.duration = duration

    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        yAnimatorMidCircle,
        yAnimatorBigCircle,
        animSizeBigCircle,
        animSizeMidCircle
    )

    animatorSet.start()

    animatorSet.doOnEnd {
        startCirclesRotation(currentStartPage)
    }

}

fun AddUserFragment.animateBackCirclesBtwPages() {
    binding.icSplashBigCircle.clearAnimation()
    binding.icSplashMidCircle.clearAnimation()

    val prevStartPage = when (currentStartPage) {
        StartPage.RAVE -> StartPage.RAVE
        StartPage.NAME -> StartPage.RAVE
        StartPage.DATE_BIRTH -> StartPage.NAME
        StartPage.TIME_BIRTH -> StartPage.DATE_BIRTH
        StartPage.PLACE_BIRTH -> StartPage.TIME_BIRTH
        StartPage.BODYGRAPH -> StartPage.BODYGRAPH
    }

    val step = when (prevStartPage) {
        StartPage.RAVE -> 0
        StartPage.NAME -> 1
        StartPage.DATE_BIRTH -> 2
        StartPage.TIME_BIRTH -> 3
        StartPage.PLACE_BIRTH -> 4
        StartPage.BODYGRAPH -> 5
    }


    val oldSizeBigCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(848f)
        StartPage.NAME -> requireContext().convertDpToPx(719f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(332f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(332f)
    }

    val oldSizeMidCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(790f)
        StartPage.NAME -> requireContext().convertDpToPx(640f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(480f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(380f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(270f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(270f)
    }

    val newSizeBigCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(848f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(719f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(461f)
    }

    val newSizeMidCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(790f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(640f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(480f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(400f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(400f)
    }

    val animSizeBigCircle =
        ValueAnimator.ofInt(oldSizeBigCircle.toInt(), newSizeBigCircle.toInt())
    val animSizeMidCircle =
        ValueAnimator.ofInt(oldSizeMidCircle.toInt(), newSizeMidCircle.toInt())
    animSizeBigCircle.addUpdateListener { valueAnimator ->
        val `val` = valueAnimator.animatedValue as Int

        val layoutParamsBigCircle: ViewGroup.LayoutParams =
            binding.icSplashBigCircle.layoutParams
        layoutParamsBigCircle.height = `val`
        layoutParamsBigCircle.width = `val`
        binding.icSplashBigCircle.layoutParams = layoutParamsBigCircle
        binding.icSplashBigCircle.requestLayout()
    }
    animSizeBigCircle.duration = 1000

    animSizeMidCircle.addUpdateListener { valueAnimator ->
        val `val` = valueAnimator.animatedValue as Int

        val layoutParamsMidCircle: ViewGroup.LayoutParams =
            binding.icSplashMidCircle.layoutParams
        layoutParamsMidCircle.height = `val`
        layoutParamsMidCircle.width = `val`
        binding.icSplashMidCircle.layoutParams = layoutParamsMidCircle
        binding.icSplashMidCircle.requestLayout()
    }
    animSizeMidCircle.duration = 1000

    val yAnimatorBigCircle = ObjectAnimator.ofFloat(
        binding.icSplashBigCircle, "translationY", stepTranslationYForBigCircle * step
    )

    val yAnimatorMidCircle = ObjectAnimator.ofFloat(
        binding.icSplashMidCircle,
        "translationY",
        (stepTranslationYForBigCircle - requireContext().convertDpToPx(12f)) * step
    )
    yAnimatorBigCircle.duration = 1000
    yAnimatorMidCircle.duration = 1000

    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        yAnimatorMidCircle,
        yAnimatorBigCircle,
        animSizeBigCircle,
        animSizeMidCircle
    )

    animatorSet.start()

    animatorSet.doOnEnd {
        startCirclesRotation(prevStartPage)
    }
}

fun AddUserFragment.startCirclesRotation(
    page: StartPage
) {

//        y/x   y/x
//        0.5   0.5
//        0.42  0.4
//        0.32  0.255/0.51
//        0.165 0.01/0.51
//        -0.08 -0.33
//        -0.5  -0.95
//       x      x
//

    val pivotXBigCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.5f
        StartPage.DATE_BIRTH -> 0.5f
        StartPage.TIME_BIRTH -> 0.5f
        StartPage.PLACE_BIRTH -> 0.5f
        StartPage.BODYGRAPH -> 0.5f
    }

    val pivotXMidCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.5f
        StartPage.DATE_BIRTH -> 0.51f
        StartPage.TIME_BIRTH -> 0.51f
        StartPage.PLACE_BIRTH -> 0.5f
        StartPage.BODYGRAPH -> 0.5f
    }

    val pivotYBigCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.42f
        StartPage.DATE_BIRTH -> 0.32f
        StartPage.TIME_BIRTH -> 0.165f
        StartPage.PLACE_BIRTH -> -0.08f
        StartPage.BODYGRAPH -> -0.5f
    }

    val pivotYMidCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.4f
        StartPage.DATE_BIRTH -> 0.255f
        StartPage.TIME_BIRTH -> 0.01f
        StartPage.PLACE_BIRTH -> -0.33f
        StartPage.BODYGRAPH -> -0.95f
    }

    val rotate = RotateAnimation(
        0f,
        360f,
        Animation.RELATIVE_TO_SELF,
        pivotXBigCircle,
        Animation.RELATIVE_TO_SELF,
        pivotYBigCircle
    )

    rotate.repeatCount = Animation.INFINITE
    rotate.fillAfter = true
    rotate.duration = 100000
    rotate.interpolator = LinearInterpolator()

    val rotateNegative = RotateAnimation(
        0f,
        -360f,
        Animation.RELATIVE_TO_SELF,
        pivotXMidCircle,
        Animation.RELATIVE_TO_SELF,
        pivotYMidCircle
    )
    rotateNegative.repeatCount = Animation.INFINITE
    rotateNegative.fillAfter = true
    rotateNegative.duration = 100000
        rotateNegative.interpolator = LinearInterpolator()

    binding.icSplashBigCircle.startAnimation(rotate)
    binding.icSplashMidCircle.startAnimation(rotateNegative)

}