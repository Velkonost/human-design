package com.myhumandesignhd.ui.compatibility.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.AddChildClickEvent
import com.myhumandesignhd.event.CompatibilityChildStartClickEvent
import com.myhumandesignhd.event.DeleteChildItemEvent
import com.myhumandesignhd.model.Child
import com.zerobranch.layout.SwipeLayout
import kotlinx.android.synthetic.main.item_diagram.view.*
import kotlinx.android.synthetic.main.item_partner.view.*
import kotlinx.android.synthetic.main.item_partner.view.chart
import kotlinx.android.synthetic.main.item_partner.view.deleteBtn
import kotlinx.android.synthetic.main.item_partner.view.icDelete
import kotlinx.android.synthetic.main.item_partner.view.subtitle
import kotlinx.android.synthetic.main.item_partner.view.swipeContainer
import kotlinx.android.synthetic.main.item_partner.view.userName
import kotlinx.android.synthetic.main.item_partner_empty.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class ChildrenAdapter : EpoxyAdapter() {

    fun createList(
        children: List<Child>
    ) {
        removeAllModels()
        children.map { addModel(ChildModel(it)) }
        addModel(EmptyChildrenModel(children.isNullOrEmpty()))

        notifyDataSetChanged()
    }

    fun getChildAtPosition(position: Int): Child {
        return (models[position] as ChildModel).model
    }

    fun deleteChild(position: Int) {
        removeModel(models[position])
    }

    fun deleteChildById(childId: Long) {
        models.forEach { model ->
            if (
                model is ChildModel
                && model.model.id == childId
            ) {
                hideModel(model)

                if (models.count { it.isShown } == 1) {
                    val emptyModel = (models.findLast { it is EmptyChildrenModel } as EmptyChildrenModel)
                    emptyModel.showEmptyText = true

                    notifyModelChanged(emptyModel)
                }
                return@forEach
            }
        }
    }
}

class ChildModel(
    val model: Child,
    private var isExpanded: Boolean = false,
    private val isSwipeEnabled: Boolean = true
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
                if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "проектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_child_dark
                    else R.drawable.ic_chart_proektor_child_light
                } else if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "рефлектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_child_dark
                    else R.drawable.ic_chart_reflector_child_light
                } else if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_child_dark
                    else R.drawable.ic_chart_generator_child_light
                } else if (model.subtitle1Ru?.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_mangenerator_child_dark
                    else R.drawable.ic_chart_mangenerator_child_light
                } else {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_manifestor_child_dark
                    else R.drawable.ic_chart_manifestor_child_light
                }
            )

            icDelete.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_dark_close
                else R.drawable.ic_light_close
            )

            deleteBtn.setOnClickListener {
                EventBus.getDefault().post(DeleteChildItemEvent(model.id))
            }

            partnerCard.setOnClickListener {

                EventBus.getDefault().post(
                    CompatibilityChildStartClickEvent(childId = model.id)
                )
            }

            swipeContainer.isEnabledSwipe = isSwipeEnabled
            swipeContainer.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    if (isContinuous) {
                        EventBus.getDefault().post(DeleteChildItemEvent(model.id))
                    }
                }

                override fun onClose() {

                }
            })
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner
}

class EmptyChildrenModel(
    var showEmptyText: Boolean = false
) : EpoxyModel<View>() {

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

            partnersEmptyText.isVisible = showEmptyText
            partnersEmptyText.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.children_empty_text))
            partnersEmptyText.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner_empty
}
