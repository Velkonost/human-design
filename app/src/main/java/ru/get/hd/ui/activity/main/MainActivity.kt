package ru.get.hd.ui.activity.main

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.jaeger.library.StatusBarUtil
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.ActivityMainBinding
import ru.get.hd.ui.base.BaseActivity
import ru.get.hd.vm.*
import java.util.*

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
) {

    private var navController: NavController? = null


    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
//        StatusBarUtil.setTransparent(this)
//
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController!!)


        binding.navView.setOnNavigationItemReselectedListener {
            if (binding.navView.selectedItemId == it.itemId) {
                val navGraph = Navigation.findNavController(this, R.id.nav_host_fragment)
                navGraph.popBackStack(it.itemId, false)

                return@setOnNavigationItemReselectedListener
            }
        }

    }

    override fun updateThemeAndLocale() {
        StatusBarUtil.setColor(
            this, resources.getColor(
                if (App.preferences.isDarkTheme) R.color.darkColor
                else R.color.lightColor
            )
        )

        window.navigationBarColor = resources.getColor(
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        )
    }

    override fun onViewModelReady(viewModel: BaseViewModel) {
        super.onViewModelReady(viewModel)


    }

    private fun setupNavMenu(msg: String) {
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(R.menu.bottom_nav_menu)

        binding.navViewContainer.isVisible = true
        binding.navView.isVisible = true

        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.open_from_top)
            .setExitAnim(R.anim.activity_close_translate_to_bottom)
            .setPopEnterAnim(R.anim.open_from_top)
            .setPopExitAnim(R.anim.activity_close_translate_to_bottom)
            .setPopUpTo(navController!!.graph.startDestination, false)
            .build()

//        binding.navView.menu.getItem(0).setOnMenuItemClickListener {
//            navController!!.navigate(R.id.navigation_metric, null, options)
//            return@setOnMenuItemClickListener true
//        }
//
//        binding.navView.menu.getItem(1).setOnMenuItemClickListener {
//            navController!!.navigate(R.id.navigation_diary, null, options)
//            return@setOnMenuItemClickListener true
//        }
//
//        binding.navView.menu.getItem(2).setOnMenuItemClickListener {
//            showSelectNoteTypeView()
//            return@setOnMenuItemClickListener true
//        }
//
//        binding.navView.menu.getItem(3).setOnMenuItemClickListener {
//            navController!!.navigate(R.id.navigation_achievements, null, options)
//            return@setOnMenuItemClickListener true
//        }
//
//        binding.navView.menu.getItem(4).setOnMenuItemClickListener {
//            navController!!.navigate(R.id.navigation_settings, null, options)
//            return@setOnMenuItemClickListener true
//        }


    }

    inner class Handler

}