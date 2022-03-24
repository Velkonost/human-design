package ru.get.hd.ui.compatibility.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_partner.view.*
import kotlinx.android.synthetic.main.item_partner_empty.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.UpdateCurrentUserEvent
import ru.get.hd.model.Child
import ru.get.hd.model.User


class PartnersAdapter : EpoxyAdapter() {

    fun createList(
        partners: List<User>
    ) {
        removeAllModels()
        partners.map { addModel(PartnerModel(it)) }
        addModel(EmptyPartnerModel())

        notifyDataSetChanged()
    }
}

class PartnerModel(
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

            partnerCard.setOnClickListener {

            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner
}

class EmptyPartnerModel : EpoxyModel<View>() {

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

            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_partner_empty
}
