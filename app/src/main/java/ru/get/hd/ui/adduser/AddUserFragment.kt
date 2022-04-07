package ru.get.hd.ui.adduser

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.view_place_select.view.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentAddUserBinding
import ru.get.hd.databinding.FragmentDiagramBinding
import ru.get.hd.event.PermissionGrantedEvent
import ru.get.hd.event.PlaceSelectedEvent
import ru.get.hd.event.UpdateCurrentUserEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.model.Place
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel
import ru.get.hd.ui.bodygraph.diagram.DiagramFragment
import ru.get.hd.ui.start.StartPage
import ru.get.hd.ui.start.StartViewModel
import ru.get.hd.ui.start.adapter.PlacesAdapter
import ru.get.hd.util.Keyboard
import ru.get.hd.util.convertDpToPx
import ru.get.hd.util.ext.alpha0
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07
import ru.get.hd.util.ext.translationY
import ru.get.hd.vm.BaseViewModel
import java.util.*

class AddUserFragment : BaseFragment<StartViewModel, FragmentAddUserBinding>(
    R.layout.fragment_add_user,
    StartViewModel::class,
    Handler::class
) {

    var currentStartPage: StartPage = StartPage.RAVE

    private lateinit var geocoder: Geocoder
    private var isCurrentLocationVariantSet = false

    private var selectedLat = ""
    private var selectedLon = ""

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val fromCompatibility: Boolean by lazy {
        arguments?.getBoolean("fromCompatibility", false)?: false
    }

    private val isChild: Boolean by lazy {
        arguments?.getBoolean("isChild", false)?: false
    }

    private val placesAdapter: PlacesAdapter by lazy {
        PlacesAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))

        geocoder = Geocoder(requireContext(), Locale.getDefault())
        prepareLogic()
        startCirclesRotation(StartPage.RAVE)

        mLocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setupLocationListener()

        setupRave()
        setupPlacesView()
    }

    @Subscribe
    fun onPermissionGrantedEvent(e: PermissionGrantedEvent) {
        setupLocationListener()
    }

    lateinit var mLocationManager: LocationManager
    var LOCATION_REFRESH_TIME = 5000L
    var LOCATION_REFRESH_DISTANCE = 500f

    val mLocationListener: LocationListener = LocationListener {
        if (::geocoder.isInitialized) {
            kotlin.runCatching {
                val currentLocationVariants =
                    geocoder.getFromLocation(it.latitude, it.longitude, 10)

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

        binding.placesView.newPlaceET.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )
        )

        binding.placesView.newPlaceET.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkHintColor
                else R.color.lightHintColor
            )
        )

        binding.placesView.placesViewContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )


        binding.bodygraphReadyTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.bodygraphReadyText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

    }

    private fun setupPlacesView() {
        binding.placesView.placeRecycler.adapter = placesAdapter

        binding.placesView.icArrow.setOnClickListener {
            binding.placesView.isVisible = false
            placesAdapter.createList(emptyList())
        }

        binding.placesView.newPlaceET.addTextChangedListener {
            if (!binding.placesView.newPlaceET.text.isNullOrEmpty() && ::geocoder.isInitialized) {


                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    val suggestions = getSuggestions()
                    val addresses: MutableList<Place> = mutableListOf()

                    suggestions.forEach { suggestion ->
                        if (addresses.none { it.name == "${suggestion.locality}, ${suggestion.countryName}" }) {
                            addresses.add(
                                Place(
                                    name = "${suggestion.locality}, ${suggestion.countryName}",
                                    lat = suggestion.latitude.toString(),
                                    lon = suggestion.longitude.toString()
                                )

                            )
                        }
                    }
                    placesAdapter.createList(addresses.toList())
                }
            }
        }
    }

    private suspend fun getSuggestions(): List<Address> =
        coroutineScope {
            withContext(Dispatchers.IO) {
                geocoder.getFromLocationName(binding.placesView.newPlaceET.text.toString(), 300)
            }
        }

    @Subscribe
    fun onPlaceSelectedEvent(e: PlaceSelectedEvent) {
        Keyboard.hide(requireActivity())

        binding.placesView.isVisible = false
        binding.placesView.newPlaceET.setText("")
        placesAdapter.createList(emptyList())

        binding.placeET.setText(e.place.name)
        selectedLat = e.place.lat
        selectedLon = e.place.lon
    }

    private var totalTranslationYForBigCircle = 0f
    private var totalTranslationYForMidCircle = 0f
    var stepTranslationYForBigCircle = 0f
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

    private fun setupRave() {
        currentStartPage = StartPage.RAVE

        when {
            isChild -> {
                binding.raveTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_rave_title))
                binding.raveDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_rave_desc))
            }
            fromCompatibility -> {
                binding.raveTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_rave_title))
                binding.raveDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_rave_desc))
            }
            else -> {
                binding.raveTitle.setTextAnimation(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.diagram_rave_title))
                binding.raveDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.diagram_rave_desc))
            }
        }
    }

    private fun setupName() {
        currentStartPage = StartPage.NAME

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

        when {
            isChild -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_name_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_name_desc))
            }
            fromCompatibility -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_name_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_name_desc))
            }
            else -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_name_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_name_desc))
            }
        }

        binding.nameET.hint = App.resourcesProvider.getStringLocale(R.string.start_name_hint)
    }

    private fun setupDateBirth() {
        currentStartPage = StartPage.DATE_BIRTH

        unselectAllIndicators()
        binding.indicator2.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        val c = Calendar.getInstance()
        binding.date.date = c.timeInMillis - 631139040000

        binding.nameET.alpha0(500) {
            binding.nameET.isVisible = false
        }
        binding.date.isVisible = true
        binding.date.alpha1(500)

        when {
            isChild -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_date_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_date_birth_desc))
            }
            fromCompatibility -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_date_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_date_birth_desc))
            }
            else -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_date_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_date_desc))
            }
        }

    }

    private fun setupTimeBirth() {
        currentStartPage = StartPage.TIME_BIRTH

        unselectAllIndicators()
        binding.indicator3.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        when {
            isChild -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_time_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_time_birth_desc))
            }
            fromCompatibility -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_time_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_time_birth_desc))
            }
            else -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_time_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_time_desc))
            }
        }

        binding.skipTime.isVisible = true
        binding.skipTime.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.start_time_skip))

        binding.date.alpha0(500) {
            binding.date.isVisible = false
        }

        binding.time.hour = 12
        binding.time.minute = 0

        binding.time.isVisible = true
        binding.time.alpha1(500)

    }

    private fun setupPlaceBirth() {
        currentStartPage = StartPage.PLACE_BIRTH

        unselectAllIndicators()
        binding.indicator4.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_active_indicator_dark
            else R.drawable.bg_active_indicator_light
        )

        when {
            isChild -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_place_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_place_birth_desc))
            }
            fromCompatibility -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_place_birth_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_place_birth_desc))
            }
            else -> {
                binding.nameTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_place_title))
                binding.nameDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_place_desc))
            }
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

        binding.indicatorsContainer.alpha0(500)
        binding.startBtn.alpha0(500) {
            binding.startBtn.isVisible = false
        }
        binding.nameContainer.alpha0(500)

        binding.bodygraphContainer.isVisible = true
        binding.bodygraphContainer.alpha1(500)

        when {
            isChild -> {
                binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_child_bodygraph_ready_title))
                binding.bodygraphReadyText.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_child_bodygraph_ready_desc))
            }
            fromCompatibility -> {
                binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.add_partner_bodygraph_ready_title))
                binding.bodygraphReadyText.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.add_partner_bodygraph_ready_desc))
            }
            else -> {
                binding.bodygraphReadyTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.diagram_bodygraph_title))
                binding.bodygraphReadyText.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.diagram_bodygraph_desc))
            }
        }

        when {
            isChild -> {
                baseViewModel.currentChildBodygraph.observe(viewLifecycleOwner) {
                    binding.bodygraphView.setupData(
                        it.design,
                        it.personality,
                        it.activeCentres,
                        it.inactiveCentres
                    )
                }
            }
            fromCompatibility -> {
                baseViewModel.currentPartnerBodygraph.observe(viewLifecycleOwner) {
                    binding.bodygraphView.setupData(
                        it.design,
                        it.personality,
                        it.activeCentres,
                        it.inactiveCentres
                    )
                }
            }
            else -> {
                baseViewModel.currentBodygraph.observe(viewLifecycleOwner) {
                    binding.bodygraphView.setupData(
                        it.design,
                        it.personality,
                        it.activeCentres,
                        it.inactiveCentres
                    )
                }
            }
        }
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

        fun openPlacesView(v: View) {
            binding.placesView.isVisible = true
        }

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
                        if (isChild) {
                            baseViewModel.createNewChild(
                                name = binding.nameET.text.toString(),
                                place = binding.placeET.text.toString(),
                                date = binding.date.date,
                                time = String.format(
                                    "%02d",
                                    binding.time.hour
                                ) + ":" + String.format("%02d", binding.time.minute),
                                lat = selectedLat,
                                lon = selectedLon
                            )
                        } else {
                            baseViewModel.createNewUser(
                                name = binding.nameET.text.toString(),
                                place = binding.placeET.text.toString(),
                                date = binding.date.date,
                                time = String.format(
                                    "%02d",
                                    binding.time.hour
                                ) + ":" + String.format("%02d", binding.time.minute),
                                lat = selectedLat,
                                lon = selectedLon,
                                fromCompatibility = fromCompatibility
                            )
                        }
                        setupBodygraph()
                    }

                }
                StartPage.BODYGRAPH -> {
                    App.preferences.lastLoginPageId = -1

                    when {
                        isChild -> {
                            router.navigateTo(Screens.compatibilityScreen())
                        }
                        fromCompatibility -> {
                            router.navigateTo(Screens.compatibilityScreen())
                        }
                        else -> {
                            router.navigateTo(Screens.bodygraphScreen())
                        }
                    }
                }
            }
        }
    }

}