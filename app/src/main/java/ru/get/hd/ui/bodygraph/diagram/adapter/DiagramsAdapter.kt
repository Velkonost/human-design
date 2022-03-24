package ru.get.hd.ui.bodygraph.diagram.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.media.metrics.Event
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_diagram.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.UpdateCurrentUserEvent
import ru.get.hd.model.Center
import ru.get.hd.model.TransitionChannel
import ru.get.hd.model.TransitionGate
import ru.get.hd.model.User
import ru.get.hd.ui.bodygraph.second.adapter.ChannelsModel
import ru.get.hd.ui.bodygraph.second.adapter.GatesModel

class DiagramsAdapter : EpoxyAdapter() {

    fun createList(
       users: List<User>
    ) {
        removeAllModels()
        users.map { addModel(DiagramModel(it)) }

        notifyDataSetChanged()
    }
}

class DiagramModel(
    private val model: User
) : EpoxyModel<View>() {

    private var root: View? = null

    @SuppressLint("SetTextI18n")
    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            userName.text = model.name
            subtitle.text =
                "${if (App.preferences.locale == "ru") model.subtitle1Ru else model.subtitle1En} • " +
                        "${model.subtitle2} • " +
                        "${if (App.preferences.locale == "ru") model.subtitle3Ru else model.subtitle3En}"

            userName.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            subtitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            diagramCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            if (App.preferences.currentUserId == model.id) {
                diagramContainer.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
                    else R.drawable.gradient_variant_card_selected_light
                )
            }

            diagramCard.setOnClickListener {
                if (App.preferences.currentUserId != model.id) {
                    EventBus.getDefault().post(UpdateCurrentUserEvent(model.id))
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_diagram
}