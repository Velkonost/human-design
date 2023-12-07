package com.myhumandesignhd.ui.affirmation.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_next_year_block.view.blockNumber
import kotlinx.android.synthetic.main.item_next_year_block.view.blockText
import kotlinx.android.synthetic.main.item_next_year_block.view.blockTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.activationDate
import kotlinx.android.synthetic.main.item_next_year_first_part.view.activationDateTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.generalOverview
import kotlinx.android.synthetic.main.item_next_year_first_part.view.generalOverviewTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.mainText

class NextYearAdapter : EpoxyAdapter() {

    fun create(
        context: Context
    ) {
        removeAllModels()

        addModel(NextYearFirstPartModel())

        val titles = context.resources.getStringArray(R.array.next_year_titles)
        val texts = context.resources.getStringArray(R.array.next_year_texts)
        val numbers = context.resources.getStringArray(R.array.next_year_numbers)

        titles.forEachIndexed { index, s ->
            addModel(
                NextYearBlockModel(
                    title = titles[index],
                    text = texts[index],
                    number = numbers[index]
                )
            )
        }
    }
}

class NextYearFirstPartModel : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val color = ContextCompat.getColor(
                context, if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )

            activationDateTitle.setTextColor(color)
            activationDate.setTextColor(color)
            mainText.setTextColor(color)
            generalOverviewTitle.setTextColor(color)
            generalOverview.setTextColor(color)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_next_year_first_part
}

class NextYearBlockModel(
    val title: String,
    val text: String,
    val number: String
) : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val color = ContextCompat.getColor(
                context, if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )

            blockTitle.text = title
            blockText.text = text
            blockNumber.text = number

            blockTitle.setTextColor(color)
            blockText.setTextColor(color)
            blockNumber.setTextColor(color)

            blockNumber.background = ContextCompat.getDrawable(
                context, if (App.preferences.isDarkTheme) R.drawable.bg_next_year_number_dark
                else R.drawable.bg_next_year_number_light
            )
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_next_year_block
}