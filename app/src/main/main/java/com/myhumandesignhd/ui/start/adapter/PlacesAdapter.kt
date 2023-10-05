package com.myhumandesignhd.ui.start.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.PlaceSelectedEvent
import com.myhumandesignhd.model.Place
import kotlinx.android.synthetic.main.item_place.view.*
import org.greenrobot.eventbus.EventBus

class PlacesAdapter : EpoxyAdapter() {

    fun createList(models: List<Place>, showEmptyModel: Boolean = true) {
        removeAllModels()
        models.map { addModel(PlacesModel(it)) }

        if (models.isEmpty() && showEmptyModel) {
            addModel(EmptyPlaceModel())
        }

        notifyDataSetChanged()
    }
}

class PlacesModel(
    private val model: Place
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

//        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)

        with(view) {
            place.text = model.name

            place.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            placeContainer.setOnClickListener {
                EventBus.getDefault().post(PlaceSelectedEvent(model))
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_place
}

class EmptyPlaceModel : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

//        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)

        with(view) {
            place.text = App.resourcesProvider.getStringLocale(R.string.places_empty_text)

            place.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_place
}