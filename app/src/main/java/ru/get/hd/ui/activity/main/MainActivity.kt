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
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
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
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.App.Companion.LOCATION_REQUEST_CODE
import ru.get.hd.R
import ru.get.hd.databinding.ActivityMainBinding
import ru.get.hd.event.PermissionGrantedEvent
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

        navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController!!)
        setupNavMenu()

        binding.navView.setOnNavigationItemReselectedListener {
            if (binding.navView.selectedItemId == it.itemId) {
                val navGraph = Navigation.findNavController(this, R.id.nav_host_fragment)
                navGraph.popBackStack(it.itemId, false)

                return@setOnNavigationItemReselectedListener
            }
        }

    }

    override fun onViewModelReady(viewModel: BaseViewModel) {
        viewModel.loadFaqs()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        EventBus.getDefault().post(PermissionGrantedEvent(LOCATION_REQUEST_CODE))
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

        binding.navViewContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.darkMenu
            else R.color.lightMenu
        ))

        binding.navView.setBackgroundColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.darkMenu
            else R.color.lightMenu
        ))
    }

    private fun setupNavMenu() {
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(R.menu.bottom_nav_menu)

        binding.navViewContainer.isVisible = true
        binding.navView.isVisible = true

        binding.navView.itemIconTintList = null
    }

    inner class Handler

}