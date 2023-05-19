package com.myhumandesignhd.ui.description.adapter

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.model.AboutItem
import com.myhumandesignhd.model.Center
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate
import com.myhumandesignhd.model.User
import com.yandex.metrica.impl.ob.T
import kotlinx.android.synthetic.main.item_about_gates_title.view.*
import kotlinx.android.synthetic.main.item_description_about.view.*


class DescriptionAdapter : EpoxyAdapter() {
    fun createList(
        activeCenters: List<Center>,
        inactiveCenters: List<Center>,
        gates: List<TransitionGate>,
        channels: List<TransitionChannel>,
        aboutItems: List<AboutItem>,
        currentUser: User
    ) {
        removeAllModels()

        addModel(AboutModel(TABS.ABOUT, aboutItems, currentUser))
        addModel(AboutModel(TABS.CENTERS, activeCenters, currentUser, inactiveCenters))
        addModel(AboutModel(TABS.GATES, gates, currentUser))
        addModel(AboutModel(TABS.CHANNELS, channels, currentUser))

        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    internal class AboutModel<T>(
        private val tabType: TABS,
        private val aboutItems: List<T>,
        private val currentUser: User,
        private val aboutItems2: List<T>? = null
    ) : EpoxyModel<View>() {
        private var root: View? = null

        override fun bind(view: View) {
            super.bind(view)
            root = view

            with(view) {
                aboutSubtitle.setTextColor(ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                aboutSubtitle2.setTextColor(ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                when(tabType) {
                    TABS.ABOUT -> {
                        aboutTitle.isVisible = false
                        aboutSubtitle.isVisible = false
                        aboutTitle2.isVisible = false
                        aboutSubtitle2.isVisible = false

                        val aboutAdapter = AboutAdapter()
                        aboutRecycler.adapter = aboutAdapter
                        aboutAdapter.createList(aboutItems as List<AboutItem>)
                    }
                    TABS.GATES -> {
                        aboutTitle.isVisible = true
                        aboutSubtitle.isVisible = true
                        aboutTitle2.isVisible = false
                        aboutSubtitle2.isVisible = false

                        aboutTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.active_gates_title)
                        aboutSubtitle.text = App.resourcesProvider.getStringLocale(R.string.active_gates_text)

                        val gatesAdapter = GatesAdapter()
                        aboutRecycler.adapter = gatesAdapter
                        gatesAdapter.createList(aboutItems as List<TransitionGate>)
                    }
                    TABS.CHANNELS -> {
                        aboutTitle.isVisible = true
                        aboutSubtitle.isVisible = true
                        aboutTitle2.isVisible = false
                        aboutSubtitle2.isVisible = false

                        aboutTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.active_channels_title)
                        aboutSubtitle.text = App.resourcesProvider.getStringLocale(R.string.active_channels_text)

                        val gatesAdapter = GatesAdapter()
                        aboutRecycler.adapter = gatesAdapter
                        gatesAdapter.createListChannels(aboutItems as List<TransitionChannel>)
                    }
                    TABS.CENTERS -> {
                        aboutTitle.isVisible = true
                        aboutSubtitle.isVisible = true
                        aboutTitle2.isVisible = true
                        aboutSubtitle2.isVisible = true

                        aboutTitle.text =
                            App.resourcesProvider.getStringLocale(
                                R.string.active_centers_title
                            )
                        aboutSubtitle.text = App.resourcesProvider.getStringLocale(
                            R.string.active_centers_text
                        )

                        aboutTitle2.text =
                            App.resourcesProvider.getStringLocale(
                                R.string.inactive_centers_title
                            )
                        aboutSubtitle2.text = App.resourcesProvider.getStringLocale(
                            R.string.inactive_centers_text
                        )

                        val paint = aboutTitle2.paint
                        val width = paint.measureText(aboutTitle2.text.toString())
                        val textShader: Shader = LinearGradient(0f, 0f, width, aboutTitle2.textSize, intArrayOf(
                            Color.parseColor("#58B9FF"), Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
                        ), null, Shader.TileMode.REPEAT)
                        aboutTitle2.paint.shader = textShader

                        val activeCentersAdapter = GatesAdapter()
                        aboutRecycler.adapter = activeCentersAdapter
                        activeCentersAdapter.createListCenters(aboutItems as List<Center>)

                        val inactiveCentersAdapter = GatesAdapter()
                        aboutRecycler2.adapter = inactiveCentersAdapter
                        inactiveCentersAdapter.createListCenters(aboutItems2 as List<Center>)
                    }
                }

                val paint = aboutTitle.paint
                val width = paint.measureText(aboutTitle.text.toString())
                val textShader: Shader = LinearGradient(0f, 0f, width, aboutTitle.textSize, intArrayOf(
                    Color.parseColor("#58B9FF"), Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
                ), null, Shader.TileMode.REPEAT)
                aboutTitle.paint.shader = textShader

            }
        }

        override fun getDefaultLayout(): Int = R.layout.item_description_about
    }

    internal enum class TABS {
        ABOUT, CENTERS, GATES, CHANNELS
    }
}