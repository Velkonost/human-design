package com.myhumandesignhd.ui.compatibility.child.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_compatibility_child.view.*

class CompatibilityChildAdapter : EpoxyAdapter() {

    fun createList(
        childTitle: String,
        childDesc: String,
        parentTitle: String,
        parentDesc: String,
        chart1ResId: Int,
        chart2ResId: Int,
        childrenTitles: List<String>?,
        childrenDescriptions: List<String>?
    ) {
        removeAllModels()

        addModel(
            ChildModel(
                true,
                childTitle,
                childDesc,
                chart1ResId,
                chart2ResId,
                childrenTitles,
                childrenDescriptions
            )
        )

        addModel(
            ChildModel(
                false,
                parentTitle,
                parentDesc,
                chart1ResId,
                chart2ResId
            )
        )

        notifyDataSetChanged()
    }
}

class ChildModel(
    private val isChild: Boolean,
    private val childTitleValue: String,
    private val childDescValue: String,
    private val chart1ResId: Int,
    private val chart2ResId: Int,
    private val childrenTitles: List<String>? = emptyList(),
    private val childrenDescriptions: List<String>? = emptyList()
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
//            childChart1.setImageResource(chart1ResId)
//            childChart2.setImageResource(chart2ResId)

            if (isChild) {
                childTitle.isVisible = false
                childDesc.isVisible = false

                val childrenDescriptionsAdapter = ChildrenDescriptionsAdapter()
                descriptionsRecycler.adapter = childrenDescriptionsAdapter
                childrenDescriptionsAdapter.createList(childrenTitles, childrenDescriptions)
            } else {

                childTitle.text = childTitleValue
                childDesc.text = childDescValue

                childTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    )
                )

                childDesc.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.lightColor
                        else R.color.darkColor
                    )
                )
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_child
}
