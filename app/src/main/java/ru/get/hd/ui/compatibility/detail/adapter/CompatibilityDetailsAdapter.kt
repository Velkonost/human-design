package ru.get.hd.ui.compatibility.detail.adapter

import android.content.res.ColorStateList
import android.os.Handler
import android.text.Html
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_bodygraph_about.view.*
import kotlinx.android.synthetic.main.item_bodygraph_centers.view.*
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.*
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.activeChannelDesc
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.activeChannelTitle
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.channelsRecycler
import kotlinx.android.synthetic.main.item_bodygraph_gates.view.*
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_channel.view.channelCard
import kotlinx.android.synthetic.main.item_channel.view.channelDesc
import kotlinx.android.synthetic.main.item_channel.view.channelTitle
import kotlinx.android.synthetic.main.item_channel.view.number
import kotlinx.android.synthetic.main.item_compatibility_channel.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_about.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_channels.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_profiles.view.*
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.AboutItem
import ru.get.hd.model.Center
import ru.get.hd.model.CompatibilityChannel
import ru.get.hd.model.CompatibilityResponse
import ru.get.hd.model.TransitionChannel
import ru.get.hd.model.TransitionGate
import ru.get.hd.ui.bodygraph.adapter.CentersAdapter
import ru.get.hd.ui.bodygraph.second.adapter.AboutAdapter
import ru.get.hd.ui.bodygraph.second.adapter.GatesModel
import ru.get.hd.ui.transit.adapter.ChannelsAdapter
import ru.get.hd.ui.transit.adapter.GatesAdapter

class CompatibilityDetailsAdapter : EpoxyAdapter() {

    fun createList(
        firstTitle: String,
        secondTitle: String,
        firstName: String,
        secondName: String,
        compatibility: CompatibilityResponse,
        chart1ResId: Int,
        chart2ResId: Int
    ) {
        removeAllModels()

        addModel(AboutModel(
            firstTitle, secondTitle,
            firstName, secondName,
            compatibility.description,
            chart1ResId, chart2ResId
        ))
        addModel(ProfilesModel(
            compatibility.line,
            compatibility.profileTitle,
            compatibility.profileDescription
        ))
        addModel(ChannelsModel(
            compatibility.channels
        ))

        notifyDataSetChanged()
    }
}

class AboutModel(
    private val firstTitleValue: String,
    private val secondTitleValue: String,
    private val firstNameValue: String,
    private val secondNameValue: String,
    private val description: String,
    private val chart1ResId: Int,
    private val chart2ResId: Int
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            chart1.setImageResource(chart1ResId)
            chart2.setImageResource(chart2ResId)

            firstTitle.text = firstTitleValue
            secondTitle.text = secondTitleValue

            firstName.text = firstNameValue
            secondName.text = secondNameValue

            aboutDesc.text = description

            firstTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            firstName.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            secondTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            secondName.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            aboutDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            Handler().postDelayed({
                (gif.drawable as GifDrawable).start()
            }, 2500)


        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_detail_about
}

class ProfilesModel(
    private val line: String,
    private val profileTitleValue: String,
    private val profileDescriptionValue: String
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            profileTitle.text = "$profileTitleValue • ${App.resourcesProvider.getStringLocale(R.string.profile_title)} $line"
            profileDesc.text = profileDescriptionValue

            profileTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            profileDesc.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_detail_profiles
}

class ChannelsModel(
    private val channels: List<CompatibilityChannel>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            channelsTitle.text = App.resourcesProvider.getStringLocale(R.string.compatibility_channels_title)
            channelsDesc.text = App.resourcesProvider.getStringLocale(R.string.compatibility_channels_desc)

            channelsTitle.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            channelsDesc.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))


            val channelsAdapter = CompatibilityChannelsAdapter()
            view.channelsRecycler.adapter = channelsAdapter
            channelsAdapter.createList(channels)

        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_detail_channels
}

class CompatibilityChannelsAdapter : EpoxyAdapter() {

    fun createList(models: List<CompatibilityChannel>) {
        removeAllModels()
        models.map { addModel(CompatibilityChannelModel(it)) }
        notifyDataSetChanged()
    }
}

class CompatibilityChannelModel(
    private val model: CompatibilityChannel
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

            typeTitle.text = when(model.type) {
                "0" -> App.resourcesProvider.getStringLocale(R.string.channel_type_1)
                "1" -> App.resourcesProvider.getStringLocale(R.string.channel_type_2)
                "2" -> App.resourcesProvider.getStringLocale(R.string.channel_type_3)
                else -> App.resourcesProvider.getStringLocale(R.string.channel_type_4)
            }

            typeTitle.background = ContextCompat.getDrawable(
                context,
                when(model.type) {
                    "0" -> R.drawable.bg_channel_type_1
                    "1" -> R.drawable.bg_channel_type_2
                    "2" -> R.drawable.bg_channel_type_3
                    else -> R.drawable.bg_channel_type_4
                }
            )
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_channel
}