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

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupData()
    }

    private fun setupData() {
        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT_PERSONAL_INFO, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = baseViewModel.currentUser.date
        val dateStr = formatter.format(calendar.time)

        binding.nameET.setTextAnimation(baseViewModel.currentUser.name)
        binding.placeET.setTextAnimation(baseViewModel.currentUser.place)
        binding.dateET.setTextAnimation(dateStr)
        binding.timeET.setTextAnimation(baseViewModel.currentUser.time)
    }

    private fun setupLocale() {
        binding.done.setTextAnimation(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.done_title))
        binding.personalInfoTitle.setTextAnimation(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.personal_info_title))
        binding.nameTitle.setTextAnimation07(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.name_title))
        binding.placeTitle.setTextAnimation07(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.city_birth_title))
        binding.dateTitle.setTextAnimation07(App.resourcesProvider.getStringLocale(ru.get.hd.R.string.date_birth_title))
        binding.timeTitle.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.time_birth_title))

        binding.confirmTitle.text = App.resourcesProvider.getStringLocale(R.string.confirm_title)
        binding.confirmText.text = App.resourcesProvider.getStringLocale(R.string.confirm_text)
        binding.confirmBtn.text = App.resourcesProvider.getStringLocale(R.string.confirm)
    }

    override fun updateThemeAndLocale() {
        setupLocale()

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

        binding.done.setTextColor(ContextCompat.getColor(
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

        binding.confirmBtn.setTextColor(ContextCompat.getColor(
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
            else R.color.lightColor
        ))

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
        GlobalScope.launch {
            if (!binding.nameET.text.isNullOrEmpty())
                baseViewModel.currentUser.name = binding.nameET.text.toString()

            if (!binding.placeET.text.isNullOrEmpty())
                baseViewModel.currentUser.place = binding.placeET.text.toString()

//        if (!binding.dateET.text.isNullOrEmpty())
//            baseViewModel.currentUser.date = binding.dateET.text.toString()

            if (!binding.timeET.text.isNullOrEmpty())
                baseViewModel.currentUser.time = binding.timeET.text.toString()

            baseViewModel.updateUser()
        }.invokeOnCompletion { baseViewModel.setupCurrentUser() }
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
            hideConfirmView()
            updateUserData()
        }
    }
}