package com.myhumandesignhd.ui.transit.adapter

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
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.model.TransitionGate
import kotlinx.android.synthetic.main.item_about_gates_title.view.activeGatesDesc
import kotlinx.android.synthetic.main.item_about_gates_title.view.activeGatesTitle
import kotlinx.android.synthetic.main.item_channel.view.channelArrow
import kotlinx.android.synthetic.main.item_channel.view.channelCard
import kotlinx.android.synthetic.main.item_channel.view.channelDesc
import kotlinx.android.synthetic.main.item_channel.view.channelTitle
import kotlinx.android.synthetic.main.item_channel.view.number

class GatesAdapter : EpoxyAdapter() {

    fun createList(
        models: List<TransitionGate>,
        recyclerView: RecyclerView,
        fromAbout: Boolean = false
    ) {
        removeAllModels()

        if (fromAbout) {
            addModel(AboutGatesTitleModel())
        }

        var position = 0
        models.map {
            addModel(GateModel(it, position + 1, recyclerView))
            position++
        }
        notifyDataSetChanged()
    }
}

class AboutGatesTitleModel : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            activeGatesTitle.text =
                App.resourcesProvider.getStringLocale(R.string.active_gates_title)
            activeGatesDesc.text = App.resourcesProvider.getStringLocale(R.string.active_gates_text)

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

class GateModel(
    private val model: TransitionGate,
    private val position: Int,
    private val recyclerView: RecyclerView,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            channelTitle.text = model.title
            channelDesc.text = model.description
            number.text = model.number

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

            number.setTextColor(
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

            number.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_channel_number_dark
                else R.drawable.bg_channel_number_light
            )

            channelArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            if (isExpanded) {
                channelDesc.maxLines = 70
                channelArrow
                    .animate().rotation(-90f).duration = 300
                channelArrow.alpha = 0.3f
            } else {

                channelDesc.maxLines = 3
                channelArrow
                    .animate().rotation(90f).duration = 300
                channelArrow.alpha = 1f
            }

            if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK) {
                channelDesc.maxLines = 70
                channelArrow.isVisible = false
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

//                                override fun calculateDtToFit(
//                                    viewStart: Int,
//                                    viewEnd: Int,
//                                    boxStart: Int,
//                                    boxEnd: Int,
//                                    snapPreference: Int
//                                ): Int {
//                                    return boxStart// - viewStart
////                                    (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
//                                }
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
                        channelArrow
                            .animate().rotation(-90f).duration = 300
                        channelArrow.alpha = 0.3f
                    } else {
                        val animation = ObjectAnimator.ofInt(
                            channelDesc,
                            "maxLines",
                            3
                        )
                        animation.duration = 500
                        animation.start()
                        channelArrow
                            .animate().rotation(90f).duration = 300
                        channelArrow.alpha = 1f
                    }
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_channel
}