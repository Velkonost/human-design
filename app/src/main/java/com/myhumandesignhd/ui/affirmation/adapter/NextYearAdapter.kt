package com.myhumandesignhd.ui.affirmation.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_next_year_first_part.view.activationDate
import kotlinx.android.synthetic.main.item_next_year_first_part.view.activationDateTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.generalOverview
import kotlinx.android.synthetic.main.item_next_year_first_part.view.generalOverviewTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.mainText

class NextYearAdapter: EpoxyAdapter() {

    fun create() {
        removeAllModels()

        addModel(NextYearFirstPartModel())
    }
}

class NextYearFirstPartModel: EpoxyModel<View>() {
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