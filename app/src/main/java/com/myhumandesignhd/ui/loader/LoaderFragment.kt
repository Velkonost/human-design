package com.myhumandesignhd.ui.loader

import android.animation.Animator
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentLoaderBinding
import com.myhumandesignhd.event.ContinueFirstLoaderEvent
import com.myhumandesignhd.event.FinishFirstLoaderEvent
import com.myhumandesignhd.event.UpdateThemeEvent
import com.myhumandesignhd.ui.base.BaseFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoaderFragment : BaseFragment<LoaderViewModel, FragmentLoaderBinding>(
    R.layout.fragment_loader,
    LoaderViewModel::class,
    Handler::class
) {

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        setupInitTheme()

        binding.anim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                EventBus.getDefault().post(FinishFirstLoaderEvent(isFirst = true))

                binding.anim.isVisible = false

                binding.anim2.playAnimation()
                binding.anim2.isVisible = true

                binding.anim2.addAnimatorListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        android.os.Handler().postDelayed({
                            binding.anim2.clearAnimation()
                            binding.anim2.cancelAnimation()
                            EventBus.getDefault().post(FinishFirstLoaderEvent())
                        }, 350)
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {
                        android.os.Handler().postDelayed({
                            EventBus.getDefault().post(FinishFirstLoaderEvent())
                        }, 350)
                    }
                })
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        android.os.Handler().postDelayed({
            binding.anim.playAnimation()
        }, 1000)
    }

    private fun updateTheme() {
        binding.container.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_splash_dark
            else R.drawable.bg_splash_light
        )

        binding.anim.setAnimation(
            if (App.preferences.isDarkTheme) R.raw.logo_transition_black
            else R.raw.logo_transition_white
        )

        binding.anim2.setAnimation(
            if (App.preferences.isDarkTheme) R.raw.loader_white
            else R.raw.loader_black
        )

    }

    private fun setupInitTheme() {
        if (App.preferences.isFirstLaunch) {
            App.preferences.isDarkTheme =
                resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

            EventBus.getDefault().post(
                UpdateThemeEvent(
                    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> false
                        Configuration.UI_MODE_NIGHT_YES -> true
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                        else -> false
                    }
                )
            )
            updateTheme()

        } else {
            updateTheme()
//            EventBus.getDefault().post(
//                UpdateThemeEvent(
//                    App.preferences.isDarkTheme
//                )
//            )
        }
    }

    @Subscribe
    fun onContinueFirstLoaderEvent(e: ContinueFirstLoaderEvent) {
    }

    inner class Handler
}