package com.myhumandesignhd.ui.transit.adapter

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenCycleItemEvent
import com.myhumandesignhd.model.Cycle
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.item_about_gates_title.view.activeGatesDesc
import kotlinx.android.synthetic.main.item_cycle.view.aboutItemText
import kotlinx.android.synthetic.main.item_cycle.view.aboutItemTitle
import kotlinx.android.synthetic.main.item_cycle.view.age
import kotlinx.android.synthetic.main.item_cycle.view.ageTitle
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemContainer
import kotlinx.android.synthetic.main.item_transit_gates_title.view.activeGatesTitle
import org.greenrobot.eventbus.EventBus

class CyclesAdapter : EpoxyAdapter() {

    fun createList(
        models: List<Cycle>,
        recyclerView: RecyclerView
    ) {
        removeAllModels()

        addModel(CycleTitleModel())
        var position = 0
        models.map {
            addModel(CycleModel(it))
            position ++
        }
        notifyDataSetChanged()
    }
}

class CycleTitleModel : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            activeGatesTitle.text =
                App.resourcesProvider.getStringLocale(R.string.transit_cycles_title)
            activeGatesDesc.text = App.resourcesProvider.getStringLocale(R.string.transit_cycles_desc)

            activeGatesTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val paint = activeGatesTitle.paint
            val width = paint.measureText(activeGatesTitle.text.toString())
            val textShader: Shader = LinearGradient(0f, 0f, width, activeGatesTitle.textSize, intArrayOf(
                Color.parseColor("#58B9FF"), Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
            ), null, Shader.TileMode.REPEAT)
            activeGatesTitle.paint.shader = textShader
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_about_gates_title
}

class CycleModel(
    private val model: Cycle
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            aboutItemTitle.text =
                when (App.preferences.locale) {
                    "ru" -> model.nameRu
                    "es" -> model.nameEs
                    else -> model.nameEn
                }

            aboutItemText.text =
                when (App.preferences.locale) {
                    "ru" -> model.descriptionRu
                    "es" -> model.descriptionEs
                    else -> model.descriptionEn
                }

            age.text =
                when (App.preferences.locale) {
                    "ru" -> model.ageRu
                    "es" -> model.ageEs
                    else -> model.ageEn
                }

            ageTitle.text = App.resourcesProvider.getStringLocale(R.string.cycle_age_title)

            aboutItemTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            aboutItemText.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            age.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            ageTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            age.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_age_dark
                else R.drawable.bg_age_light
            )

            aboutItemContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_about_item_dark
                else R.drawable.bg_about_item_light
            )

            aboutItemContainer.setOnClickListener {
                when(model.id) {
                    "SaturnReturn" -> {
                        YandexMetrica.reportEvent("Tab3CyclesSaturnTapped")
                        Amplitude.getInstance().logEvent("tab3CyclesSaturnTapped")
                    }
                    "OppositionUran" -> {
                        YandexMetrica.reportEvent("Tab3CyclesUranusTapped")
                        Amplitude.getInstance().logEvent("tab3CyclesUranusTapped")
                    }
                    "ReturnChiron" -> {
                        YandexMetrica.reportEvent("Tab3CyclesChironTapped")
                        Amplitude.getInstance().logEvent("tab3CyclesChironTapped")
                    }
                    "SecondSaturn" -> {
                        YandexMetrica.reportEvent("Tab3CyclesSecondSaturnTapped")
                        Amplitude.getInstance().logEvent("tab3CyclesSecondSaturnTapped")
                    }
                }

                EventBus.getDefault().post(OpenCycleItemEvent(model))

            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_cycle
}