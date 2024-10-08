package com.myhumandesignhd.ui.faq.adapter

import android.content.res.ColorStateList
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.FaqClickedEvent
import com.myhumandesignhd.model.Faq
import kotlinx.android.synthetic.main.item_faq.view.faqArrow
import kotlinx.android.synthetic.main.item_faq.view.faqClick
import kotlinx.android.synthetic.main.item_faq.view.faqSeparator
import kotlinx.android.synthetic.main.item_faq.view.faqTitle
import org.greenrobot.eventbus.EventBus

class FaqAdapter : EpoxyAdapter() {

    fun createList(models: List<Faq>) {
        removeAllModels()
        var isFirst = true
        models.map {
            addModel(FaqModel(it, isFirst))
            isFirst = false
        }
        notifyDataSetChanged()
    }
}

class FaqModel(
    private val model: Faq,
    private val isFirst: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            faqTitle.text =
                when (App.preferences.locale) {
                    "ru" -> Html.fromHtml(model.titleRu)
                    "es" -> Html.fromHtml(model.titleEs)
                    else -> Html.fromHtml(model.titleEn)
                }

            if (isFirst) {
                faqTitle.highlightKeys(
                    "Human Design",
                    "Types",
                    "Profiles",
                    "Gate",
                    "Channels",
                    "Daily Transits",
                    "Compatibility",

                    "Каналы",
                    "Дизайн Человека",
                    "Типы",
                    "Ворота",
                    "Профили",
                    "Отношения",
                    "Транзиты",
                    "Inner Authority",

                    "Diseño Humano",
                    "Tipos",
                    "Perfiles",
                    "Autoridad Interior",
                    "Estrategia",
                    "Centros",
                    "Canales",
                    "una puerta",
                    "Tránsitos Diarios",
                    "Compatibilidad",
                    "Trauma Genético",

                    model = model
                )
            } else {
                faqTitle.highlightKeys(
                    "Human Design",
                    "Types",
                    "Profiles",
                    "Gate",
                    "Channels",
                    "Daily Transits",
                    "Compatibility",

                    "Каналы",
                    "Дизайн Человека",
                    "Типы",
                    "Ворота",
                    "Профили",
                    "Отношения",
                    "Транзиты",
                    "Inner Authority",

                    "Tipos",
                    "Perfiles",
                    "Autoridad Interior",
                    "Estrategia",
                    "Centros",
                    "Canales",
                    "una puerta",
                    "Tránsitos Diarios",
                    "Compatibilidad",
                    "Trauma Genético",
                    "Nutrición",
                    "Hábitat",

                    model = model
                )
            }

            faqSeparator.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            faqTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            faqArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            faqClick.setOnClickListener {
                EventBus.getDefault().post(FaqClickedEvent(model))
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_faq
}

fun TextView.highlightKeys(vararg keys: String, model: Faq) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in keys) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
                textPaint.isUnderlineText = false
            }

            override fun onClick(view: View) {
                EventBus.getDefault().post(FaqClickedEvent(model))
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link, startIndexOfLink + 1)

        if (startIndexOfLink != -1) {
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    this.movementMethod =
        LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}
