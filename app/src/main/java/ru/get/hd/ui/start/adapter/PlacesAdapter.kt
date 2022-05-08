package ru.get.hd.ui.start.adapter

import android.text.Html
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_faq.view.*
import kotlinx.android.synthetic.main.item_place.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.FaqClickedEvent
import ru.get.hd.event.PlaceSelectedEvent
import ru.get.hd.model.Faq
import ru.get.hd.model.Place
import ru.get.hd.ui.faq.adapter.FaqModel

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