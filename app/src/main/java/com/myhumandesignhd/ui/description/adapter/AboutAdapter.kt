package com.myhumandesignhd.ui.description.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenAboutItemEvent
import com.myhumandesignhd.model.AboutItem
import kotlinx.android.synthetic.main.item_description_about_item.view.aboutItemContainer
import kotlinx.android.synthetic.main.item_description_about_item.view.aboutItemSubtitle
import kotlinx.android.synthetic.main.item_description_about_item.view.aboutItemText
import kotlinx.android.synthetic.main.item_description_about_item.view.aboutItemTitle
import org.greenrobot.eventbus.EventBus

class AboutAdapter : EpoxyAdapter() {

    fun createList(items: List<AboutItem>) {
        items.map { addModel(AboutItemModel(it)) }
    }

    internal class AboutItemModel(
        private val item: AboutItem
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

                aboutItemTitle.setTextColor(ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                aboutItemSubtitle.setTextColor(ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                aboutItemText.setTextColor(ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

                aboutItemTitle.text = item.title
                aboutItemSubtitle.text = item.subtitle
                aboutItemText.text = item.text

                aboutItemContainer.setOnClickListener {
                    EventBus.getDefault().post(OpenAboutItemEvent(item))
                }
            }
        }

        override fun getDefaultLayout(): Int = R.layout.item_description_about_item
    }
}