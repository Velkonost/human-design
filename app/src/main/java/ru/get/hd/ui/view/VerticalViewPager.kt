package ru.get.hd.ui.view

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max


class VerticalViewPager : ViewPager {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setPageTransformer(true, VerticalPageTransformer())
        overScrollMode = OVER_SCROLL_NEVER
    }

    private inner class VerticalPageTransformer : PageTransformer {
        override fun transformPage(view: View, position: Float) {
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.alpha = 0F
                }
                position <= 1 -> { // [-1,1]
                    view.alpha = 1f

                    // Counteract the default slide transition
                    view.translationX = view.width * -position

                    //set Y position to swipe in from top
                    val yPosition: Float = position * view.height
                    view.translationY = yPosition
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.alpha = 0f
                }
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private fun swapXY(ev: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()
        val newX = ev.y / height * width
        val newY = ev.x / width * height
        ev.setLocation(newX, newY)
        return ev
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val intercepted = super.onInterceptTouchEvent(swapXY(ev))
        swapXY(ev)
        return intercepted
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(swapXY(ev))
    }

    fun changeCurrentItem(
        item: Int,
        duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
    ) {
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        val animator = ValueAnimator.ofInt(0, pxToDrag)
        var previousValue = 0
        animator.addUpdateListener { valueAnimator ->

            kotlin.runCatching {
                val currentValue = valueAnimator.animatedValue as Int
                val currentPxToDrag = (currentValue - previousValue).toFloat()
                fakeDragBy(-currentPxToDrag)
                previousValue = currentValue
            }
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                kotlin.runCatching { beginFakeDrag() }
            }

            override fun onAnimationEnd(animation: Animator?) {
                kotlin.runCatching { endFakeDrag() }

            }

            override fun onAnimationCancel(animation: Animator?) { /* Ignored */
            }

            override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
            }
        })
        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }
}

private class VerticalPageTransformerAnimate : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        var alpha = 0f
        if (position in 0.0..1.0) {
            alpha = 1 - position
        } else if (-1 < position && position < 0) {
            val scaleFactor = max(MIN_SCALE, 1 - abs(position))
            val verticalMargin = pageHeight * (1 - scaleFactor) / 2
            val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
            if (position < 0) {
                view.translationX = horizontalMargin - verticalMargin / 2
            } else {
                view.translationX = -horizontalMargin + verticalMargin / 2
            }
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
            alpha = position + 1
        }
        view.alpha = alpha
        view.translationX = view.width * -position
        val yPosition = position * view.height
        view.translationY = yPosition
    }

    companion object {
        private const val MIN_SCALE = 0.75f
    }
}