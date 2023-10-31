package com.myhumandesignhd.ui.compatibility.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Html
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.amplitude.api.Amplitude
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.AddPartnerClickEvent
import com.myhumandesignhd.event.ChangeCompatibilityViewPagerUserInputEvent
import com.myhumandesignhd.event.CompatibilityStartClickEvent
import com.myhumandesignhd.event.DeletePartnerItemEvent
import com.myhumandesignhd.model.response.BodygraphResponse
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


class PartnersAdapter : EpoxyAdapter() {

    private var items: MutableList<BodygraphResponse> = mutableListOf()


    fun createList(partners: List<BodygraphResponse>) {
        items = partners.toMutableList()

        removeAllModels()
        partners.map { addModel(PartnerModel(it)) }

        if (partners.isNotEmpty()) {
            addModel(EmptyPartnerModel(partners.isNullOrEmpty()))
        }

        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val item = models[position]
        return item.id()
    }


    fun getPartnerAtPosition(position: Int): BodygraphResponse {
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

                if (models.count { it.isShown } == 1) {
                    val emptyModel =
                        (models.findLast { it is EmptyPartnerModel } as EmptyPartnerModel)
                    hideModel(emptyModel)
                }

                return@forEach
            }
        }
    }
}

class PartnerModel(
    val model: BodygraphResponse,
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
            subtitle.text = "${model.type} • " + "${model.line}\n" + model.profile
            compatibilityPercentage.text = "${model.compatibilityAvg}%"

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

            compatibilityName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val chartResId =
                if (model.type.lowercase(Locale.getDefault()) == "проектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_proektor_dark
                    else R.drawable.ic_chart_proektor_light
                } else if (model.type.lowercase(Locale.getDefault()) == "рефлектор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_reflector_dark
                    else R.drawable.ic_chart_reflector_light
                } else if (model.type.lowercase(Locale.getDefault()) == "генератор") {
                    if (App.preferences.isDarkTheme) R.drawable.ic_chart_generator_dark
                    else R.drawable.ic_chart_generator_light
                } else if (model.type.lowercase(Locale.getDefault()) == "манифестирующий генератор") {
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

            partnerCard.setOnClickListener {
                Amplitude.getInstance().logEvent("tab4CheckedLoveRelationship")
                EventBus.getDefault().post(
                    CompatibilityStartClickEvent(
                        user = model,
                        chartResId = chartResId
                    )
                )
            }

            partnerCard.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> EventBus.getDefault().post(
                        ChangeCompatibilityViewPagerUserInputEvent(true)
                    )
                    else -> EventBus.getDefault().post(
                        ChangeCompatibilityViewPagerUserInputEvent(false)
                    )
                }
                false
            }


            swipeContainer.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> EventBus.getDefault().post(
                        ChangeCompatibilityViewPagerUserInputEvent(true)
                    )
                    else -> EventBus.getDefault().post(
                        ChangeCompatibilityViewPagerUserInputEvent(false)
                    )
                }
                false
            }

            swipeContainer.isEnabledSwipe = isSwipeEnabled
            swipeContainer.setOnActionsListener(object : SwipeLayout.SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    ChangeCompatibilityViewPagerUserInputEvent(false)

                    if (isContinuous) {
                        EventBus.getDefault().post(DeletePartnerItemEvent(model.id))
                    }
                }

                override fun onClose() {
                    ChangeCompatibilityViewPagerUserInputEvent(false)
                }
            })

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner
}

class EmptyPartnerModel(
    var showEmptyText: Boolean = false
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
