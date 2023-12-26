package com.myhumandesignhd.ui.transit.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.model.Cycle
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.item_about_gates_title.view.activeGatesDesc
import kotlinx.android.synthetic.main.item_about_gates_title.view.activeGatesTitle
import kotlinx.android.synthetic.main.item_cycle.view.cycleAge
import kotlinx.android.synthetic.main.item_cycle.view.cycleAgeTitle
import kotlinx.android.synthetic.main.item_cycle.view.cycleArrow
import kotlinx.android.synthetic.main.item_cycle.view.cycleCard
import kotlinx.android.synthetic.main.item_cycle.view.cycleDesc
import kotlinx.android.synthetic.main.item_cycle.view.cycleTitle

class CyclesAdapter : EpoxyAdapter() {

    fun createList(
        models: List<Cycle>,
        recyclerView: RecyclerView
    ) {
        removeAllModels()

        addModel(CycleTitleModel())
        var position = 0
        models.map {
            addModel(CycleModel(it, position + 1, recyclerView))
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

            activeGatesDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_about_gates_title
}

class CycleModel(
    private val model: Cycle,
    private val position: Int,
    private val recyclerView: RecyclerView,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            cycleTitle.text = model.name

            cycleDesc.text = model.description

            cycleAge.text = model.age

            cycleAgeTitle.text = App.resourcesProvider.getStringLocale(R.string.cycle_age_title)

            cycleTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleAge.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleAgeTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            if (isExpanded) {
                cycleDesc.maxLines = 140
                cycleArrow
                    .animate().rotation(-90f).duration = 300
                cycleArrow.alpha = 0.3f
            } else {
                cycleDesc.maxLines = 3
                cycleArrow
                    .animate().rotation(90f).duration = 300
                cycleArrow.alpha = 1f
            }

            cycleCard.setOnClickListener {
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

                isExpanded = !isExpanded

                if (isExpanded) {
                    val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_START
                        }

                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return 0.5f//3000f / recyclerView.computeVerticalScrollRange()
                        }

                    }
                    smoothScroller.targetPosition = position
                    (recyclerView.layoutManager as LinearLayoutManager)
                        .startSmoothScroll(smoothScroller)

                    cycleDesc.isVisible = true
                    val animation = ObjectAnimator.ofInt(
                        cycleDesc,
                        "maxLines",
                        140
                    )
                    animation.duration = 1000
                    animation.start()
                    cycleArrow
                        .animate().rotation(-90f).duration = 300
                    cycleArrow.alpha = 0.3f
                } else {
                    val animation = ObjectAnimator.ofInt(
                        cycleDesc,
                        "maxLines",
                        0
                    )
                    animation.duration = 500
                    animation.start()
                    animation.doOnEnd {
                        cycleDesc.isVisible = false
                    }
                    cycleArrow
                        .animate().rotation(90f).duration = 300
                    cycleArrow.alpha = 1f


                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_cycle
}