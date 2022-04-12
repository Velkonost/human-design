package ru.get.hd.ui.faq.adapter

import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
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

            faqBlock.setOnClickListener {
                EventBus.getDefault().post(FaqClickedEvent(model))
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_faq
}