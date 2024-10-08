package com.myhumandesignhd.ui.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myhumandesignhd.event.UpdateThemeEvent
import com.myhumandesignhd.glide.GlideApp
import com.myhumandesignhd.util.ext.getViewModel
import dagger.android.support.DaggerAppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass
import com.myhumandesignhd.BR

abstract class BaseActivity<V : ViewModel, B : ViewDataBinding>(
    private val layoutResourceId: Int?,
    private val viewModelClass: KClass<V>? = null,
    private val handler: KClass<*>? = null
) : DaggerAppCompatActivity(), Toolbar.OnMenuItemClickListener {

    protected lateinit var drawerArrowDrawable: DrawerArrowDrawable
    protected val nullableBinding: B? get() = _binding
    var binding: B
        get() = _binding!!
        set(value) {
            _binding = value
        }

    val glideRequestManager by lazy { GlideApp.with(this) }

    protected val nullableViewModel: V? get() = _viewModel
    protected var viewModel: V
        get() = _viewModel!!
        set(value) {
            _viewModel = value
        }

    private var _binding: B? = null
    private var _viewModel: V? = null
    private var savedInstanceState: Bundle? = null
    private var isBindingInitialized = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var toolbarTextColor: Int = 0

    private var isDoubleClicked: Boolean = false

    /**
     * Overriding classes should use onLayoutReady or onViewModelReady instead of onCreate to ensure app is fully initialized
     */
    final override fun onCreate(savedInstanceState: Bundle?) {
//        getWindow().setBackgroundDrawableResource(
//            if (App.preferences.isDarkTheme) R.drawable.ic_splash_dark
//            else R.drawable.ic_splash_light
//        );

        Timber.e("onCreate")

        savedInstanceState?.clear()
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, layoutResourceId!!)
        this.isBindingInitialized = false
        this.savedInstanceState = savedInstanceState

        onLayoutReady(savedInstanceState)
        proceedWithOnCreate(savedInstanceState)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    private fun proceedWithOnCreate(savedInstanceState: Bundle?) {
        synchronized(this) {
            Timber.e("proceedWithOnCreate")
            this.savedInstanceState = null

            if (!isBindingInitialized) {
                Timber.e("initializing binding")
                isBindingInitialized = true

                if (viewModelClass != null) {
                    _viewModel = getViewModel(viewModelFactory, viewModelClass.java)
                    binding.setVariable(BR.viewModel, viewModel)
                }

                if (handler != null) {
                    try {
                        binding.setVariable(BR.handler, handler.java.newInstance())
                    } catch (ex: InstantiationException) {
                        binding.setVariable(
                            BR.handler,
                            handler.java.getDeclaredConstructor(this.javaClass).newInstance(this)
                        )
                    }
                }

                if (viewModelClass != null) {
                    binding.lifecycleOwner = this
                }

                _viewModel?.let { onViewModelReady(it) }
            }
        }
    }

    protected open fun onNewIntentReady(intent: Intent?) {
        // Empty for optional override
    }

    open fun updateThemeAndLocale() {

    }

    @Subscribe
    fun onUpdateThemeEvent(e: UpdateThemeEvent) {
        updateThemeAndLocale()
    }

    protected open fun onLayoutReady(savedInstanceState: Bundle?) {
        updateThemeAndLocale()
        // Empty for optional override
    }

    protected open fun onViewModelReady(viewModel: V) {
        // Empty for optional override
    }

    override fun onStart() {
        Timber.e("onStart")
        super.onStart()

        try {
            EventBus.getDefault().register(this)
        } catch (e: EventBusException) {
            Timber.i(e)
        }
    }

    override fun onStop() {
        Timber.e("onStop")
        super.onStop()

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onDestroy() {
        Timber.e("onDestroy")
        super.onDestroy()
    }


    fun showDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    fun showLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
