package ru.get.hd.ui.compatibility.child.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_compatibility_child_description.view.*
import ru.get.hd.App
import ru.get.hd.R

class ChildrenDescriptionsAdapter : EpoxyAdapter() {

    fun createList(
        childrenTitles: List<String>?,
        childrenDescriptions: List<String>?
    ) {
        removeAllModels()

        if (
            childrenTitles != null
            && childrenDescriptions != null
        ) {
            childrenTitles.forEachIndexed { index, s ->
                addModel(
                    ChildrenDescriptionModel(
                        childrenTitles[index],
                        childrenDescriptions[index]
                    )
                )
            }
        }

        notifyDataSetChanged()
    }
}

class ChildrenDescriptionModel(
    private val title: String,
    private val description: String
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            childTitle.text = title
            childDesc.text = description

            childTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

            childDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_child_description
}
