package ru.get.hd.ui.compatibility.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_diagram.view.*
import kotlinx.android.synthetic.main.item_partner.view.*
import kotlinx.android.synthetic.main.item_partner.view.chart
import kotlinx.android.synthetic.main.item_partner.view.subtitle
import kotlinx.android.synthetic.main.item_partner.view.userName
import kotlinx.android.synthetic.main.item_partner_empty.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.AddChildClickEvent
import ru.get.hd.event.CompatibilityChildStartClickEvent
import ru.get.hd.model.Child
import ru.get.hd.model.User

class ChildrenAdapter : EpoxyAdapter() {

    fun createList(
        children: List<Child>
    ) {
        removeAllModels()
        children.map { addModel(ChildModel(it)) }
        addModel(EmptyChildrenModel())

        notifyDataSetChanged()
    }

    fun getChildAtPosition(position: Int): Child {
        return (models[position] as ChildModel).model
    }

    fun deleteChild(position: Int) {
        removeModel(models[position])
    }
}

class ChildModel(
    val model: Child
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
                ))

            subtitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

            partnerCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                ))

            chart.setImageResource(
                if (model.subtitle1Ru?.toLowerCase() == "проектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_child_dark
                    else R.drawable.ic_chart_proektor_child_light
                } else if (model.subtitle1Ru?.toLowerCase() == "рефлектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_child_dark
                    else R.drawable.ic_chart_reflector_child_light
                } else if (model.subtitle1Ru?.toLowerCase() == "генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_child_dark
                    else R.drawable.ic_chart_generator_child_light
                } else if (model.subtitle1Ru?.toLowerCase() == "манифестирующий генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_child_dark
                    else R.drawable.ic_chart_mangenerator_child_light
                } else {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_child_dark
                    else R.drawable.ic_chart_manifestor_child_light
                }
            )

            partnerCard.setOnClickListener {
                EventBus.getDefault().post(
                    CompatibilityChildStartClickEvent(childId = model.id)
                )
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner
}

class EmptyChildrenModel : EpoxyModel<View>() {

    private var root: View? = null

    @SuppressLint("SetTextI18n")
    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            icPlus.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
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
                EventBus.getDefault().post(AddChildClickEvent())
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner_empty
}
