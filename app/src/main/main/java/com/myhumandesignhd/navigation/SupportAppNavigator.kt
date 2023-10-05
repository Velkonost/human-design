package com.myhumandesignhd.navigation

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Forward
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.myhumandesignhd.R
import com.myhumandesignhd.ui.paywall.PaywallFragment

class SupportAppNavigator(activity: AppCompatActivity, @IdRes containerId: Int) :
    AppNavigator(activity, containerId) {

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        if (
            currentFragment is PaywallFragment
            || nextFragment is PaywallFragment
        ) {
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }

        super.setupFragmentTransaction(screen, fragmentTransaction, currentFragment, nextFragment)
    }

    override fun back() {
        super.back()
    }

//    override fun applyCommands(commands: Array<out Command>) {
//
////        val topFragment = fragmentManager.fragments.lastOrNull()
////        val command = commands.let { if (it.isNotEmpty()) it[it.size - 1] else null }
////        (topFragment as? PaywallFragment)?.let {
////            if (true) {
////                it.exit().subscribe { _: Boolean? ->
////                    super.applyCommands(commands)
////                }
////            } else {
////                super.applyCommands(commands)
////            }
////        } ?: let {
////            super.applyCommands(commands)
////        }
//    }
}