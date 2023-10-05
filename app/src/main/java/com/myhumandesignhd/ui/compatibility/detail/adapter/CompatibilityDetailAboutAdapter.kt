package com.myhumandesignhd.ui.compatibility.detail.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.text.Html
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.amplitude.api.Amplitude
import com.google.android.material.card.MaterialCardView
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenPaywallEvent
import com.myhumandesignhd.model.CompatibilityNewDescription
import com.myhumandesignhd.ui.bodygraph.second.adapter.AboutAdapter
import com.yandex.metrica.YandexMetrica
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus

enum class CompatibilityDetailAboutType {
    Overview,
    Love,
    Business
}

class CompatibilityDetailAboutAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val items: List<CompatibilityNewDescription>,
) : RecyclerView.Adapter<CompatibilityDetailAboutAdapter.ViewHolder>() {
    private var selectedItem = AboutAdapter.UNSELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compatibility_about, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

        private val container: MaterialCardView = itemView.findViewById(R.id.itemAboutContainer)
        private val expandableLayout: ExpandableLayout =
            itemView.findViewById(R.id.expandable_layout)
        private val expandButton: TextView
        private val text: TextView

        private val subtitle: TextView = itemView.findViewById(R.id.subtitle)

        private val generateBtnText: TextView = itemView.findViewById(R.id.generateBtnText)
        private val generateBtn: MaterialCardView = itemView.findViewById(R.id.generateBtn)

        private val generateProgressText: TextView = itemView.findViewById(R.id.generateProgressText)
        private val generateProgress: ProgressBar = itemView.findViewById(R.id.generateProgress)
        private val percentageProgress: ProgressBar = itemView.findViewById(R.id.percentageProgress)

        private val icArrow: ImageView = itemView.findViewById(R.id.icArrow)

        private val pointView: View = itemView.findViewById(R.id.pointView)
        private val pointViewContainer: CardView = itemView.findViewById(R.id.pointViewContainer)

        private val percentage: TextView = itemView.findViewById(R.id.percentage)

        fun bind() {
            val position = adapterPosition
            val isSelected = position == selectedItem

            icArrow.isVisible = android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
            generateBtn.isVisible = false
            generateProgress.isVisible = false
            generateProgressText.isVisible = false
            pointViewContainer.isVisible = false

            //
            expandButton.text = items[adapterPosition].title


            subtitle.isVisible = android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
            if (!App.preferences.isPremiun)
                subtitle.isVisible = true

            val firstLine = if (items[position].description.first().title != null)
                items[position].description.first().title
            else items[position].description.first().text

            var fullText = ""
            items[position].description.forEach {
                if (it.title != null) {
                    fullText += "<b>${it.title}</b>"
                    fullText += "<br>"
                }

                if (it.text != null) {
                    fullText += it.text
                    fullText += "<br>"
                }

                fullText += "<br>"
            }
            text.text = Html.fromHtml(fullText)
            subtitle.text = firstLine

            val successStr = App.resourcesProvider.getStringLocale(R.string.compatibility_success)
            percentage.text = "${items[position].percentage}% ${successStr}"
            percentageProgress.progress = items[position].percentage
            //

            expandButton.isSelected = isSelected
            expandableLayout.setExpanded(
                if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun) true
                else isSelected,
                false
            )

            container.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            expandButton.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            subtitle.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            generateProgressText.setTextColor(
                ContextCompat.getColor(
                context,
                R.color.lightColor
            ))

            percentageProgress.progressDrawable = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.injury_generate_progress_dark
                else R.drawable.injury_generate_progress_light
            )
        }

        override fun onClick(view: View?) {
            if (!App.preferences.isPremiun) {
                EventBus.getDefault().post(OpenPaywallEvent())
            }

            if (android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK) {

                val holder =
//                    recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
                    recyclerView.findViewHolderForAdapterPosition(adapterPosition) as ViewHolder?
                val position = adapterPosition
                if (holder != null && holder.expandableLayout.isExpanded) {

                    holder.expandButton.isSelected = false
                    holder.expandableLayout.collapse()
                }


                else if (holder != null && !holder.expandableLayout.isExpanded) {
                    expandButton.isSelected = true
                    expandableLayout.expand()
                    selectedItem = position

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        val smoothScroller: RecyclerView.SmoothScroller =
                            object : LinearSmoothScroller(context) {
                                override fun getVerticalSnapPreference(): Int {
                                    return SNAP_TO_START
                                }

                                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                                    return 0.5f//3000f / recyclerView.computeVerticalScrollRange()
                                }

                            }
                        smoothScroller.targetPosition = adapterPosition


                        (recyclerView.layoutManager as LinearLayoutManager)
                            .startSmoothScroll(smoothScroller)
                    }
                }

                when (adapterPosition) {
                    0 -> {
                        YandexMetrica.reportEvent("CompatibilityOverviewTapped")
                        Amplitude.getInstance().logEvent("CompatibilityOverviewTapped")
                    }
                    1 -> {
                        YandexMetrica.reportEvent("CompatibilityLoveTapped")
                        Amplitude.getInstance().logEvent("CompatibilityLoveTapped")
                    }
                    else -> {
                        YandexMetrica.reportEvent("CompatibilityBusinessTapped")
                        Amplitude.getInstance().logEvent("CompatibilityBusinessTapped")
                    }

                }
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {

            if (state == ExpandableLayout.State.EXPANDED) {
                expandButton.text = items[adapterPosition].title
                subtitle.isVisible = false

                icArrow.animate()
                    .rotation(-90f)
                    .duration = 300
            }

            if (state == ExpandableLayout.State.EXPANDING) {
                subtitle.isVisible = false
            }

            if (state == ExpandableLayout.State.COLLAPSED) {
                kotlin.runCatching {
                    expandButton.text = items[adapterPosition].title
                    subtitle.isVisible = true

                    icArrow.animate()
                        .rotation(90f)
                        .duration = 300
                }
            }
        }

        init {
            expandableLayout.setOnExpansionUpdateListener(this)
            expandButton = itemView.findViewById(R.id.expand_button)
            expandButton.setOnClickListener(this)
            icArrow.setOnClickListener(this)
            container.setOnClickListener(this)
            text = itemView.findViewById(R.id.description)
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }
}