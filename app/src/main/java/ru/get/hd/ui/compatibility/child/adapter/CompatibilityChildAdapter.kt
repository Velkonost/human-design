package ru.get.hd.ui.compatibility.child.adapter

import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_compatibility_child.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_about.view.*
import pl.droidsonroids.gif.GifDrawable
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.CompatibilityResponse
import ru.get.hd.ui.compatibility.detail.adapter.AboutModel
import ru.get.hd.ui.compatibility.detail.adapter.ChannelsModel
import ru.get.hd.ui.compatibility.detail.adapter.ProfilesModel

class CompatibilityChildAdapter : EpoxyAdapter() {

    fun createList(
        childTitle: String,
        childDesc: String,
        parentTitle: String,
        parentDesc: String,
        chart1ResId: Int,
        chart2ResId: Int
    ) {
        removeAllModels()

        addModel(
            ChildModel(
                childTitle,
                childDesc,
                chart1ResId,
                chart2ResId
            )
        )

        addModel(
            ChildModel(
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
    private val childTitleValue: String,
    private val childDescValue: String,
    private val chart1ResId: Int,
    private val chart2ResId: Int
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            childChart1.setImageResource(chart1ResId)
            childChart2.setImageResource(chart2ResId)

            childTitle.text = childTitleValue
            childDesc.text = childDescValue

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

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_child
}
