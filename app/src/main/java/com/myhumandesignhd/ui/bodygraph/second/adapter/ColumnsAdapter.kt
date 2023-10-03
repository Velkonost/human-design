package com.myhumandesignhd.ui.bodygraph.second.adapter

import android.view.View
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.R
import com.myhumandesignhd.model.AboutItem
import com.myhumandesignhd.model.Center
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate
import com.myhumandesignhd.ui.bodygraph.adapter.CentersAdapter
import com.myhumandesignhd.ui.transit.adapter.ChannelsAdapter
import com.myhumandesignhd.ui.transit.adapter.GatesAdapter
import com.myhumandesignhd.vm.BaseViewModel
import kotlinx.android.synthetic.main.item_bodygraph_about.view.aboutRecycler
import kotlinx.android.synthetic.main.item_bodygraph_centers.view.inactiveCentersRecycler
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.channelsRecycler
import kotlinx.android.synthetic.main.item_bodygraph_gates.view.gatesRecycler


class ColumnsAdapter : EpoxyAdapter() {

    fun createList(
        activeCenters: List<Center>,
        inactiveCenters: List<Center>,
        gates: List<TransitionGate>,
        channels: List<TransitionChannel>,
        aboutItems: List<AboutItem>,
        injuryStatus: BaseViewModel.InjuryStatus,
        injuryPercent: Int? = null,
    ) {
        removeAllModels()

        addModel(AboutModel(aboutItems, injuryStatus, injuryPercent))
        addModel(CentersModel(activeCenters, inactiveCenters))
        addModel(GatesModel(gates))
        addModel(ChannelsModel(channels))

        notifyDataSetChanged()
    }
}

class AboutModel(
    private val aboutItems: List<AboutItem>,
    private val injuryStatus: BaseViewModel.InjuryStatus,
    private val injuryPercent: Int? = null,
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val aboutAdapter = AboutAdapter(
                context,
                aboutRecycler,
                aboutItems,
                injuryStatus, injuryPercent
            )
            aboutRecycler.adapter = aboutAdapter

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_bodygraph_about
}

class CentersModel(
    private val activeCenters: List<Center>,
    private val inactiveCenters: List<Center>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {

            val centersAdapter = CentersAdapter()

            view.inactiveCentersRecycler.adapter = centersAdapter

            centersAdapter.createList(
                activeCenters, inactiveCenters,
                true, view.inactiveCentersRecycler
            )

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_bodygraph_centers
}

class GatesModel(
    private val gates: List<TransitionGate>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val gatesAdapter = GatesAdapter()
            view.gatesRecycler.adapter = gatesAdapter

            gatesAdapter.createList(gates, view.gatesRecycler, true)

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_bodygraph_gates
}

class ChannelsModel(
    private val channels: List<TransitionChannel>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val channelsAdapter = ChannelsAdapter()
            view.channelsRecycler.adapter = channelsAdapter
            channelsAdapter.createList(channels, view.channelsRecycler, true)

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_bodygraph_channels
}