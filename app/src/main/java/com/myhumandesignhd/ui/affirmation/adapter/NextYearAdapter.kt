package com.myhumandesignhd.ui.affirmation.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_channel.view.channelArrow
import kotlinx.android.synthetic.main.item_channel.view.channelCard
import kotlinx.android.synthetic.main.item_channel.view.channelDesc
import kotlinx.android.synthetic.main.item_channel.view.channelTitle
import kotlinx.android.synthetic.main.item_channel.view.number
import kotlinx.android.synthetic.main.item_next_year_block.view.blockNumber
import kotlinx.android.synthetic.main.item_next_year_block.view.blockText
import kotlinx.android.synthetic.main.item_next_year_block.view.blockTitle
import kotlinx.android.synthetic.main.item_next_year_block.view.footerText
import kotlinx.android.synthetic.main.item_next_year_first_part.view.activationDate
import kotlinx.android.synthetic.main.item_next_year_first_part.view.activationDateTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.generalOverview
import kotlinx.android.synthetic.main.item_next_year_first_part.view.generalOverviewTitle
import kotlinx.android.synthetic.main.item_next_year_first_part.view.mainText
import kotlinx.android.synthetic.main.item_next_year_planet_title.view.globalProcesses
import kotlinx.android.synthetic.main.item_next_year_planet_title.view.planetInsights
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class NextYearAdapter : EpoxyAdapter() {

    fun create(
        context: Context,
        includeConclusion: Boolean = true
    ) {
        removeAllModels()

        addModel(NextYearFirstPartModel())

        val titles = context.resources.getStringArray(R.array.next_year_titles)
        val texts = context.resources.getStringArray(R.array.next_year_texts)
        val numbers = context.resources.getStringArray(R.array.next_year_numbers)

        titles.forEachIndexed { index, _ ->
            addModel(
                NextYearBlockModel(
                    title = titles[index],
                    text = texts[index],
                    numberStr = numbers[index]
                )
            )
        }

        val planetTitles = context.resources.getStringArray(R.array.next_year_planets_titles)
        val planetTexts = context.resources.getStringArray(R.array.next_year_planets_texts)
        val planetNumbers = context.resources.getStringArray(R.array.next_year_planets_numbers)

        addModel(NextYearPlanetsTitle())

        planetTitles.forEachIndexed { index, _ ->
            addModel(
                NextYearPlanetModel(
                    title = planetTitles[index],
                    text = planetTexts[index],
                    numberStr = planetNumbers[index]
                )
            )
        }

        addModel(NextYearConclusionModel(includeConclusion))
    }
}

class NextYearPlanetsTitle : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val color = ContextCompat.getColor(
                context, if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )

            globalProcesses.setTextColor(color)
            planetInsights.setTextColor(color)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_next_year_planet_title
}

class NextYearPlanetModel(
    val title: String,
    val text: String,
    val numberStr: String,
//    private val position: Int,
//    private val recyclerView: RecyclerView,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            channelTitle.text = title
            channelDesc.text = text
            number.text = numberStr

            val lp = (channelCard.layoutParams as ViewGroup.MarginLayoutParams)
            lp.marginStart = 0
            lp.marginEnd = 0
            channelCard.requestLayout()

            channelTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            channelDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            number.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            channelCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                    else R.color.lightSettingsCard
                )
            )

            number.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_channel_number_dark
                else R.drawable.bg_channel_number_light
            )

            channelArrow.setImageResource(R.drawable.ic_next_year_arrow)

            channelArrow.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            if (isExpanded) {
                channelDesc.maxLines = 70
                channelArrow
                    .animate().rotation(-180f).duration = 300
                channelArrow.alpha = 0.3f
            } else {

                channelDesc.maxLines = 3
                channelArrow
                    .animate().rotation(0f).duration = 300
                channelArrow.alpha = 1f
            }

            if (Build.VERSION.SDK_INT < App.TARGET_SDK) {
                channelDesc.maxLines = 70
                channelArrow.isVisible = false
            }

            channelCard.setOnClickListener {
                if (Build.VERSION.SDK_INT >= App.TARGET_SDK) {
                    isExpanded = !isExpanded

                    if (isExpanded) {

                        val smoothScroller: RecyclerView.SmoothScroller =
                            object : LinearSmoothScroller(context) {
                                override fun getVerticalSnapPreference(): Int {
                                    return SNAP_TO_START
                                }

                                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                                    return 0.5f//3000f / recyclerView.computeVerticalScrollRange()
                                }

                            }
//                        smoothScroller.targetPosition = position
//                        (recyclerView.layoutManager as LinearLayoutManager)
//                            .startSmoothScroll(smoothScroller)

                        val animation = ObjectAnimator.ofInt(
                            channelDesc,
                            "maxLines",
                            70
                        )
                        animation.duration = 1000
                        animation.start()
                        channelArrow
                            .animate().rotation(-180f).duration = 300
                        channelArrow.alpha = 0.3f
                    } else {
                        val animation = ObjectAnimator.ofInt(
                            channelDesc,
                            "maxLines",
                            3
                        )
                        animation.duration = 500
                        animation.start()
                        channelArrow
                            .animate().rotation(0f).duration = 300
                        channelArrow.alpha = 1f
                    }
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_channel
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

            val diff = TimeUnit.HOURS.convert(TimeZone.getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS) - 3
            val millsToAdd = diff * 1000 * 60 * 60
            val dateFormat = SimpleDateFormat("HH:mm")
            val d = dateFormat.parse("16:18")
            if (d != null) {
                d.time = d.time + millsToAdd
                val activationDateText = context.getString(R.string.next_year_activation_date) + " " + dateFormat.format(d)
                activationDate.text = activationDateText
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_next_year_first_part
}

class NextYearBlockModel(
    val title: String,
    val text: String,
    val numberStr: String
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

            blockNumber.isVisible = true
            footerText.isVisible = false

            blockTitle.text = title
            blockText.text = text
            blockNumber.text = numberStr
//            val typeface = ResourcesCompat.getFont(context, R.font.roboto)
//            blockText.typeface = typeface
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                blockText.typeface = Typeface.create(blockText.typeface, 400, false)
            }

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

class NextYearConclusionModel(val includeAll: Boolean = true) : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val color = ContextCompat.getColor(
                context, if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            )

            blockTitle.setTextColor(color)
            blockText.setTextColor(color)
            footerText.setTextColor(color)

//            val typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
//            blockText.typeface = typeface
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                blockText.typeface = Typeface.create(blockText.typeface, 600, false)
            }

            blockNumber.isVisible = false
            footerText.isVisible = true

            blockTitle.text = context.getString(R.string.next_year_conclusion_title)
            blockText.text = context.getString(R.string.next_year_conclusion_text)

            footerText.text =
                if (includeAll) context.getString(R.string.next_year_conclusion_footer) + context.getString(
                    R.string.next_year_conclusion_footer_2
                )
                else context.getString(R.string.next_year_conclusion_footer)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_next_year_block
}