package ru.get.hd.ui.bodygraph.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_center.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.Center
import ru.get.hd.model.TransitionGate
import ru.get.hd.ui.transit.adapter.GateModel
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07

class CentersAdapter : EpoxyAdapter() {

    fun createList(models: List<Center>) {
        removeAllModels()
        models.map { addModel(CentersModel(it)) }
        notifyDataSetChanged()
    }
}

class CentersModel(
    private val model: Center
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

//        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)

        with(view) {
//            channelTitle.setTextAnimation(model.name)
//            channelDesc.setTextAnimation07(model.description)

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


        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_center
}