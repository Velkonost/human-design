package com.myhumandesignhd.util.ext

import android.animation.Animator
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.myhumandesignhd.ui.start.makeGradientAppName

fun TextView.setTextAnimation(
    text: String,
    duration: Long = 300,
    completion: (() -> Unit)? = null
) {
    fadOutAnimation(duration) {
        this.text = Html.fromHtml(text)
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun TextView.setTextAnimationWithGradientAppName(
    text: String,
    duration: Long = 300,
    completion: (() -> Unit)? = null
) {
    fadOutAnimation(duration) {
        this.text = Html.fromHtml(text)
        this.makeGradientAppName(
            Pair(
                when {
                    text.contains("Human Design") -> "Human Design"
                    text.contains("Дизайна Человека") -> "Дизайна Человека"
                    else -> "Дизайне Человека"
                }
            , View.OnClickListener {}
            )
        )
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun TextView.setTextAnimation07(
    text: String,
    duration: Long = 300,
    completion: (() -> Unit)? = null
) {
    fadOutAnimation(duration) {
        this.text = Html.fromHtml(text)
        fad07Animation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun ImageView.setImageAnimation(
    resId: Int,
    duration: Long = 300,
    completion: (() -> Unit)? = null
) {
    fadOutAnimation(duration) {
        setImageResource(resId)
        fadInAnimation(duration) {
            completion?.invoke()
        }
    }
}

fun View.fadOutAnimation(
    duration: Long = 300,
    visibility: Int = View.INVISIBLE,
    completion: (() -> Unit)? = null
) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.visibility = visibility
            completion?.let {
                it()
            }
        }
}

fun View.fadInAnimation(duration: Long = 300, completion: (() -> Unit)? = null) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction {
            completion?.let {
                it()
            }
        }
}

fun View.fad07Animation(duration: Long = 300, completion: (() -> Unit)? = null) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(0.7f)
        .setDuration(duration)
        .withEndAction {
            completion?.let {
                it()
            }
        }
}


fun View.alpha0(duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.alpha1(duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .alpha(1f)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.alpha07(duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .alpha(0.7f)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.translationYalpha0(y: Float, duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .alpha(0f)
        .translationY(y)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.translationYalpha1(y: Float, duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .alpha(1f)
        .translationY(y)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.translationY(y: Float, duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .translationY(y)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.translationX(x: Float, duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .translationX(x)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}

fun View.scaleXY(x: Float, y: Float, duration: Long, onEnd: () -> Unit = {}) {
    animate()
        .scaleX(x)
        .scaleY(y)
        .setDuration(duration)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
}