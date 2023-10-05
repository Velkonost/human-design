package com.myhumandesignhd.ui.bodygraph.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.model.Center
import kotlinx.android.synthetic.main.item_about_gates_title.view.*
import kotlinx.android.synthetic.main.item_center.view.*
import kotlinx.android.synthetic.main.item_center.view.channelCard
import kotlinx.android.synthetic.main.item_center.view.channelDesc
import kotlinx.android.synthetic.main.item_center.view.channelTitle
import kotlinx.android.synthetic.main.item_channel.view.*

class CentersAdapter : EpoxyAdapter() {

    fun createList(
        activeCenters: List<Center>,
        inactiveCenters: List<Center>,
        fromAbout: Boolean = false,
        recyclerView: RecyclerView
    ) {
        removeAllModels()

        if (fromAbout) {
            addModel(AboutCentersTitleModel(true))
        }
        var position = 0
        activeCenters.map {
            addModel(CentersModel(it, position + 1, recyclerView))
            position ++
        }

        if (fromAbout) {
            addModel(AboutCentersTitleModel(false))
        }
        inactiveCenters.map {
            addModel(CentersModel(it, position + 2, recyclerView))
            position ++
        }


        notifyDataSetChanged()
    }
}

class AboutCentersTitleModel(
    private val isActive: Boolean
): EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            activeGatesTitle.text =
                App.resourcesProvider.getStringLocale(
                    if (isActive) R.string.active_centers_title
                    else R.string.inactive_centers_title
                )
            activeGatesDesc.text = App.resourcesProvider.getStringLocale(
                if (isActive) R.string.active_centers_text
                else R.string.inactive_centers_text
            )

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

class CentersModel(
    private val model: Center,
    private val position: Int,
    private val recyclerView: RecyclerView,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            channelTitle.text = model.name
            channelDesc.text = model.description

            channelTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

            channelDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

            channelCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                ))

            centersArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            if (isExpanded) {
                channelDesc.maxLines = 70
                centersArrow
                    .animate().rotation(-90f).duration = 300
                centersArrow.alpha = 0.3f
            } else {

                channelDesc.maxLines = 3
                centersArrow
                    .animate().rotation(90f).duration = 300
                centersArrow.alpha = 1f
            }

            if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK) {
                channelDesc.maxLines = 70
                centersArrow.isVisible = false
            }

            channelCard.setOnClickListener {
                if (android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK) {
                    isExpanded = !isExpanded

                    if (isExpanded) {
                        val smoothScroller: RecyclerView.SmoothScroller =
                            object : LinearSmoothScroller(context) {
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

                        val animation = ObjectAnimator.ofInt(
                            channelDesc,
                            "maxLines",
                            70
                        )
                        animation.duration = 1000
                        animation.start()
                        centersArrow
                            .animate().rotation(-90f).duration = 300
                        centersArrow.alpha = 0.3f
                    } else {
                        val animation = ObjectAnimator.ofInt(
                            channelDesc,
                            "maxLines",
                            3
                        )
                        animation.duration = 500
                        animation.start()
                        centersArrow
                            .animate().rotation(90f).duration = 300
                        centersArrow.alpha = 1f
                    }
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_center
}