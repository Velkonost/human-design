package com.myhumandesignhd.ui.bodygraph.diagram.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Html
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.DeleteDiagramItemEvent
import com.myhumandesignhd.event.DiagramAddUserClickEvent
import com.myhumandesignhd.event.DiagramSetSwipingModelEvent
import com.myhumandesignhd.event.UpdateCurrentUserEvent
import com.myhumandesignhd.model.User
import com.zerobranch.layout.SwipeLayout
import kotlinx.android.synthetic.main.item_diagram.view.*
import kotlinx.android.synthetic.main.item_partner_empty.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class DiagramsAdapter : EpoxyAdapter() {

    private var swipingModelId: Long? = null

    fun createList(
        users: List<User>
    ) {
        removeAllModels()
        users.map {
            addModel(
                DiagramModel(
                    it,
                    isSwipeEnabled =

                    if (swipingModelId != null) {
                        it.id == swipingModelId
                    } else users.size > 1
                )
            )
        }

        addModel(DiagramEmptyModel(users.size <= 1))
        notifyDataSetChanged()
    }

    fun getUserById(id: Long): User {
        lateinit var user: User
        models.forEach { model ->
            if (
                model is DiagramModel
                && model.model.id == id
            ) {
                user = model.model
            }
        }
        return user
    }

    fun getUserAtPosition(position: Int): User {
        return (models[position] as DiagramModel).model
    }

    fun getPositionByUserId(userId: Long): Int {
        var position = 0
        models.forEachIndexed { index, model ->
            if (
                model is DiagramModel
                && model.model.id == userId
            ) position = index

        }
        return position
    }

    fun getNextPosition(position: Int): Int {
        var nextPosition = position + 1//position - 1

        if (models.size - 2 >= position && position > 0) {
            nextPosition = position - 1
        }
        return nextPosition
    }

    fun deleteUserById(id: Long) {
        models.forEach { model ->
            if (
                model is DiagramModel
                && model.model.id == id
                && models.size != 1
            ) {
                hideModel(model)

                if (models.filter { it is DiagramModel && it.isShown }.size == 1) {
                    models.forEach { anotherModel ->
                        if (anotherModel is DiagramModel) {
                            anotherModel.isSwipeEnabled = false
                            notifyModelChanged(anotherModel)
                        }
                    }
                }

                return@forEach
            } else {
                if (
                    model is DiagramModel
                    && model.model.id == id
                ) {
                    model.isSwipeEnabled = false
                    notifyModelChanged(model)
                }
            }

        }
    }

    fun deleteUser(position: Int) {
        removeModel(models[position])
    }
}

class DiagramModel(
    val model: User,
    private var isExpanded: Boolean = false,
    var isSwipeEnabled: Boolean = true
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
                        "${model.subtitle2} •\n" +
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
            } else diagramContainer.background = null

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
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
            }

            icDelete.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_dark_close
                else R.drawable.ic_light_close
            )

            deleteBtn.setOnClickListener {
                EventBus.getDefault().post(DeleteDiagramItemEvent(model.id))

            }

            if (isSwipeEnabled) {
                diagramCard.setOnClickListener {
                    if (App.preferences.currentUserId != model.id) {
                        EventBus.getDefault().post(UpdateCurrentUserEvent(model.id))
                    }
                }
            } else {
                diagramCard.setOnClickListener {
                    if (App.preferences.currentUserId != model.id) {
                        EventBus.getDefault().post(UpdateCurrentUserEvent(model.id))
                    }
                }
            }

            swipeContainer.isEnabledSwipe = isSwipeEnabled
            swipeContainer.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    EventBus.getDefault().post(DiagramSetSwipingModelEvent(model.id, true))
                    if (isContinuous) {
                        EventBus.getDefault().post(DeleteDiagramItemEvent(model.id))
                    }
                }

                override fun onClose() {
                    EventBus.getDefault().post(DiagramSetSwipingModelEvent(model.id, false))
                }
            })
        }
    }


    override fun getDefaultLayout(): Int = R.layout.item_diagram
}

class DiagramEmptyModel(
    private val showEmptyText: Boolean = false,
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
                )
            )

            emptyPartnerCard.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_empty_partner_light
                else R.drawable.bg_empty_partner_dark
            )

            emptyPartnerCard.setOnClickListener {
                EventBus.getDefault().post(DiagramAddUserClickEvent())
            }

            partnersEmptyText.isVisible = showEmptyText
            partnersEmptyText.text =
                Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.diagram_empty_text))
            partnersEmptyText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 105f, resources.displayMetrics)

            val cardParams = emptyPartnerCard.layoutParams
            cardParams.height = height.toInt()

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner_empty
}