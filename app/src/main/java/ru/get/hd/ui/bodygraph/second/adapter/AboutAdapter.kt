package ru.get.hd.ui.bodygraph.second.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import net.cachapa.expandablelayout.ExpandableLayout
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.AboutItem
import ru.get.hd.model.AboutType
import ru.get.hd.ui.faq.adapter.FaqAdapter
import ru.get.hd.util.ext.setTextAnimation
import kotlin.math.exp
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import android.util.DisplayMetrics
import android.util.EventLog
import android.widget.ProgressBar
import androidx.core.view.isVisible
import org.greenrobot.eventbus.EventBus
import ru.get.hd.event.UpdateCurrentUserInjurySettingsEvent
import ru.get.hd.model.User
import ru.get.hd.push.NotificationReceiver
import ru.get.hd.util.ext.setTextAnimation07

class AboutAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val items: List<AboutItem>,
    private val currentUser: User
) : RecyclerView.Adapter<AboutAdapter.ViewHolder>() {

    private var selectedItem = UNSELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_about, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return items.size
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

        private val icArrow: ImageView = itemView.findViewById(R.id.icArrow)

        fun bind() {
            val position = adapterPosition
            val isSelected = position == selectedItem

            when(items[position].type) {
                AboutType.TYPE -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.type_title)
                    text.text = items[position].description

                    subtitle.isVisible = true
                    subtitle.text = items[position].name

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false
                }
                AboutType.PROFILE -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.profile_title)
                    text.text = items[position].description

                    subtitle.isVisible = true
                    subtitle.text = items[position].name

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false
                }
                AboutType.AUTHORITY -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.authority_title)
                    text.text = items[position].description

                    subtitle.isVisible = true
                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_authority_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false
                }
                AboutType.STRATEGY -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.strategy_title)
                    text.text = items[position].description

                    subtitle.isVisible = true
                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_strategy_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false
                }
                AboutType.INJURY -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.injury_title)

                    subtitle.isVisible = true
                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_injury_subtitle)

                    if (currentUser.isInjuryGenerated) {
                        text.text = items[position].description

                        generateBtn.isVisible = false
                        generateProgress.isVisible = false
                        generateProgressText.isVisible = false
                    } else {
                        generateProgressText.text = App.resourcesProvider.getStringLocale(R.string.injury_generating_text)

                        if (currentUser.injuryDateStart == null) {
                            text.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.injury_desc_before))

                            generateBtn.isVisible = true
                            generateBtnText.text = App.resourcesProvider.getStringLocale(R.string.injury_generate_text)

                            generateProgress.isVisible = false
                            generateProgressText.isVisible = false

                            generateBtn.setOnClickListener {
                                val injuryGenerationDuration = 60000L//(12..24).random() * 3600000L
                                currentUser.injuryDateStart = System.currentTimeMillis()
                                currentUser.injuryGenerationDuration = injuryGenerationDuration

                                text.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.injury_desc_progress))

                                generateBtn.isVisible = false

                                generateProgress.isVisible = true
                                generateProgressText.isVisible = true

                                generateProgress.max = 100
                                generateProgress.progress = 5

                                val notifyIntent = Intent(context, NotificationReceiver::class.java)
                                notifyIntent.putExtra("userName", currentUser.name)

                                val pendingIntent = PendingIntent.getBroadcast(
                                    context,
                                    4,
                                    notifyIntent,
                                    PendingIntent.FLAG_IMMUTABLE
                                )

                                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                alarmManager.set(
                                    AlarmManager.RTC_WAKEUP,
                                    System.currentTimeMillis() + injuryGenerationDuration,
                                    pendingIntent
                                )

                                EventBus.getDefault().post(UpdateCurrentUserInjurySettingsEvent())
                            }
                        } else {
                            generateProgress.isVisible = true
                            generateProgressText.isVisible = true
                            generateBtn.isVisible = false

                            text.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.injury_desc_progress))

                            generateProgress.max = 100

                            var currentProgress =
                                ((System.currentTimeMillis() - currentUser.injuryDateStart!!) * 100 / currentUser.injuryGenerationDuration!!).toInt() + 5

                            if (currentProgress > 100) currentProgress = 100

                            generateProgress.progress = currentProgress

                            if (currentProgress >= 100) {
                                generateProgress.isVisible = false
                                generateProgressText.isVisible = false

                                text.text = items[position].description
                            } else {

                            }
                        }
                    }
                }
            }

            expandButton.isSelected = isSelected
            expandableLayout.setExpanded(isSelected, false)

            container.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            expandButton.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            subtitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            generateBtnText.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            generateProgressText.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            generateProgress.progressDrawable = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.injury_generate_progress_dark
                else R.drawable.injury_generate_progress_light
            )
        }

        override fun onClick(view: View?) {
            val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
            if (holder != null) {
                holder.expandButton.isSelected = false
                holder.expandableLayout.collapse()
            }
            val position = adapterPosition
            if (position == selectedItem) {
                selectedItem = UNSELECTED
            } else {
                expandButton.isSelected = true
                expandableLayout.expand()
                selectedItem = position
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            Log.d("ExpandableLayout", "State: $state")

            if (state == ExpandableLayout.State.EXPANDED) {
//                if (
//                    items[adapterPosition].type != AboutType.TYPE
//                    && items[adapterPosition].type != AboutType.PROFILE
//                ) {


                    if (
                        items[adapterPosition].type == AboutType.INJURY
                    ) {

                        if (currentUser.injuryDateStart != null) {
                            var currentProgress =
                                ((System.currentTimeMillis() - currentUser.injuryDateStart!!) * 100 / currentUser.injuryGenerationDuration!!).toInt() + 5
                            if (currentProgress > 100) currentProgress = 100

                            if (currentProgress == 100) {
                                expandButton.setTextAnimation(items[adapterPosition].name)
                            }
                        }

                    } else {
                        expandButton.setTextAnimation(items[adapterPosition].name)
                    }

                    subtitle.isVisible = false
//                }

                icArrow.animate()
                    .rotation(-90f)
                    .duration = 300
            }

            if (state == ExpandableLayout.State.COLLAPSED) {
                kotlin.runCatching {
                    expandButton.setTextAnimation(
                        when (items[adapterPosition].type) {
                            AboutType.TYPE -> App.resourcesProvider.getStringLocale(R.string.type_title)
                            AboutType.PROFILE -> App.resourcesProvider.getStringLocale(R.string.profile_title)
                            AboutType.AUTHORITY -> App.resourcesProvider.getStringLocale(R.string.authority_title)
                            AboutType.STRATEGY -> App.resourcesProvider.getStringLocale(R.string.strategy_title)
                            AboutType.INJURY -> App.resourcesProvider.getStringLocale(R.string.injury_title)
                        }
                    )
                    subtitle.isVisible = true

                    icArrow.animate()
                        .rotation(90f)
                        .duration = 300
                }
            }

            if (state == ExpandableLayout.State.EXPANDING) {
                val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
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

        init {
            expandableLayout.setOnExpansionUpdateListener(this)


            expandButton = itemView.findViewById(R.id.expand_button)
            expandButton.setOnClickListener(this)
            icArrow.setOnClickListener(this)
            container.setOnClickListener(this)

            text = itemView.findViewById(R.id.description)
        }
    }

    companion object {
        private const val UNSELECTED = -1
    }
}