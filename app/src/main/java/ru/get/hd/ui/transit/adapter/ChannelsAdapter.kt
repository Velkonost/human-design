package ru.get.hd.ui.transit.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_channel.view.channelArrow
import kotlinx.android.synthetic.main.item_channel.view.channelCard
import kotlinx.android.synthetic.main.item_channel.view.channelDesc
import kotlinx.android.synthetic.main.item_channel.view.channelTitle
import kotlinx.android.synthetic.main.item_channel.view.number
import kotlinx.android.synthetic.main.item_compatibility_channel.view.*
import kotlinx.android.synthetic.main.item_faq.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.FaqClickedEvent
import ru.get.hd.model.Faq
import ru.get.hd.model.TransitionChannel
import ru.get.hd.util.ext.setTextAnimation
import ru.get.hd.util.ext.setTextAnimation07

class ChannelsAdapter : EpoxyAdapter() {

    fun createList(models: List<TransitionChannel>) {
        removeAllModels()
        models.map { addModel(ChannelModel(it)) }
        notifyDataSetChanged()
    }
}

class ChannelModel(
    private val model: TransitionChannel,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            channelTitle.text = model.title
            channelDesc.text = model.description
            number.text = model.number

            channelTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            channelDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            number.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            channelCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            number.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_channel_number_dark
                else R.drawable.bg_channel_number_light
            )

            channelArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            channelCard.setOnClickListener {
                isExpanded = !isExpanded

                if (isExpanded) {
                    val animation = ObjectAnimator.ofInt(
                        channelDesc,
                        "maxLines",
                        300
                    )
                    animation.duration = 1000
                    animation.start()
                    channelArrow
                        .animate().rotation(-90f).duration = 300
                    channelArrow.alpha = 0.3f
                } else {
                    val animation = ObjectAnimator.ofInt(
                        channelDesc,
                        "maxLines",
                        3
                    )
                    animation.duration = 1000
                    animation.start()
                    channelArrow
                        .animate().rotation(90f).duration = 300
                    channelArrow.alpha = 1f
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_channel
}