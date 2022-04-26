package ru.get.hd.ui.faq.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
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
import kotlinx.android.synthetic.main.item_faq.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.FaqClickedEvent
import ru.get.hd.model.Faq

class FaqAdapter : EpoxyAdapter() {

    fun createList(models: List<Faq>) {
        removeAllModels()
        models.map { addModel(FaqModel(it)) }
        notifyDataSetChanged()
    }
}

class FaqModel(
    private val model: Faq
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            faqTitle.text =
                if (App.preferences.locale == "ru") Html.fromHtml(model.titleRu)
                else Html.fromHtml(model.titleEn)

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
                model = model
            )

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

            faqBlock.setOnClickListener {
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
