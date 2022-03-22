package ru.get.hd.navigation

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import ru.get.hd.R
import ru.get.hd.ui.NavigationResult
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.settings.SettingsFragment
import ru.get.hd.ui.splash.SplashFragment
import ru.get.hd.ui.start.StartFragment
import kotlin.properties.Delegates

object Navigator {

    fun goBack(f: Fragment) = f.findNavController().popBackStack()

    fun navigateBackWithResult(f: Fragment, result: Bundle) {
        val childFragmentManager =
            f.requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager
        var backStackListener: FragmentManager.OnBackStackChangedListener by Delegates.notNull()
        backStackListener = FragmentManager.OnBackStackChangedListener {
            (childFragmentManager?.fragments?.get(0) as NavigationResult).onNavigationResult(result)
            childFragmentManager.removeOnBackStackChangedListener(backStackListener)
        }
        childFragmentManager?.addOnBackStackChangedListener(backStackListener)
        goBack(f)
    }

    fun refresh(f: Fragment) {
        val destId = f.findNavController().currentDestination!!.id


        f.findNavController().popBackStack()
        f.findNavController().navigate(destId, null)
    }

    fun splashToStart(f: SplashFragment) {
        f.findNavController().navigate(R.id.action_navigation_splash_to_navigation_start)
    }

    fun settingsToFaq(f: SettingsFragment) {
        f.findNavController().navigate(R.id.action_navigation_settings_to_navigation_faq)
    }

    fun faqToFaqDetail(
        f: FaqFragment,
        title: String,
        desc: String
    ) {
        f.findNavController().navigate(
            R.id.action_navigation_faq_to_navigation_faq_detail,
            bundleOf(
                "title" to title,
                "desc" to desc
            )
        )
    }

    fun startToBodygraph(f: StartFragment) {
        f.findNavController().navigate(R.id.action_navigation_start_to_navigation_bodygraph)
    }

//    fun splashToAuth(f: Fragment) {
//        val options = NavOptions.Builder()
//            .setLaunchSingleTop(true)
//            .setEnterAnim(R.anim.open_from_top)
//            .setExitAnim(R.anim.activity_close_translate_to_bottom)
//            .setPopEnterAnim(R.anim.open_from_top)
//            .setPopExitAnim(R.anim.activity_close_translate_to_bottom)
//            .setPopUpTo(f.findNavController().graph.startDestination, false)
//            .build()
//
//        f.findNavController().navigate(
//            R.id.action_navigation_splash_to_navigation_auth,
//            null, options
//        )
//    }


}