package ru.get.hd.navigation

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Forward
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.get.hd.R

class SupportAppNavigator(activity: AppCompatActivity, @IdRes containerId: Int) :
    AppNavigator(activity, containerId) {

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
//        fragmentTransaction.setCustomAnimations(
//            R.anim.enter_from_right,
//            R.anim.exit_to_left,
//            R.anim.enter_from_left,
//            R.anim.exit_to_right
//        )
        super.setupFragmentTransaction(screen, fragmentTransaction, currentFragment, nextFragment)
    }

    override fun applyCommand(command: Command) {
        super.applyCommand(command)
    }
    override fun forward(command: Forward) {
        super.forward(command)

    }

}