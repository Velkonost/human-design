package com.myhumandesignhd.ui.adduser

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.myhumandesignhd.ui.start.StartPage
import com.myhumandesignhd.util.convertDpToPx

fun AddUserFragment.animateCirclesBtwPages(duration: Long) {
    binding.icSplashBigCircle.clearAnimation()
    binding.icSplashBigCircle.rotation = 0f
    binding.icSplashMidCircle.clearAnimation()
    binding.icSplashMidCircle.rotation = 0f

//    if (isShouldFirstCirclesHide) {
//        binding.icSplashBigCircle.isVisible = false
//        binding.icSplashMidCircle.isVisible = false
//    }

    binding.icSplashBigCirclePlaceOfBirth.clearAnimation()
    binding.icSplashMidCirclePlaceOfBirth.clearAnimation()
    binding.icSplashBigCirclePlaceOfBirth.rotation = 0f
    binding.icSplashMidCirclePlaceOfBirth.rotation = 0f

    val step = when (currentStartPage) {
        StartPage.RAVE -> 1
        StartPage.NAME -> 2
        StartPage.DATE_BIRTH -> 3
        StartPage.TIME_BIRTH -> 5
        StartPage.PLACE_BIRTH -> 5
        StartPage.BODYGRAPH -> 6
        else -> 1
    }

    val newSizeBigCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(848f)
        StartPage.NAME -> requireContext().convertDpToPx(719f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(290f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(290f)
        else -> requireContext().convertDpToPx(848f)
    }

    val newSizeMidCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(790f)
        StartPage.NAME -> requireContext().convertDpToPx(640f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(480f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(380f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(236f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(236f)
        else -> requireContext().convertDpToPx(790f)
    }

    val oldSizeBigCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(848f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(719f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(461f)
        else -> requireContext().convertDpToPx(977f)
    }

    val oldSizeMidCircle = when (currentStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(790f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(760f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(530f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(400f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(400f)
        else -> requireContext().convertDpToPx(977f)
    }

    val animSizeBigCircle =
        ValueAnimator.ofInt(oldSizeBigCircle.toInt(), newSizeBigCircle.toInt())
    val animSizeMidCircle =
        ValueAnimator.ofInt(oldSizeMidCircle.toInt(), newSizeMidCircle.toInt())
    animSizeBigCircle.duration = 1000
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

    var animatorSet = AnimatorSet()


    when (currentStartPage) {
        StartPage.TIME_BIRTH -> {
            animSizeBigCircle.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int

                val layoutParamsBigCircle: ViewGroup.LayoutParams =
                    binding.icSplashBigCircle.layoutParams
                layoutParamsBigCircle.height = `val`
                layoutParamsBigCircle.width = `val`
                binding.icSplashBigCircle.layoutParams = layoutParamsBigCircle
                binding.icSplashBigCircle.requestLayout()
            }

            animSizeMidCircle.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int

                val layoutParamsMidCircle: ViewGroup.LayoutParams =
                    binding.icSplashMidCircle.layoutParams
                layoutParamsMidCircle.height = `val`
                layoutParamsMidCircle.width = `val`
                binding.icSplashMidCircle.layoutParams = layoutParamsMidCircle
                binding.icSplashMidCircle.requestLayout()
            }

            binding.icSplashBigCircle
                .animate()
//            .x(binding.icSplashBigCirclePlaceOfBirth.x - binding.icSplashBigCirclePlaceOfBirth.width / 2)
                .y(binding.icSplashBigCirclePlaceOfBirth.y )
                .setDuration(1000)
                .start()

            binding.icSplashMidCircle
                .animate()
//            .x(binding.icSplashMidCirclePlaceOfBirth.x)
                .y(binding.icSplashMidCirclePlaceOfBirth.y)
                .setDuration(1000)
                .start()

            animatorSet.playTogether(
                animSizeBigCircle,
                animSizeMidCircle
            )
        }
        StartPage.PLACE_BIRTH -> {
            val bigX = binding.icSplashBigCirclePlaceOfBirth.x
            val bigY = binding.icSplashBigCirclePlaceOfBirth.y


            animSizeBigCircle.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int

                val layoutParamsBigCircle: ViewGroup.LayoutParams =
                    binding.icSplashBigCirclePlaceOfBirth.layoutParams
                layoutParamsBigCircle.height = `val`
                layoutParamsBigCircle.width = `val`
                binding.icSplashBigCirclePlaceOfBirth.layoutParams = layoutParamsBigCircle
                binding.icSplashBigCirclePlaceOfBirth.requestLayout()
            }

            animSizeMidCircle.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int

                val layoutParamsMidCircle: ViewGroup.LayoutParams =
                    binding.icSplashMidCirclePlaceOfBirth.layoutParams
                layoutParamsMidCircle.height = `val`
                layoutParamsMidCircle.width = `val`
                binding.icSplashMidCirclePlaceOfBirth.layoutParams = layoutParamsMidCircle
                binding.icSplashMidCirclePlaceOfBirth.requestLayout()
            }

//        binding.icSplashBigCirclePlaceOfBirth
//            .animate()
//            .x(bigX)
//            .y(bigY)
//            .setDuration(1200)
//            .start()

            animatorSet.playTogether(
                animSizeBigCircle,
                animSizeMidCircle
            )
        }
        else -> {
            animSizeBigCircle.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int

                val layoutParamsBigCircle: ViewGroup.LayoutParams =
                    binding.icSplashBigCircle.layoutParams
                layoutParamsBigCircle.height = `val`
                layoutParamsBigCircle.width = `val`
                binding.icSplashBigCircle.layoutParams = layoutParamsBigCircle
                binding.icSplashBigCircle.requestLayout()
            }

            animSizeMidCircle.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int

                val layoutParamsMidCircle: ViewGroup.LayoutParams =
                    binding.icSplashMidCircle.layoutParams
                layoutParamsMidCircle.height = `val`
                layoutParamsMidCircle.width = `val`
                binding.icSplashMidCircle.layoutParams = layoutParamsMidCircle
                binding.icSplashMidCircle.requestLayout()
            }

            animatorSet.playTogether(
                yAnimatorMidCircle,
                yAnimatorBigCircle,
                animSizeBigCircle,
                animSizeMidCircle
            )
        }
    }



    animatorSet.start()

    animatorSet.doOnEnd {
        if (
            currentStartPage == StartPage.PLACE_BIRTH
            || currentStartPage == StartPage.BODYGRAPH
        ) {
            binding.icSplashMidCircle.isVisible = false
            binding.icSplashBigCircle.isVisible = false

            binding.icSplashBigCirclePlaceOfBirth.isVisible = true
            binding.icSplashMidCirclePlaceOfBirth.isVisible = true
        }
        startCirclesRotation(currentStartPage)
    }
}

fun AddUserFragment.animateBackCirclesBtwPages() {
    binding.icSplashBigCircle.clearAnimation()
    binding.icSplashMidCircle.clearAnimation()

    binding.icSplashMidCirclePlaceOfBirth.clearAnimation()
    binding.icSplashBigCirclePlaceOfBirth.clearAnimation()

    val prevStartPage = when (currentStartPage) {
        StartPage.RAVE -> StartPage.RAVE
        StartPage.NAME -> StartPage.RAVE
        StartPage.DATE_BIRTH -> StartPage.NAME
        StartPage.TIME_BIRTH -> StartPage.DATE_BIRTH
        StartPage.PLACE_BIRTH -> StartPage.TIME_BIRTH
        StartPage.BODYGRAPH -> StartPage.BODYGRAPH
        else -> StartPage.RAVE
    }

    val step = when (prevStartPage) {
        StartPage.RAVE -> 0
        StartPage.NAME -> 1
        StartPage.DATE_BIRTH -> 2
        StartPage.TIME_BIRTH -> 3
        StartPage.PLACE_BIRTH -> 4
        StartPage.BODYGRAPH -> 5
        else -> 0
    }


    val oldSizeBigCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(848f)
        StartPage.NAME -> requireContext().convertDpToPx(719f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(332f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(332f)
        else -> requireContext().convertDpToPx(848f)
    }

    val oldSizeMidCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(790f)
        StartPage.NAME -> requireContext().convertDpToPx(640f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(480f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(380f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(270f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(270f)
        else -> requireContext().convertDpToPx(790f)
    }

    val newSizeBigCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(848f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(719f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(590f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(461f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(461f)
        else -> requireContext().convertDpToPx(977f)
    }

    val newSizeMidCircle = when (prevStartPage) {
        StartPage.RAVE -> requireContext().convertDpToPx(977f)
        StartPage.NAME -> requireContext().convertDpToPx(790f)
        StartPage.DATE_BIRTH -> requireContext().convertDpToPx(640f)
        StartPage.TIME_BIRTH -> requireContext().convertDpToPx(480f)
        StartPage.PLACE_BIRTH -> requireContext().convertDpToPx(400f)
        StartPage.BODYGRAPH -> requireContext().convertDpToPx(400f)
        else -> requireContext().convertDpToPx(977f)
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
    if (currentStartPage == StartPage.PLACE_BIRTH) {
        binding.icSplashMidCircle.isVisible = true
        binding.icSplashBigCircle.isVisible = true

        binding.icSplashBigCirclePlaceOfBirth.isVisible = false
        binding.icSplashMidCirclePlaceOfBirth.isVisible = false
    }

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
        else -> 0.5f
    }

    val pivotXMidCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.5f
        StartPage.DATE_BIRTH -> 0.51f
        StartPage.TIME_BIRTH -> 0.51f
        StartPage.PLACE_BIRTH -> 0.5f
        StartPage.BODYGRAPH -> 0.5f
        else -> 0.5f
    }

    val pivotYBigCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.42f
        StartPage.DATE_BIRTH -> 0.32f
        StartPage.TIME_BIRTH -> 0.165f
//        StartPage.PLACE_BIRTH -> -0.08f
        StartPage.PLACE_BIRTH -> -0.2f
        StartPage.BODYGRAPH -> -0.5f
        else -> 0.5f
    }

    val pivotYMidCircle = when (page) {
        StartPage.RAVE -> 0.5f
        StartPage.NAME -> 0.4f
        StartPage.DATE_BIRTH -> 0.255f
        StartPage.TIME_BIRTH -> 0.01f
//        StartPage.PLACE_BIRTH -> -0.33f
        StartPage.PLACE_BIRTH -> -0.54f
        StartPage.BODYGRAPH -> -0.95f
        else -> 0.5f
    }

    if (currentStartPage == StartPage.PLACE_BIRTH || currentStartPage == StartPage.BODYGRAPH) {
        val rotate = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotate.repeatCount = Animation.INFINITE
        rotate.fillAfter = true
        rotate.duration = 100000
        rotate.interpolator = LinearInterpolator()

        val rotateNegative = RotateAnimation(
            0f,
            -360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateNegative.repeatCount = Animation.INFINITE
        rotateNegative.fillAfter = true
        rotateNegative.duration = 100000
//        rotateNegative.interpolator = LinearInterpolator()

        binding.icSplashBigCirclePlaceOfBirth.startAnimation(rotate)
        binding.icSplashMidCirclePlaceOfBirth.startAnimation(rotateNegative)
    } else {
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
//        rotateNegative.interpolator = LinearInterpolator()

        binding.icSplashBigCircle.startAnimation(rotate)
        binding.icSplashMidCircle.startAnimation(rotateNegative)
    }


}