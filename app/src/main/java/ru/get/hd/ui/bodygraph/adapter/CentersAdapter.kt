package ru.get.hd.ui.bodygraph.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_center.view.*
import kotlinx.android.synthetic.main.item_center.view.channelCard
import kotlinx.android.synthetic.main.item_center.view.channelDesc
import kotlinx.android.synthetic.main.item_center.view.channelTitle
import kotlinx.android.synthetic.main.item_channel.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.Center
import ru.get.hd.model.TransitionGate
import ru.get.hd.ui.transit.adapter.GateModel
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07

class CentersAdapter : EpoxyAdapter() {

    fun createList(models: List<Center>) {
        removeAllModels()
        models.map { addModel(CentersModel(it)) }
        notifyDataSetChanged()
    }
}

class CentersModel(
    private val model: Center,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            channelTitle.text = model.name
            channelDesc.text = model.description

            channelTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

            channelDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                ))

            channelCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                ))

            centersArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            centersArrow.setOnClickListener {
                isExpanded = !isExpanded

                if (isExpanded) {
                    val animation = ObjectAnimator.ofInt(
                        channelDesc,
                        "maxLines",
                        300
                    )
                    animation.duration = 1000
                    animation.start()
                    centersArrow
                        .animate().rotation(-90f).duration = 300
                    centersArrow.alpha = 0.3f
                } else {
                    val animation = ObjectAnimator.ofInt(
                        channelDesc,
                        "maxLines",
                        3
                    )
                    animation.duration = 1000
                    animation.start()
                    centersArrow
                        .animate().rotation(90f).duration = 300
                    centersArrow.alpha = 1f
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_center
}