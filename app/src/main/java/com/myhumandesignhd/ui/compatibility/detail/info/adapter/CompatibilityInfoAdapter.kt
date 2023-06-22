package com.myhumandesignhd.ui.compatibility.detail.info.adapter

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_compatibility_info.view.aboutItemContainer
import kotlinx.android.synthetic.main.item_compatibility_info.view.aboutItemText
import kotlinx.android.synthetic.main.item_compatibility_info.view.aboutItemTitle
import kotlinx.android.synthetic.main.item_compatibility_info_title.view.desc

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

class CompatibilityInfoTitleModel : EpoxyModel<View>() {

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

            aboutItemContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_about_item_dark
                else R.drawable.bg_about_item_light
            )

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

            aboutItemTitle.setTextColor(
                when(position) {
                    1 -> Color.parseColor("#E45B47")
                    2 -> Color.parseColor("#58A1FE")
                    3 -> Color.parseColor("#2AC568")
                    else -> Color.parseColor("#278E68")
                }
            )
            aboutItemTitle.paint.shader = textShader
            aboutItemTitle.text = App.resourcesProvider.getStringLocale(
                when (position) {
                    1 -> R.string.compatibility_info_title_1
                    2 -> R.string.compatibility_info_title_2
                    3 -> R.string.compatibility_info_title_3
                    else -> R.string.compatibility_info_title_4
                }
            )

            aboutItemText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            aboutItemText.text = App.resourcesProvider.getStringLocale(
                when (position) {
                    1 -> R.string.compatibility_info_desc_1
                    2 -> R.string.compatibility_info_desc_2
                    3 -> R.string.compatibility_info_desc_3
                    else -> R.string.compatibility_info_desc_4
                }
            )
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_info
}