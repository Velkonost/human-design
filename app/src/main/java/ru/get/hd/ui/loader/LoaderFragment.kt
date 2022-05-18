package ru.get.hd.ui.loader

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentFaqBinding
import ru.get.hd.databinding.FragmentLoaderBinding
import ru.get.hd.event.FinishFirstLoaderEvent
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.faq.FaqViewModel

class LoaderFragment : BaseFragment<LoaderViewModel, FragmentLoaderBinding>(
    ru.get.hd.R.layout.fragment_loader,
    LoaderViewModel::class,
    Handler::class
) {

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        binding.container.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_splash_dark
            else R.drawable.bg_splash_05_light
        )

        binding.anim.setAnimation(
            if (App.preferences.isDarkTheme) R.raw.logo_transition_black
            else R.raw.logo_transition_white
        )

//        binding.anim.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
//                super.onAnimationStart(animation, isReverse)
//            }
//
//            override fun onAnimationStart(animation: Animator?) {}
//
//            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
//                super.onAnimationEnd(animation, isReverse)
//            }
//
//            override fun onAnimationEnd(animation: Animator?) {
//
//            }
//
//            override fun onAnimationCancel(animation: Animator?) {
//
//            }
//
//            override fun onAnimationRepeat(animation: Animator?) {
//                binding.anim.pauseAnimation()
//                EventBus.getDefault().post(FinishFirstLoaderEvent())
//            }
//        })


        binding.anim.addAnimatorUpdateListener {
            it.doOnEnd {
                EventBus.getDefault().post(FinishFirstLoaderEvent())
            }
        }

        android.os.Handler().postDelayed({
            binding.anim.playAnimation()
        }, 1000)

    }

//    @Subscribe
//    fun onFinishFirstLoaderEvent(e: FinishFirstLoaderEvent) {
//
//    }

    inner class Handler {

    }
}