package com.myhumandesignhd.ui.compatibility.detail.info.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.util.DisplayMetrics
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_channel.view.channelArrow
import kotlinx.android.synthetic.main.item_channel.view.channelDesc
import kotlinx.android.synthetic.main.item_compatibility_channel.view.*
import kotlinx.android.synthetic.main.item_compatibility_info.view.*
import kotlinx.android.synthetic.main.item_compatibility_info_title.view.*

class CompatibilityInfoAdapter : EpoxyAdapter() {

    fun createList(
        recyclerView: RecyclerView
    ) {
        removeAllModels()

        addModel(CompatibilityInfoTitleModel())
        addModel(CompatibilityInfoModel(1, recyclerView))
        addModel(CompatibilityInfoModel(2, recyclerView))
        addModel(CompatibilityInfoModel(3, recyclerView))
        addModel(CompatibilityInfoModel(4, recyclerView))

        notifyDataSetChanged()
    }
}

class CompatibilityInfoTitleModel(

): EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            desc.text = App.resourcesProvider.getStringLocale(R.string.compatibility_information_desc)
            desc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_info_title
}


class CompatibilityInfoModel(
    private val position: Int,
    private val recyclerView: RecyclerView,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {

            compatibilityInfoCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            compatibilityInfoArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            val textShader: Shader = LinearGradient(
                0f, 0f, 0f, 20f,
                when (position) {
                    1 -> intArrayOf(Color.parseColor("#CE7559"), Color.parseColor("#BD5252"))
                    2 -> intArrayOf(Color.parseColor("#5290BD"), Color.parseColor("#6A59CE"))
                    3 -> intArrayOf(Color.parseColor("#46A59F"), Color.parseColor("#117D43"))
                    else -> intArrayOf(Color.parseColor("#7D2793"), Color.parseColor("#4E128A"))
                },
                floatArrayOf(0f, 1f),
                TileMode.CLAMP
            )

            compatibilityInfoTitle.setTextColor(
                when(position) {
                    1 -> Color.parseColor("#C66456")
                    2 -> Color.parseColor("#5D77C5")
                    3 -> Color.parseColor("#278E68")
                    else -> Color.parseColor("#278E68")
                }
            )
            compatibilityInfoTitle.paint.shader = textShader
            compatibilityInfoTitle.text = App.resourcesProvider.getStringLocale(
                when (position) {
                    1 -> R.string.compatibility_info_title_1
                    2 -> R.string.compatibility_info_title_2
                    3 -> R.string.compatibility_info_title_3
                    else -> R.string.compatibility_info_title_4
                }
            )

            compatibilityInfoDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            compatibilityInfoDesc.text = App.resourcesProvider.getStringLocale(
                when (position) {
                    1 -> R.string.compatibility_info_desc_1
                    2 -> R.string.compatibility_info_desc_2
                    3 -> R.string.compatibility_info_desc_3
                    else -> R.string.compatibility_info_desc_4
                }
            )

            if (isExpanded) {
                compatibilityInfoDesc.maxLines = 70
                compatibilityInfoArrow
                    .animate().rotation(-90f).duration = 300
                compatibilityInfoArrow.alpha = 0.3f
            } else {

                compatibilityInfoDesc.maxLines = 3
                compatibilityInfoArrow
                    .animate().rotation(90f).duration = 300
                compatibilityInfoArrow.alpha = 1f
            }

            compatibilityInfoCard.setOnClickListener {
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

                    val animation = ObjectAnimator.ofInt(
                        compatibilityInfoDesc,
                        "maxLines",
                        30
                    )
                    animation.duration = 1000
                    animation.start()
                    compatibilityInfoArrow
                        .animate().rotation(-90f).duration = 300
                    compatibilityInfoArrow.alpha = 0.3f

                } else {
                    val animation = ObjectAnimator.ofInt(
                        compatibilityInfoDesc,
                        "maxLines",
                        3
                    )
                    animation.duration = 500
                    animation.start()
                    compatibilityInfoArrow
                        .animate().rotation(90f).duration = 300
                    compatibilityInfoArrow.alpha = 1f
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_info
}