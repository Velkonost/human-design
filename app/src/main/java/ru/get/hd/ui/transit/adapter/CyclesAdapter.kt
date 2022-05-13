package ru.get.hd.ui.transit.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_cycle.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.Cycle
import ru.get.hd.model.TransitionChannel

class CyclesAdapter : EpoxyAdapter() {

    fun createList(models: List<Cycle>) {
        removeAllModels()
        models.map { addModel(CycleModel(it)) }
        notifyDataSetChanged()
    }
}

class CycleModel(
    private val model: Cycle,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            cycleTitle.text =
                if (App.preferences.locale == "ru") model.nameRu
                else model.nameEn

            cycleDesc.text =
                if (App.preferences.locale == "ru") model.descriptionRu
                else model.descriptionEn

            cycleAge.text =
                if (App.preferences.locale == "ru") model.ageRu
                else model.ageEn

            cycleAgeTitle.text = App.resourcesProvider.getStringLocale(R.string.cycle_age_title)

            cycleTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleAge.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleAgeTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            cycleCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            cycleCard.setOnClickListener {
                isExpanded = !isExpanded

                if (isExpanded) {
                    cycleDesc.isVisible = true
                    val animation = ObjectAnimator.ofInt(
                        cycleDesc,
                        "maxLines",
                        50
                    )
                    animation.duration = 1000
                    animation.start()
                    cycleArrow
                        .animate().rotation(-90f).duration = 300
                    cycleArrow.alpha = 0.3f
                } else {
                    val animation = ObjectAnimator.ofInt(
                        cycleDesc,
                        "maxLines",
                        0
                    )
                    animation.duration = 1000
                    animation.start()
                    animation.doOnEnd {
                        cycleDesc.isVisible = false
                    }
                    cycleArrow
                        .animate().rotation(90f).duration = 300
                    cycleArrow.alpha = 1f


                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_cycle
}