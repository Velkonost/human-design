package ru.get.hd.ui.bodygraph.diagram

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.databinding.FragmentDiagramBinding
import ru.get.hd.event.CurrentUserLoadedEvent
import ru.get.hd.event.UpdateCurrentUserEvent
import ru.get.hd.event.UpdateNavMenuVisibleStateEvent
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.bodygraph.BodygraphViewModel
import ru.get.hd.ui.bodygraph.diagram.adapter.DiagramsAdapter
import ru.get.hd.util.ext.setUpRemoveItemTouchHelper
import ru.get.hd.vm.BaseViewModel

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
        EventBus.getDefault().post(UpdateNavMenuVisibleStateEvent(isVisible = false))

        binding.diagramsRecycler.setUpRemoveItemTouchHelper(
            ::onItemSwiped
        )
    }

    private fun onItemSwiped(vh: RecyclerView.ViewHolder, swipeDirection: Int) {
        val swipedPosition = vh.absoluteAdapterPosition

        baseViewModel.deleteUser(diagramsAdapter.getUserAtPosition(swipedPosition).id)
        diagramsAdapter.deleteUser(swipedPosition)

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

    @Subscribe
    fun onUpdateCurrentUserEvent(e: UpdateCurrentUserEvent) {
        App.preferences.currentUserId = e.userId
        baseViewModel.setupCurrentUser()
    }

    @Subscribe
    fun onCurrentUserLoadedEvent(e: CurrentUserLoadedEvent) {
        if (isAdded) router.exit()
    }

    private fun setupData() {
        binding.diagramsRecycler.adapter = diagramsAdapter

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val users = baseViewModel.getAllUsers()

            diagramsAdapter.createList(users)
            binding.emptyText.isVisible = users.size <= 1
        }

    }

    inner class Handler {

        fun onBackClicked(v: View) {
            router.exit()
        }

        fun onAddClicked(v: View) {
            router.navigateTo(Screens.addUserScreen(
                fromDiagram = true
            ))
        }

    }
}