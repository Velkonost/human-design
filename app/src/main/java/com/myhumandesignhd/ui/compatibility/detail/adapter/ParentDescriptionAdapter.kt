package com.myhumandesignhd.ui.compatibility.detail.adapter

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenCompatibilityAboutItemEvent
import com.myhumandesignhd.model.NewDescriptions
import kotlinx.android.synthetic.main.item_compatibility_info.view.aboutItemContainer
import kotlinx.android.synthetic.main.item_compatibility_parent_description.view.aboutItemText
import kotlinx.android.synthetic.main.item_compatibility_parent_description.view.aboutItemTitle
import kotlinx.android.synthetic.main.item_compatibility_parent_description.view.percentage
import kotlinx.android.synthetic.main.item_compatibility_parent_description.view.percentageTitle
import org.greenrobot.eventbus.EventBus

class ParentDescriptionAdapter : EpoxyAdapter() {

    fun createList(descs: ArrayList<NewDescriptions>) {
        descs.map { addModel(ParentDescriptionModel(it)) }
    }

    class ParentDescriptionModel(val model: NewDescriptions): EpoxyModel<View>() {

        private var root: View? = null

        override fun bind(view: View) {
            super.bind(view)
            root = view

            with(view) {
                aboutItemTitle.text = model.title
                percentageTitle.text = "${model.percentage}% success"
                aboutItemText.text = model.description.first().text

                percentage.progress = model.percentage

                percentage.progressDrawable = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.compatibility_progressbar_percentage_dark
                    else R.drawable.compatibility_progressbar_percentage_light
                )

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
                    )
                )

                aboutItemText.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    )
                )

                percentageTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    )
                )

                val paint = percentageTitle.paint
                val width = paint.measureText(percentageTitle.text.toString())
                val textShader: Shader = LinearGradient(
                    0f, 0f, width, percentageTitle.textSize, intArrayOf(
                        Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
                    ), null, Shader.TileMode.REPEAT
                )
                percentageTitle.paint.shader = textShader

                aboutItemContainer.setOnClickListener {
                    EventBus.getDefault().post(OpenCompatibilityAboutItemEvent(model))
                }

            }
        }

        override fun getDefaultLayout(): Int = R.layout.item_compatibility_parent_description

    }
}