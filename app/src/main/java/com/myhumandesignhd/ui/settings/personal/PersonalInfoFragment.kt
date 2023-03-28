package com.myhumandesignhd.ui.settings.personal

import android.content.res.ColorStateList
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentPersonalInfoBinding
import com.myhumandesignhd.event.PlaceSelectedEvent
import com.myhumandesignhd.event.UpdateNavMenuVisibleStateEvent
import com.myhumandesignhd.model.Place
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.settings.SettingsViewModel
import com.myhumandesignhd.ui.start.adapter.PlacesAdapter
import com.myhumandesignhd.util.Keyboard
import com.myhumandesignhd.vm.BaseViewModel
import kotlinx.android.synthetic.main.single_day_and_time_picker.view.*
import kotlinx.android.synthetic.main.view_place_select.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class PersonalInfoFragment : BaseFragment<SettingsViewModel, FragmentPersonalInfoBinding>(
    R.layout.fragment_personal_info,
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

        if (baseViewModel.isCurrentUserInitialized()) {
            calendar.timeInMillis = baseViewModel.currentUser.date
            val dateStr = formatter.format(calendar.time)

            binding.nameET.setText(baseViewModel.currentUser.name)
            binding.placeET.setText(baseViewModel.currentUser.place)
            binding.dateET.setText(dateStr)
            binding.timeET.setText(baseViewModel.currentUser.time)

            if (App.preferences.locale == "en") {
                val sdf = SimpleDateFormat("hh:mm")
                val sdfs = SimpleDateFormat("hh:mm a")

                val dt = sdf.parse(baseViewModel.currentUser.time)
                binding.timeET.setText(dt?.let { sdfs.format(it) })
            }
        }

        setupPlacesView()

        val dateCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.blur.isVisible = false
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.blur.isVisible = true
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
                    binding.blur.isVisible = false
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.blur.isVisible = true
                    EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))
                }
            }
        }
        timeBehavior.addBottomSheetCallback(timeCallback)
        setupTimeSheet()
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onStop() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onStop()
    }

    private fun setupDateSheet() {
        with(binding.dateBottomSheet) {
            this.ok.setOnClickListener {
                dateBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                val formatter: DateFormat = SimpleDateFormat(com.myhumandesignhd.App.DATE_FORMAT_PERSONAL_INFO, Locale.getDefault())
                val calendar: Calendar = Calendar.getInstance()

                calendar.timeInMillis = this.date.date.time
                val dateStr = formatter.format(calendar.time)
                binding.dateET.setText(dateStr)

                selectedDate = this.date.date.time
            }

            val c = Calendar.getInstance()

            if (baseViewModel.isCurrentUserInitialized()) {
                c.timeInMillis = baseViewModel.currentUser.date
            }

            this.date.maxDate = Calendar.getInstance().time
            this.date.setDefaultDate(c.time)

        }
    }

    private fun setupTimeSheet() {
        with(binding.timeBottomSheet) {
            if (com.myhumandesignhd.App.preferences.locale == "en")
                time.setIsAmPm(true)
            else time.setIsAmPm(false)

            this.ok.setOnClickListener {
                timeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                var newTime = String.format(
                    "%02d",
                    this.time.hoursPicker.currentHour
                ) + ":" + String.format("%02d", this.time.minutesPicker.currentMinute)
                binding.timeET.setText(newTime)

                if (App.preferences.locale == "en") {
                    newTime =
                        String.format(
                            "%02d",
                            this.time.hoursPicker.currentHour +
                                    if (this.time.amPmPicker.isPm) 12 else 0
                        ) + ":" + String.format("%02d", this.time.minutesPicker.currentMinute)

                    val sdf = SimpleDateFormat("hh:mm")
                    val sdfs = SimpleDateFormat("hh:mm a")

                    val dt = sdf.parse(newTime)
                    binding.timeET.setText(dt?.let { sdfs.format(it) })
                }
                selectedTime = newTime
            }

            val c = Calendar.getInstance()

            if (baseViewModel.isCurrentUserInitialized()) {
                c.set(Calendar.HOUR_OF_DAY, baseViewModel.currentUser.time.split(":")[0].toInt())
                c.set(Calendar.MINUTE, baseViewModel.currentUser.time.split(":")[1].toInt())
            }

            this.time.selectDate(c)
        }
    }

    private fun setupLocale() {
        binding.done.text = App.resourcesProvider.getStringLocale(R.string.done_title)
        binding.personalInfoTitle.text = App.resourcesProvider.getStringLocale(R.string.personal_info_title)
        binding.nameTitle.text = App.resourcesProvider.getStringLocale(R.string.name_title)
        binding.placeTitle.text = App.resourcesProvider.getStringLocale(R.string.city_birth_title)
        binding.dateTitle.text = App.resourcesProvider.getStringLocale(R.string.date_birth_title)
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
            if (App.preferences.isDarkTheme) R.color.lightColor
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
            else R.color.darkColor
        ))

        binding.blur.setBackgroundColor(ContextCompat.getColor(
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

            EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(true))
        }

        binding.placesView.newPlaceET.hint = App.resourcesProvider.getStringLocale(R.string.search)
        binding.placesView.newPlaceET.addTextChangedListener {
            if (!binding.placesView.newPlaceET.text.isNullOrEmpty() && ::geocoder.isInitialized) {
                binding.viewModel!!.geocodingNominatim(binding.placesView.newPlaceET.text.toString())
            }
        }
    }

    private fun showConfirmView() {
        binding.confirmBlock.isVisible = true
        binding.confirmCard.animate()
            .alpha(1f)
            .scaleY(1f)
            .scaleX(1f)
            .setDuration(600)
            .withEndAction { binding.confirmCard.alpha = 1f }

        binding.confirmBackground.animate()
            .alpha(0.5f).duration = 600
    }

    private fun hideConfirmView() {
        binding.confirmCard.animate()
            .alpha(0f)
            .scaleY(0f)
            .scaleX(0f)
            .duration = 600

        binding.confirmBackground.animate()
            .alpha(0f).setDuration(600)
            .withEndAction { binding.confirmBlock.isVisible = false }
    }

    private fun updateUserData() {
        GlobalScope.launch(Dispatchers.Main) {
            if (binding.nameET.text.toString().replace(" ", "").isNotEmpty())
                baseViewModel.currentUser.name = binding.nameET.text.toString().trim()

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
            baseViewModel.updateUser(true)

           android.os.Handler().postDelayed({
               router.exit()
           }, 2000)

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

        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(true))
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

        fun onBlurClicked(v: View) {
//            if (dateBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
//                dateBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//
//            if (timeBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
//                timeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        fun onPlaceClicked(v: View) {
            binding.placesView.isVisible = true
            binding.doneBtn.isVisible = false

            EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(false))
        }

        fun onDateClicked(v: View) {
            if (timeBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                timeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            Keyboard.hide(binding.nameET)
            if (binding.nameET.hasFocus()) {
                binding.nameET.clearFocus()
            }

            dateBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        fun onTimeClicked(v: View) {
            if (dateBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                dateBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            Keyboard.hide(binding.nameET)
            if (binding.nameET.hasFocus()) {
                binding.nameET.clearFocus()
            }

            timeBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}