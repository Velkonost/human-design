package ru.get.hd.ui.settings.personal

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_start.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentPersonalInfoBinding
import ru.get.hd.databinding.FragmentSettingsBinding
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.settings.SettingsFragment
import ru.get.hd.ui.settings.SettingsViewModel
import ru.get.hd.util.ext.alpha1
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07
import ru.get.hd.vm.BaseViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

import android.animation.AnimatorInflater

import android.animation.AnimatorSet
import android.location.Address
import android.location.Geocoder
import android.view.Menu

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_personal_info.*
import kotlinx.android.synthetic.main.single_day_and_time_picker.view.*
import kotlinx.android.synthetic.main.view_place_select.view.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.event.PlaceSelectedEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.model.Place
import ru.get.hd.ui.start.StartViewModel
import ru.get.hd.ui.start.adapter.PlacesAdapter
import ru.get.hd.util.Keyboard
import ru.get.hd.util.ext.alpha0


class PersonalInfoFragment : BaseFragment<SettingsViewModel, FragmentPersonalInfoBinding>(
    ru.get.hd.R.layout.fragment_personal_info,
    SettingsViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val dateBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.dateBottomSheet.bottomSheetContainer)
    }

    private val timeBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.timeBottomSheet.bottomSheetContainer)
    }

    private lateinit var geocoder: Geocoder

    private val placesAdapter: PlacesAdapter by lazy {
        PlacesAdapter()
    }

    private var selectedLat = ""
    private var selectedLon = ""
    private var selectedTime = ""
    private var selectedDate = 0L

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        geocoder = Geocoder(requireContext(), Locale.getDefault())
        setupData()
    }

    private fun setupData() {
        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT_PERSONAL_INFO, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = baseViewModel.currentUser.date
        val dateStr = formatter.format(calendar.time)

        binding.nameET.setText(baseViewModel.currentUser.name)
        binding.placeET.setText(baseViewModel.currentUser.place)
        binding.dateET.setText(dateStr)
        binding.timeET.setText(baseViewModel.currentUser.time)

        setupPlacesView()

        val dateCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
                }
            }
        }
        dateBehavior.addBottomSheetCallback(dateCallback)
        setupDateSheet()

        val timeCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
                }
            }
        }
        timeBehavior.addBottomSheetCallback(timeCallback)
        setupTimeSheet()

    }

    private fun setupDateSheet() {
        with(binding.dateBottomSheet) {
            this.ok.setOnClickListener {
                dateBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT_PERSONAL_INFO, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()

                calendar.timeInMillis = this.date.date.time
                val dateStr = formatter.format(calendar.time)
                binding.dateET.setText(dateStr)

                selectedDate = this.date.date.time
            }

            val c = Calendar.getInstance()
            c.timeInMillis = baseViewModel.currentUser.date
            this.date.setDefaultDate(c.time)
        }
    }

    private fun setupTimeSheet() {
        with(binding.timeBottomSheet) {
            if (App.preferences.locale == "en")
                time.setIsAmPm(true)

            this.ok.setOnClickListener {
                timeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                val newTime = String.format(
                    "%02d",
                    this.time.hoursPicker.currentHour
                ) + ":" + String.format("%02d", this.time.minutesPicker.currentMinute)
                binding.timeET.setText(newTime)

                selectedTime = newTime
            }

            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, baseViewModel.currentUser.time.split(":")[0].toInt())
            c.set(Calendar.MINUTE, baseViewModel.currentUser.time.split(":")[1].toInt())
            this.time.selectDate(c)
        }
    }

    private fun setupLocale() {
        binding.done.text = App.resourcesProvider.getStringLocale(ru.get.hd.R.string.done_title)
        binding.personalInfoTitle.text = App.resourcesProvider.getStringLocale(ru.get.hd.R.string.personal_info_title)
        binding.nameTitle.text = App.resourcesProvider.getStringLocale(ru.get.hd.R.string.name_title)
        binding.placeTitle.text = App.resourcesProvider.getStringLocale(ru.get.hd.R.string.city_birth_title)
        binding.dateTitle.text = App.resourcesProvider.getStringLocale(ru.get.hd.R.string.date_birth_title)
        binding.timeTitle.text = App.resourcesProvider.getStringLocale(R.string.time_birth_title)

        binding.confirmTitle.text = App.resourcesProvider.getStringLocale(R.string.confirm_title)
        binding.confirmText.text = App.resourcesProvider.getStringLocale(R.string.confirm_text)
        binding.confirmBtnText.text = App.resourcesProvider.getStringLocale(R.string.confirm)
    }

    override fun updateThemeAndLocale() {
        setupLocale()

        binding.dateBottomSheet.container.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        binding.dateBottomSheet.ok.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.timeBottomSheet.container.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        binding.timeBottomSheet.ok.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.placesView.newPlaceET.backgroundTintList = ColorStateList.valueOf(
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

        binding.placesView.newPlaceET.backgroundTintList = ColorStateList.valueOf(
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

        binding.personalInfoContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.personalInfoTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.nameTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.dateTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.placeTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.timeTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.nameET.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.placeET.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.dateET.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.timeET.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.nameET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        ))

        binding.placeET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        ))

        binding.dateET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        ))

        binding.timeET.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkHintColor
            else R.color.lightHintColor
        ))

        binding.confirmTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.confirmText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.icCross.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) ru.get.hd.R.color.lightColor
            else R.color.darkColor
        ))

        binding.confirmCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkSettingsCard
            else R.color.lightSettingsCard
        ))

        binding.confirmBackground.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.placesView.newPlaceET.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_search_dark
            else R.drawable.bg_search_light
        )

        binding.placesView.icArrowPlace.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.placesView.icSearch.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.searchTintDark
            else R.color.searchTintLight
        ))

    }

    override fun onViewModelReady(viewModel: SettingsViewModel) {
        super.onViewModelReady(viewModel)

        viewModel.nominatimSuggestions.observe(this) {
            if (binding.placesView.isVisible) {
                val addresses: MutableList<Place> = mutableListOf()

                it.forEach { feature ->
                    if (addresses.none { it.name == feature.placeName }) {
                        addresses.add(
                            Place(
                                name = feature.placeName,
                                lat = feature.lat.toString(),
                                lon = feature.lon.toString()
                            )

                        )
                    }
                }
                placesAdapter.createList(addresses.toList())
            }
        }
    }

    private fun setupPlacesView() {
        binding.placesView.placeRecycler.adapter = placesAdapter

        binding.placesView.icArrowPlace.setOnClickListener {
            binding.placesView.isVisible = false
            binding.doneBtn.isVisible = true
            placesAdapter.createList(emptyList(), false)
        }

        binding.placesView.newPlaceET.addTextChangedListener {
            if (!binding.placesView.newPlaceET.text.isNullOrEmpty() && ::geocoder.isInitialized) {
                binding.viewModel!!.geocodingNominatim(binding.placesView.newPlaceET.text.toString())
            }
        }

//        binding.placesView.newPlaceET.addTextChangedListener {
//            if (!binding.placesView.newPlaceET.text.isNullOrEmpty() && ::geocoder.isInitialized) {
//
//                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
//                    val suggestions = getSuggestions()
//                    val addresses: MutableList<Place> = mutableListOf()
//
//                    suggestions.forEach { suggestion ->
//                        if (addresses.none { it.name == "${suggestion.locality}, ${suggestion.countryName}" }) {
//                            addresses.add(
//                                Place(
//                                    name = "${suggestion.locality}, ${suggestion.countryName}",
//                                    lat = suggestion.latitude.toString(),
//                                    lon = suggestion.longitude.toString()
//                                )
//
//                            )
//                        }
//                    }
//                    placesAdapter.createList(addresses.toList())
//                }
//            }
//        }
    }

    private fun showConfirmView() {
        binding.confirmCard.animate()
            .alpha(1f)
            .scaleY(1f)
            .scaleX(1f)
            .duration = 600
        binding.confirmBackground.animate()
            .alpha(0.5f).duration = 600
    }

    private fun hideConfirmView() {
        binding.confirmCard.animate()
            .alpha(0f)
            .scaleY(0f)
            .scaleX(0f)
            .duration = 600
        binding.confirmBackground.alpha0(600)
    }

    private fun updateUserData() {
        GlobalScope.launch(Dispatchers.Main) {
            if (binding.nameET.text.toString().replace(" ", "").isNotEmpty())
                baseViewModel.currentUser.name = binding.nameET.text.toString().replace(" ", "")

            if (!binding.placeET.text.isNullOrEmpty()) {
                if (selectedLat.isNotEmpty())
                    baseViewModel.currentUser.lat = selectedLat

                if (selectedLon.isNotEmpty())
                    baseViewModel.currentUser.lon = selectedLon

                baseViewModel.currentUser.place = binding.placeET.text.toString()
            }

        if (!binding.dateET.text.isNullOrEmpty() && selectedDate != 0L)
            baseViewModel.currentUser.date = selectedDate

            if (!binding.timeET.text.isNullOrEmpty() && selectedTime.isNotEmpty())
                baseViewModel.currentUser.time = selectedTime//binding.timeET.text.toString()


        }.invokeOnCompletion {
            baseViewModel.updateUser()
            baseViewModel.setupCurrentUser()
            router.exit()
        }
    }

    @Subscribe
    fun onPlaceSelectedEvent(e: PlaceSelectedEvent) {
        Keyboard.hide(requireActivity())

        binding.placesView.isVisible = false
        binding.placesView.newPlaceET.setText("")
        binding.doneBtn.isVisible = true
        placesAdapter.createList(emptyList(), false)

        binding.placeET.setText(e.place.name)
        selectedLat = e.place.lat
        selectedLon = e.place.lon
    }

    inner class Handler {

        fun onDoneClicked(v: View) {
            showConfirmView()
        }

        fun onBackClicked(v: View) {
            router.exit()
        }

        fun onConfirmCloseClicked(v: View) {
            hideConfirmView()
        }

        fun onConfirmClicked(v: View) {
            if (binding.confirmCard.alpha == 1f) {
                hideConfirmView()
                updateUserData()
            }
        }

        fun onPlaceClicked(v: View) {
            binding.placesView.isVisible = true
            binding.doneBtn.isVisible = false
        }

        fun onDateClicked(v: View) {
            if (timeBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                timeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            dateBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        fun onTimeClicked(v: View) {
            if (dateBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                dateBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            timeBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}