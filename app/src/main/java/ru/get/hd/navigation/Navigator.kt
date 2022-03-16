package ru.get.hd.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import ru.get.hd.R
import ru.get.hd.ui.NavigationResult
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