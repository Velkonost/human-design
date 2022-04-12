package ru.get.hd.ui.bodygraph.second.adapter

import android.content.res.ColorStateList
import android.telecom.GatewayInfo
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_bodygraph_about.view.*
import kotlinx.android.synthetic.main.item_bodygraph_centers.view.*
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.*
import kotlinx.android.synthetic.main.item_bodygraph_gates.view.*
import kotlinx.android.synthetic.main.item_center.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.AboutItem
import ru.get.hd.model.Center
import ru.get.hd.model.TransitionChannel
import ru.get.hd.model.TransitionGate
import ru.get.hd.ui.bodygraph.adapter.CentersAdapter
import ru.get.hd.ui.bodygraph.adapter.CentersModel
import ru.get.hd.ui.transit.adapter.ChannelsAdapter
import ru.get.hd.ui.transit.adapter.GatesAdapter

class ColumnsAdapter : EpoxyAdapter() {

    fun createList(
        activeCenters: List<Center>,
        inactiveCenters: List<Center>,
        gates: List<TransitionGate>,
        channels: List<TransitionChannel>,
        aboutItems: List<AboutItem>
    ) {
        removeAllModels()

        addModel(AboutModel(aboutItems))
        addModel(CentersModel(activeCenters, inactiveCenters))
        addModel(GatesModel(gates))
        addModel(ChannelsModel(channels))

        notifyDataSetChanged()
    }
}

class AboutModel(
    private val aboutItems: List<AboutItem>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val aboutAdapter = AboutAdapter(context, aboutRecycler, aboutItems)
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

            activeCentersTitle.text = App.resourcesProvider.getStringLocale(R.string.active_centers_title)
            activeCentersDesc.text = App.resourcesProvider.getStringLocale(R.string.active_centers_text)
            inactiveCentersTitle.text = App.resourcesProvider.getStringLocale(R.string.inactive_centers_title)
            inactiveCentersDesc.text = App.resourcesProvider.getStringLocale(R.string.inactive_centers_text)


            activeCentersTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            inactiveCentersTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            activeCentersDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            inactiveCentersDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            val activeCentersAdapter = CentersAdapter()
            val inactiveCentersAdapter = CentersAdapter()

            view.activeCentersRecycler.adapter = activeCentersAdapter
            view.inactiveCentersRecycler.adapter = inactiveCentersAdapter

            activeCentersAdapter.createList(activeCenters)
            inactiveCentersAdapter.createList(inactiveCenters)

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

//                                view.activeGatesTitle.setTextAnimation(App.resourcesProvider.getStringLocale(R.string.active_gates_title))
//                    view.activeGatesDesc.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.active_gates_text))
                    activeGatesTitle.text = App.resourcesProvider.getStringLocale(R.string.active_gates_title)
                    activeGatesDesc.text = App.resourcesProvider.getStringLocale(R.string.active_gates_text)

                    activeGatesTitle.setTextColor(ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                    activeGatesDesc.setTextColor(ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

            val gatesAdapter = GatesAdapter()
            view.gatesRecycler.adapter = gatesAdapter

            gatesAdapter.createList(gates)

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
            activeChannelTitle.text = App.resourcesProvider.getStringLocale(R.string.active_channels_title)
            activeChannelDesc.text = App.resourcesProvider.getStringLocale(R.string.active_channels_text)

            activeChannelTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            activeChannelDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            val channelsAdapter = ChannelsAdapter()
            view.channelsRecycler.adapter = channelsAdapter
            channelsAdapter.createList(channels)

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_bodygraph_channels
}