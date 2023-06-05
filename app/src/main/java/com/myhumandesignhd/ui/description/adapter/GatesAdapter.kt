package com.myhumandesignhd.ui.description.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenCenterItemEvent
import com.myhumandesignhd.event.OpenChannelItemEvent
import com.myhumandesignhd.event.OpenGateItemEvent
import com.myhumandesignhd.model.Center
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemContainer
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemText
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemTitle
import kotlinx.android.synthetic.main.item_description_gate_item.view.gateNumber
import org.greenrobot.eventbus.EventBus

class GatesAdapter : EpoxyAdapter() {

    fun createList(items: List<TransitionGate>) {
        items.map { addModel(GateModel(it)) }
    }

    fun createListChannels(items: List<TransitionChannel>) {
        items.map { addModel(ChannelModel(it)) }
    }

    fun createListCenters(items: List<Center>) {
        items.map { addModel(CenterModel(it)) }
    }

    class GateModel(private val item: TransitionGate) : EpoxyModel<View>() {

        private var root: View? = null

        override fun bind(view: View) {
            super.bind(view)
            root = view

            with(view) {
                aboutItemContainer.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.bg_about_item_dark
                    else R.drawable.bg_about_item_light
                )

                aboutItemTitle.setTextColor(
                    ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                aboutItemText.setTextColor(
                    ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                gateNumber.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.bg_gate_number_dark
                    else R.drawable.bg_gate_number_light
                )

                aboutItemTitle.text = item.title
                aboutItemText.text = item.description
                gateNumber.text = item.number
//
                aboutItemContainer.setOnClickListener {
                    EventBus.getDefault().post(OpenGateItemEvent(item))
                }
            }
        }

        override fun getDefaultLayout(): Int = R.layout.item_description_gate_item
    }

    class ChannelModel(
        private val item: TransitionChannel
    ) : EpoxyModel<View>() {

        private var root: View? = null

        override fun bind(view: View) {
            super.bind(view)
            root = view

            with(view) {
                aboutItemContainer.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.bg_about_item_dark
                    else R.drawable.bg_about_item_light
                )

                aboutItemTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                aboutItemText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                gateNumber.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.bg_gate_number_dark
                    else R.drawable.bg_gate_number_light
                )

                aboutItemTitle.text = item.title
                aboutItemText.text = item.description
                gateNumber.text = item.number

                aboutItemContainer.setOnClickListener {
                    EventBus.getDefault().post(OpenChannelItemEvent(item))
                }
            }
        }

        override fun getDefaultLayout(): Int = R.layout.item_description_gate_item
    }


    internal class CenterModel(
        private val item: Center
    ) : EpoxyModel<View>() {

        private var root: View? = null

        override fun bind(view: View) {
            super.bind(view)
            root = view

            with(view) {
                aboutItemContainer.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.bg_about_item_dark
                    else R.drawable.bg_about_item_light
                )

                aboutItemTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                aboutItemText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    ))

                gateNumber.isVisible = false

                aboutItemTitle.text = item.name
                aboutItemText.text = item.description

                aboutItemContainer.setOnClickListener {
                    EventBus.getDefault().post(OpenCenterItemEvent(item))
                }
            }
        }

        override fun getDefaultLayout(): Int = R.layout.item_description_gate_item
    }
}