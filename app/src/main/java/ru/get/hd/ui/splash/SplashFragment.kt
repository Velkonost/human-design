package ru.get.hd.ui.splash

import android.animation.Animator
import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.jaeger.library.StatusBarUtil
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentSplashBinding
import ru.get.hd.event.UpdateThemeEvent
import ru.get.hd.navigation.Navigator
import ru.get.hd.ui.activity.main.MainActivity
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.util.ext.alpha0
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setImageAnimation
import ru.get.hd.util.ext.setTextAnimation
import java.util.*

import android.view.animation.LinearInterpolator

import android.view.animation.Animation

import android.view.animation.RotateAnimation
import android.widget.ImageView


class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>(
    ru.get.hd.R.layout.fragment_splash,
    SplashViewModel::class,
    Handler::class
) {

    private var currentSplashPage: SplashPage = SplashPage.SPLASH_01

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)


        setupInitTheme()
        setupLocale()

        prepareUI()
        startCirclesRotation()
        setupSplash01()
    }

    private fun prepareUI() {
        binding.splash0304Container.alpha = 0f
        binding.splash05Container.alpha = 0f

        binding.cardVariant1.alpha = 0f
        binding.cardVariant1.isVisible = false

        binding.cardVariant2.alpha = 0f
        binding.cardVariant2.isVisible = false

        binding.cardVariant3.alpha = 0f
        binding.cardVariant3.isVisible = false

        binding.cardVariant4.alpha = 0f
        binding.cardVariant4.isVisible = false

        binding.cardVariant5.alpha = 0f
        binding.cardVariant5.isVisible = false
    }

    private fun startCirclesRotation() {
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
        rotateNegative.interpolator = LinearInterpolator()

        binding.icSplashBigCircle.startAnimation(rotate)
        binding.icSplashMidCircle.startAnimation(rotateNegative)
    }

    private fun setupSplash01() {
        currentSplashPage = SplashPage.SPLASH_01

        binding.icSplash0102Header.setImageAnimation(
            resId =
            if (App.preferences.isDarkTheme) R.drawable.ic_splash_01_header_dark
            else R.drawable.ic_splash_01_header_light
        )

        binding.titleSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.title_splash_01))
        binding.descSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.desc_splash_01))
    }

    private fun setupSplash02() {
        currentSplashPage = SplashPage.SPLASH_02

        if (!App.preferences.isDarkTheme) {
            binding.icSplash0102Header.setImageAnimation(
                resId = R.drawable.ic_splash_02_header_light
            )
        }

        binding.descSplash0102.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.desc_splash_02))
    }

    private fun setupSplash03() {
        currentSplashPage = SplashPage.SPLASH_03

        binding.splash0102Container.alpha0(500) {
            binding.splash0102Container.isVisible = false
        }

        binding.splash0304Container.isVisible = true
        binding.splash0304Container.alpha1(500) {
            binding.cardVariant1.alpha1(300)
            binding.cardVariant2.alpha1(300)
            binding.cardVariant3.alpha1(300)
            binding.cardVariant4.alpha1(300)
        }

        binding.titleSplash0304.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.title_splash_03))
        binding.descSplash0304.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.desc_splash_03))

        binding.cardVariant1.isVisible = true
        binding.cardVariant2.isVisible = true
        binding.cardVariant3.isVisible = true
        binding.cardVariant4.isVisible = true

        binding.textVariant1.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_1_splash_03))
        binding.textVariant2.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_2_splash_03))
        binding.textVariant3.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_3_splash_03))
        binding.textVariant4.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.variant_4_splash_03))
    }

    private fun unselectAllVariants() {
        binding.strokeVariant1.alpha0(500)
        binding.strokeVariant2.alpha0(500)
        binding.strokeVariant3.alpha0(500)
        binding.strokeVariant4.alpha0(500)
        binding.strokeVariant5.alpha0(500)
    }

    private fun setupSplash04() {
        currentSplashPage = SplashPage.SPLASH_04
        unselectAllVariants()

        binding.titleSplash0304.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.title_splash_04))
        binding.descSplash0304.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.desc_splash_04))

        binding.textVariant1.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_1_splash_04))
        binding.textVariant2.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_2_splash_04))
        binding.textVariant3.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_3_splash_04))
        binding.textVariant4.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_4_splash_04))
        binding.textVariant5.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.variant_5_splash_04))

        binding.cardVariant5.isVisible = true
        binding.cardVariant5.alpha1(500)
    }

    private fun setupSplash05() {
        binding.splash0304Container.alpha0(500)
        binding.splash05Container.alpha1(500)

        binding.text1Splash05.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.text_1_splash_05))
        binding.text2Splash05.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.text_2_splash_05))
        binding.text3Splash05.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.text_3_splash_05))
    }

    override fun updateThemeAndLocale() {
        updateTheme()
    }

    private fun setupInitTheme() {
        if (App.preferences.isFirstLaunch) {
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
        } else {
//            EventBus.getDefault().post(
//                UpdateThemeEvent(
//                    App.preferences.isDarkTheme
//                )
//            )
        }
    }

    private fun setupLocale() {
        if (App.preferences.isFirstLaunch || App.preferences.locale.isNullOrEmpty()) {
            App.preferences.isFirstLaunch = false

            val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language
            App.preferences.locale =
                if (
                    locale == "ru"
                    || locale == "ua"
                    || locale == "kz"
                    || locale == "be"
                    || locale == "uk"
                ) "ru"
                else "en"
        }

        Locale.setDefault(Locale(App.preferences.locale ?: "ru"))
        val resources = requireActivity().resources
        val config = resources.configuration
        config.setLocale(Locale(App.preferences.locale ?: "ru"))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    inner class Handler {

        fun onBtnClicked(v: View) {
            when(currentSplashPage) {
                SplashPage.SPLASH_01 -> setupSplash02()
                SplashPage.SPLASH_02 -> setupSplash03()
                SplashPage.SPLASH_03 -> setupSplash04()
                SplashPage.SPLASH_04 -> setupSplash05()

            }
        }

        fun onVariantClicked(v: View) {
            val selectedStroke = when(v.id) {
                binding.cardVariant1.id -> binding.strokeVariant1
                binding.cardVariant2.id -> binding.strokeVariant2
                binding.cardVariant3.id -> binding.strokeVariant3
                binding.cardVariant4.id -> binding.strokeVariant4
                binding.cardVariant5.id -> binding.strokeVariant5
                else -> binding.strokeVariant1
            }

            if (selectedStroke.alpha == 0f) {
                unselectAllVariants()
                selectedStroke.alpha1(500)
            } else {
                unselectAllVariants()
            }

         }
    }
}

enum class SplashPage {
    SPLASH_01,
    SPLASH_02,
    SPLASH_03,
    SPLASH_04,
    SPLASH_05
}