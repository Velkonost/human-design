package com.myhumandesignhd.ui.bodygraph.diagram

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.databinding.FragmentDiagramBinding
import com.myhumandesignhd.event.CurrentUserLoadedEvent
import com.myhumandesignhd.event.DeleteDiagramItemEvent
import com.myhumandesignhd.event.DiagramAddUserClickEvent
import com.myhumandesignhd.event.UpdateCurrentUserEvent
import com.myhumandesignhd.navigation.Screens
import com.myhumandesignhd.ui.base.BaseFragment
import com.myhumandesignhd.ui.bodygraph.BodygraphViewModel
import com.myhumandesignhd.ui.bodygraph.diagram.adapter.DiagramsAdapter
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import io.sulek.ssml.SSMLLinearLayoutManager
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe

class DiagramFragment : BaseFragment<BodygraphViewModel, FragmentDiagramBinding>(
    R.layout.fragment_diagram,
    BodygraphViewModel::class,
    Handler::class
) {

    private val baseViewModel: BaseViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            BaseViewModel::class.java
        )
    }

    private val diagramsAdapter: DiagramsAdapter by lazy {
        DiagramsAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupData()

        (binding.diagramsRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.diagramsRecycler.itemAnimator = null
        binding.diagramsRecycler.layoutManager = SSMLLinearLayoutManager(requireContext())
    }

    @Subscribe
    fun onDeleteDiagramItemEvent(e: DeleteDiagramItemEvent) {
        baseViewModel.deleteUser(e.userId,
            diagramsAdapter.getUserAtPosition(
                diagramsAdapter.getNextPosition(
                    diagramsAdapter.getPositionByUserId(e.userId)
                )
            ).id
        )
        diagramsAdapter.deleteUserById(e.userId)
    }

    override fun updateThemeAndLocale() {
        binding.diagramContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        binding.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.icPlus.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.diagramTitle.text = App.resourcesProvider.getStringLocale(R.string.diagram_title)
        binding.diagramTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        binding.emptyText.text = App.resourcesProvider.getStringLocale(R.string.diagram_empty_text)
        binding.emptyText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
    }

    private var isChangedUserClicked = false
    @Subscribe
    fun onUpdateCurrentUserEvent(e: UpdateCurrentUserEvent) {
        App.preferences.currentUserId = e.userId
        isChangedUserClicked = true
        baseViewModel.setupCurrentUser()
    }

    @Subscribe
    fun onCurrentUserLoadedEvent(e: CurrentUserLoadedEvent) {
        if (isAdded) {
            setupData()

            App.preferences.isInvokeNewTransits = true
            App.preferences.isInvokeNewDescription = true
            App.preferences.isInvokeNewCompatibility = true
            App.preferences.isInvokeNewInsights = true

            App.isBodygraphWithAnimationShown = false

            if (isChangedUserClicked) {
                isChangedUserClicked = false
                router.exit()
            }
        }
    }

    @Subscribe
    fun onDiagramAddUserClickEvent(e: DiagramAddUserClickEvent) {
        YandexMetrica.reportEvent("Tab1AddUserTappedStart1")

        router.navigateTo(
            Screens.addUserScreen(
            fromDiagram = true
        ))
    }

    private fun setupData() {
        binding.diagramsRecycler.adapter = diagramsAdapter

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val users = baseViewModel.getAllUsers()

            diagramsAdapter.createList(users)
           // binding.emptyText.isVisible = users.size <= 1
        }

    }

    inner class Handler {

        fun onBackClicked(v: View) {
            router.exit()
        }

        fun onAddClicked(v: View) {
            router.navigateTo(
                Screens.addUserScreen(
                fromDiagram = true
            ))
        }

    }
}