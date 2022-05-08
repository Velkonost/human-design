package ru.get.hd.ui.compatibility.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import io.sulek.ssml.OnSwipeListener
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
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.AddPartnerClickEvent
import ru.get.hd.event.CompatibilityStartClickEvent
import ru.get.hd.event.DeletePartnerItemEvent
import ru.get.hd.event.UpdateCurrentUserEvent
import ru.get.hd.model.Child
import ru.get.hd.model.User
import java.util.*


class PartnersAdapter : EpoxyAdapter() {

    fun createList(
        partners: List<User>
    ) {
        removeAllModels()
        partners.map { addModel(PartnerModel(it)) }
        addModel(EmptyPartnerModel(partners.isNullOrEmpty()))

        notifyDataSetChanged()
    }

    fun getPartnerAtPosition(position: Int): User {
        return (models[position] as PartnerModel).model
    }

    fun deletePartner(position: Int) {
        removeModel(models[position])
    }

    fun deletePartnerById(partnerId: Long) {
        models.forEach { model ->
            if (
                model is PartnerModel
                && model.model.id == partnerId
            ) {
                hideModel(model)
                return@forEach
            }
        }
    }
}

class PartnerModel(
    val model: User,
    private var isExpanded: Boolean = false,
    private val isSwipeEnabled: Boolean = true
) : EpoxyModel<View>() {

    private var root: View? = null

    @SuppressLint("SetTextI18n")
    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            swipeContainer.setOnSwipeListener(object : OnSwipeListener {
                override fun onSwipe(isExpanded: Boolean) {
                    this@PartnerModel.isExpanded = isExpanded
                }
            })
            swipeContainer.isEnabled = isSwipeEnabled

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

            val chartResId =
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

            chart.setImageResource(chartResId)

            icDelete.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_dark_close
                else R.drawable.ic_light_close
            )

            deleteBtn.setOnClickListener {
                EventBus.getDefault().post(DeletePartnerItemEvent(model.id))
            }

            swipeContainer.setOnClickListener {
                EventBus.getDefault().post(
                    CompatibilityStartClickEvent(
                        user = model,
                        chartResId = chartResId
                    )
                )
            }
            swipeContainer.apply(isExpanded)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner
}

class EmptyPartnerModel(
    private val showEmptyText: Boolean = false
) : EpoxyModel<View>() {

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
                EventBus.getDefault().post(AddPartnerClickEvent())
            }

            partnersEmptyText.isVisible = showEmptyText
            partnersEmptyText.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.partners_empty_text))
            partnersEmptyText.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner_empty
}
