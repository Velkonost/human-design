package ru.get.hd.ui.start

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.App.Companion.LOCATION_REQUEST_CODE
import ru.get.hd.R
import ru.get.hd.databinding.FragmentStartBinding
import ru.get.hd.event.PermissionGrantedEvent
import ru.get.hd.navigation.Navigator
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.util.Keyboard
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.alpha0
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.translationY
import ru.get.hd.vm.BaseViewModel
import java.util.*

class StartFragment : BaseFragment<StartViewModel, FragmentStartBinding>(
    R.layout.fragment_start,
    StartViewModel::class,
    Handler::class
) {

    private var currentStartPage: StartPage = StartPage.RAVE

    private lateinit var geocoder: Geocoder
    private var isCurrentLocationVariantSet = false

    private var selectedLat = ""
    private var selectedLon = ""

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        geocoder = Geocoder(requireContext(), Locale.getDefault())
        prepareLogic()
        startCirclesRotation(StartPage.RAVE)

        mLocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        setupLocationListener()

        when(App.preferences.lastLoginPageId) {
            StartPage.RAVE.pageId -> setupRave()
            StartPage.NAME.pageId -> {
                animateCirclesBtwPages(1000)
                setupName()
            }
            StartPage.DATE_BIRTH.pageId -> {
                animateCirclesBtwPages(1000)
                setupDateBirth()
            }
            StartPage.TIME_BIRTH.pageId -> {
                animateCirclesBtwPages(1000)
                setupTimeBirth()
            }
            StartPage.PLACE_BIRTH.pageId -> {
                animateCirclesBtwPages(1000)
                setupPlaceBirth()
            }
            StartPage.BODYGRAPH.pageId -> {
                animateCirclesBtwPages(1000)
                setupBodygraph()
            }
            else -> setupRave()
        }

//        binding.nameET.addTextChangedListener {
//            if (!binding.nameET.text.isNullOrEmpty() && ::geocoder.isInitialized) {
//                val suggestions = geocoder.getFromLocationName(binding.nameET.text.toString(), 10)
//                Log.d("keke", "elke")
//            }
//        }
    }

    @Subscribe
    fun onPermissionGrantedEvent(e: PermissionGrantedEvent) {
        setupLocationListener()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun setupLocationListener() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationPermission()
            return
        }

        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE, mLocationListener
        )
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    private lateinit var mLocationManager: LocationManager
    private var LOCATION_REFRESH_TIME = 5000L
    private var LOCATION_REFRESH_DISTANCE = 500f

    private val mLocationListener: LocationListener = LocationListener {
        if (::geocoder.isInitialized) {
            val currentLocationVariants = geocoder.getFromLocation(it.latitude, it.longitude, 10)

            if (
                currentLocationVariants.isNotEmpty()
                && currentLocationVariants.any { variant ->
                    !variant.locality.isNullOrEmpty() && !variant.countryName.isNullOrEmpty()
                }
            ) {
                isCurrentLocationVariantSet = true

                selectedLat = currentLocationVariants[0].latitude.toString()
                selectedLon = currentLocationVariants[0].longitude.toString()
                binding.placeET.setText("${currentLocationVariants[0].locality}, ${currentLocationVariants[0].countryName}")
            }
        }
    }

    override fun updateThemeAndLocale() {
        binding.startContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.darkColor
                else ru.get.hd.R.color.lightColor
            )
        )

        binding.raveTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
                else ru.get.hd.R.color.darkColor
            )
        )

        binding.raveDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
                else ru.get.hd.R.color.darkColor
            )
        )

        binding.nameTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
                else ru.get.hd.R.color.darkColor
            )
        )

        binding.nameDesc.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
                else ru.get.hd.R.color.darkColor
            )
        )

        binding.nameET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
                else ru.get.hd.R.color.darkColor
            )
        )

        binding.nameET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) ru.get.hd.R.color.darkHintColor
                else ru.get.hd.R.color.lightHintColor
            )
        )

        binding.skipTime.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.placeET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.placeET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

    }

    private var totalTranslationYForBigCircle = 0f
    private var totalTranslationYForMidCircle = 0f
    private var stepTranslationYForBigCircle = 0f
    private var stepTranslationYForMidCircle = 0f
    private fun prepareLogic() {
        val pos = IntArray(2)
        binding.icSplashBigCircle.getLocationOnScreen(pos)

        //Get screen size
        val screenWidth: Int = requireContext().resources.displayMetrics.widthPixels
        val screenHeight: Int = requireContext().resources.displayMetrics.heightPixels

        totalTranslationYForBigCircle =
            -(screenHeight / 2f - pos[1] + binding.icSplashBigCircle.height / 2f)
        totalTranslationYForMidCircle =
            -(screenHeight / 2f - pos[1] + binding.icSplashMidCircle.height / 2f)
        stepTranslationYForBigCircle = totalTranslationYForBigCircle / 5
        stepTranslationYForMidCircle = totalTranslationYForMidCircle / 5
    }

    private fun animateCirclesBtwPages(duration: Long) {
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

            startCirclesRotation(
//                when (currentStartPage) {
//                    StartPage.RAVE -> StartPage.NAME
//                    StartPage.NAME -> StartPage.DATE_BIRTH
//                    StartPage.DATE_BIRTH -> StartPage.TIME_BIRTH
//                    StartPage.TIME_BIRTH -> StartPage.PLACE_BIRTH
//                    StartPage.PLACE_BIRTH -> StartPage.BODYGRAPH
//                    StartPage.BODYGRAPH -> StartPage.BODYGRAPH
//                }
                currentStartPage
            )
        }

    }

    private fun setupRave() {
        currentStartPage = StartPage.RAVE


        binding.raveTitle.setTextAnimation(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.rave_title))
        binding.raveDesc.setTextAnimation(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.rave_desc)) {
            binding.raveDesc.alpha = 0.7f
        }
    }

    private fun setupName() {
        currentStartPage = StartPage.NAME
        App.preferences.lastLoginPageId = StartPage.NAME.pageId

        unselectAllIndicators()
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        binding.nameContainer.isVisible = true
        binding.raveContainer.alpha0(500)
        binding.nameContainer.alpha1(500)
        binding.startBtn.translationY(requireContext().convertDpToPx(-44f), 500)
        binding.indicatorsContainer.alpha1(500)

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_name_title))
        binding.nameDesc.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_name_desc)) {
            binding.nameDesc.alpha = 0.7f
        }
        binding.nameET.hint = App.resourcesProvider.getStringLocale(R.string.start_name_hint)
    }

    private fun setupDateBirth() {
        currentStartPage = StartPage.DATE_BIRTH
        App.preferences.lastLoginPageId = StartPage.DATE_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        val c = Calendar.getInstance()
        binding.date.maxDate = c.timeInMillis
        binding.date.date = c.timeInMillis

        binding.nameET.alpha0(500) {
            binding.nameET.isVisible = false
        }
        binding.date.isVisible = true
        binding.date.alpha1(500)

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_date_title))
        binding.nameDesc.setTextAnimation(
            binding.nameET.text.toString() + App.resourcesProvider.getStringLocale(
                R.string.start_date_desc
            )
        ) {
            binding.nameDesc.alpha = 0.7f
        }
    }

    private fun setupTimeBirth() {
        currentStartPage = StartPage.TIME_BIRTH
        App.preferences.lastLoginPageId = StartPage.TIME_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_time_title))
        binding.nameDesc.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_time_desc)) {
            binding.nameDesc.alpha = 0.7f
        }
        binding.skipTime.isVisible = true
        binding.skipTime.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_time_skip))

        binding.date.alpha0(500) {
            binding.date.isVisible = false
        }
        binding.time.isVisible = true
        binding.time.alpha1(500)

    }

    private fun setupPlaceBirth() {
        currentStartPage = StartPage.PLACE_BIRTH
        App.preferences.lastLoginPageId = StartPage.PLACE_BIRTH.pageId

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_place_title))
        binding.nameDesc.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_place_desc)) {
            binding.nameDesc.alpha = 0.7f
        }

        binding.skipTime.alpha0(500) {
            binding.skipTime.isVisible = false
        }
        binding.time.alpha0(500) {
            binding.time.isVisible = false
        }

        if (!isCurrentLocationVariantSet)
            binding.placeET.hint = App.resourcesProvider.getStringLocale(R.string.start_place_hint)

        binding.placeET.isVisible = true
        binding.placeET.alpha1(500)

    }

    private fun setupBodygraph() {
        currentStartPage = StartPage.BODYGRAPH
        App.preferences.lastLoginPageId = StartPage.BODYGRAPH.pageId
        Log.d("keke", "3")
    }

    private fun unselectAllIndicators() {
        binding.indicator1.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )

        binding.indicator5.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_inactive_indicator_dark
            else R.drawable.bg_inactive_indicator_light
        )
    }

    inner class Handler {

        fun onBtnClicked(v: View) {

            when (currentStartPage) {
                StartPage.RAVE -> {
                    animateCirclesBtwPages(1000)
                    setupName()
                }
                StartPage.NAME -> {
                    if (binding.nameET.text.toString().replace(" ", "").isNullOrEmpty()) {

                    } else {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Keyboard.hide(binding.nameET)
                        }.invokeOnCompletion {
                            requireActivity().runOnUiThread {
                                animateCirclesBtwPages(1000)
                                setupDateBirth()
                            }
                        }

                    }
                }
                StartPage.DATE_BIRTH -> {
                    animateCirclesBtwPages(1000)
                    setupTimeBirth()
                }
                StartPage.TIME_BIRTH -> {
                    animateCirclesBtwPages(1000)
                    setupPlaceBirth()
                }
                StartPage.PLACE_BIRTH -> {
                    animateCirclesBtwPages(1000)

                    lifecycleScope.launch {
                        baseViewModel.createNewUser(
                            name = binding.nameET.text.toString(),
                            place = binding.placeET.text.toString(),
                            date = binding.date.date,
                            time = String.format("%02d", binding.time.hour) + ":" + String.format("%02d", binding.time.minute),
                            lat = selectedLat,
                            lon = selectedLon
                        )
                        setupBodygraph()
                    }

                }
                StartPage.BODYGRAPH -> {
                    App.preferences.lastLoginPageId = -1
                    Log.d("keke", App.preferences.lastLoginPageId.toString())
                    Navigator.startToBodygraph(this@StartFragment)
                }
            }
        }
    }

    private fun startCirclesRotation(
        page: StartPage
    ) {

//        binding.icSplashBigCircle.pivotX = binding.icSplashBigCircle.width / 2f
//        binding.icSplashBigCircle.pivotY = binding.icSplashBigCircle.height / 2f

        Log.d("keke pivotx", (binding.icSplashBigCircle.measuredWidth / 2).toString())
        Log.d("keke pivoty", (binding.icSplashBigCircle.measuredHeight / 2).toString())

//        binding.icSplashBigCircle.pivotX = (binding.icSplashBigCircle.measuredWidth / 2).toFloat()
//        binding.icSplashBigCircle.pivotY = (binding.icSplashBigCircle.measuredHeight / 2).toFloat()
//
//        binding.icSplashMidCircle.pivotX = binding.icSplashMidCircle.width.toFloat()
//        binding.icSplashMidCircle.pivotY = binding.icSplashMidCircle.height.toFloat()
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
//        rotateNegative.interpolator = LinearInterpolator()

        binding.icSplashBigCircle.startAnimation(rotate)
        binding.icSplashMidCircle.startAnimation(rotateNegative)

    }
}

enum class StartPage(val pageId: Int) {
    RAVE(5),
    NAME(6),
    DATE_BIRTH(7),
    TIME_BIRTH(8),
    PLACE_BIRTH(9),
    BODYGRAPH(10)
}