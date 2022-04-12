package ru.get.hd.ui.bodygraph.diagram.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_diagram.view.*
import kotlinx.android.synthetic.main.item_partner_empty.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.AddPartnerClickEvent
import ru.get.hd.event.DiagramAddUserClickEvent
import ru.get.hd.event.UpdateCurrentUserEvent
import ru.get.hd.model.User
import java.util.*

class DiagramsAdapter : EpoxyAdapter() {

    fun createList(
        users: List<User>
    ) {
        removeAllModels()
        users.map { addModel(DiagramModel(it)) }

        addModel(DiagramEmptyModel())
        notifyDataSetChanged()
    }

    fun getUserAtPosition(position: Int): User {
        return (models[position] as DiagramModel).model
    }

    fun deleteUser(position: Int) {
        removeModel(models[position])
    }
}

class DiagramModel(
    val model: User
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

            userName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            subtitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            diagramCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )

            if (App.preferences.currentUserId == model.id) {
                diagramContainer.background = ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.gradient_variant_card_selected_dark
                    else R.drawable.gradient_variant_card_selected_light
                )
            }

            chart.setImageResource(
                if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "проектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_dark
                    else R.drawable.ic_chart_proektor_light
                } else if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "рефлектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_dark
                    else R.drawable.ic_chart_reflector_light
                } else if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_dark
                    else R.drawable.ic_chart_generator_light
                } else if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_dark
                    else R.drawable.ic_chart_mangenerator_light
                } else {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_dark
                    else R.drawable.ic_chart_manifestor_light
                }
            )

            diagramCard.setOnClickListener {
                if (App.preferences.currentUserId != model.id) {
                    EventBus.getDefault().post(UpdateCurrentUserEvent(model.id))
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_diagram
}

class DiagramEmptyModel : EpoxyModel<View>() {

    private var root: View? = null

    @SuppressLint("SetTextI18n")
    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            icPlus.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            emptyPartnerCard.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_empty_partner_light
                else R.drawable.bg_empty_partner_dark
            )

            emptyPartnerCard.setOnClickListener {
                EventBus.getDefault().post(DiagramAddUserClickEvent())
            }

            partnersEmptyText.isVisible = true
            partnersEmptyText.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.diagram_empty_text))
            partnersEmptyText.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner_empty
}