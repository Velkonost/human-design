package com.myhumandesignhd.ui.base

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.myhumandesignhd.App
import com.myhumandesignhd.event.UpdateThemeEvent
import com.myhumandesignhd.glide.GlideApp
import com.myhumandesignhd.ui.settings.SettingsFragment
import com.myhumandesignhd.util.ext.getViewModel
import com.myhumandesignhd.util.lazyErrorDelegate
import dagger.android.support.DaggerFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass
import com.myhumandesignhd.BR

abstract class BaseFragment<T : ViewModel, B : ViewDataBinding>(
    private val layoutResourceId: Int,
    private val viewModelClass: KClass<T>? = null,
    private val handler: KClass<*>? = null,
    private val isSharedViewModel: Boolean = false
) : DaggerFragment(), Toolbar.OnMenuItemClickListener {

    protected lateinit var drawerArrowDrawable: DrawerArrowDrawable
    lateinit var binding: B

    private var viewModel: T
        get() = _viewModel!!
        set(value) {
            _viewModel = value
        }

    private var _viewModel: T? = null
    private var pendingAuthAction: (() -> Unit)? = null

    protected val glideRequestManager by lazy { GlideApp.with(this) }

    private var backPressedCallback: OnBackPressedCallback? = null

    protected val errorDelegate by lazyErrorDelegate { requireContext() }

    private var isDoubleClicked: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var rootView: View

    var isFirstFragmentLaunch = false

    val router by lazy {
        App.instance.router
    }

    fun pressBack() {
        router.exit()
    }

    fun navigate(screen: FragmentScreen) {
        router.navigateTo(screen)
    }

    override fun onStart() {
        updateThemeAndLocale()
        updateThemeAndLocale(withAnimation = false, withTextAnimation = false)

        registerBackPressedCallback()
        super.onStart()
        try {
            EventBus.getDefault().register(this)
        } catch (e: EventBusException) {
            Timber.i(e)
        }
        Timber.d("${this@BaseFragment.javaClass.name}::OnStart")
    }

    @Subscribe
    fun onEvent(event: Any) {
        //Dummy event subscription to prevent exceptions
    }

    open fun updateThemeAndLocale(
        withAnimation: Boolean = false,
        withTextAnimation: Boolean = false
    ) {}

    open fun updateThemeAndLocale() {

    }

    @Subscribe
    fun onUpdateThemeEvent(e: UpdateThemeEvent) {
        if (this is SettingsFragment) {
            updateThemeAndLocale(
                e.withAnimation,
                e.withTextAnimation
            )
        } else
            updateThemeAndLocale()
    }

    override fun onStop() {
        unregisterBackPressedCallback()
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        Timber.d("${this@BaseFragment.javaClass.name}::OnStop")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstFragmentLaunch = true
        Timber.d("${this@BaseFragment.javaClass.name}::onCreate")
        Timber.d("${this@BaseFragment.javaClass.name}::${savedInstanceState == null}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        Timber.d("${this@BaseFragment.javaClass.name}::${savedInstanceState == null}")
        if (viewModelClass != null) {
            _viewModel = if (isSharedViewModel) {
                requireActivity().getViewModel(
                    viewModelFactory,
                    viewModelClass.java
                )
            } else {
                getViewModel(
                    viewModelFactory,
                    viewModelClass.java
                )
            }

            binding.setVariable(BR.viewModel, viewModel)
        }

        if (handler != null) {
            try {
                binding.setVariable(BR.handler, handler.java.newInstance())
            } catch (ex: java.lang.InstantiationException) {
                binding.setVariable(
                    BR.handler,
                    handler.java.getDeclaredConstructor(this.javaClass).newInstance(this)
                )
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerBackPressedCallback()

        onLayoutReady(savedInstanceState)

        _viewModel?.let { onViewModelReady(it) }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            // TODO
        }
        return false
    }

    protected open fun onLayoutReady(savedInstanceState: Bundle?) {
        Timber.d("${this@BaseFragment.javaClass.name}::OnLayoutReady")
        Timber.d("${this@BaseFragment.javaClass.name}::${savedInstanceState == null}")

//        updateThemeAndLocale()
//        updateThemeAndLocale(withAnimation = false, withTextAnimation = false)
    }

    protected open fun onViewModelReady(viewModel: T) {
        // Empty for optional override
    }

    // optional override
    protected open fun onBackPressed() {
//        if (!router..goBack(this@BaseFragment)) {
//            requireActivity().finish()
//        }
/*        else {
            Log.d("TAG", "onBackPressed else: ")

            if (isDoubleClicked) {
                requireActivity().finish()
            }

            isDoubleClicked = true
            showSnackBar(getString(R.string.message_back_double_click))
            Handler(Looper.getMainLooper()).postDelayed({ isDoubleClicked = false }, 2000)
        }*/
    }

    private fun registerBackPressedCallback() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback!!
        )
    }

    private fun unregisterBackPressedCallback() {
        backPressedCallback?.isEnabled = false
    }

}