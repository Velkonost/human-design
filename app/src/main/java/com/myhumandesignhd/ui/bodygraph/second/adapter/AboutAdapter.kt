package com.myhumandesignhd.ui.bodygraph.second.adapter

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.amplitude.api.Amplitude
import com.google.android.material.card.MaterialCardView
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenPaywallEvent
import com.myhumandesignhd.event.StartInjuryEvent
import com.myhumandesignhd.event.UpdateCurrentUserInjurySettingsEvent
import com.myhumandesignhd.model.AboutItem
import com.myhumandesignhd.model.AboutType
import com.myhumandesignhd.util.ext.setTextAnimation
import com.myhumandesignhd.util.ext.setTextAnimation07
import com.myhumandesignhd.vm.BaseViewModel
import com.yandex.metrica.YandexMetrica
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus

class AboutAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val items: List<AboutItem>,
    private val injuryStatus: BaseViewModel.InjuryStatus,
    private val injuryPercent: Int? = null,
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

        private val pointView: View = itemView.findViewById(R.id.pointView)
        private val pointViewContainer: CardView = itemView.findViewById(R.id.pointViewContainer)

        fun bind() {
            val position = adapterPosition
            val isSelected = position == selectedItem

            icArrow.isVisible = android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK

            if (!App.preferences.isPremiun) {
                icArrow.setImageResource(
                    if (App.preferences.isDarkTheme) R.drawable.ic_lock_dark
                    else R.drawable.ic_lock_light
                )
                icArrow.rotation = 0f
                icArrow.isVisible = true
            }

            when(items[position].type) {
                AboutType.TYPE -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.type_title)
                    text.text = items[position].description

                    subtitle.isVisible = android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = items[position].name

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.PROFILE -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.profile_title)
                    text.text = items[position].description

                    subtitle.isVisible = android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = items[position].name

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.AUTHORITY -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.authority_title)
                    text.text = items[position].description

                    subtitle.isVisible = android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_authority_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.STRATEGY -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.strategy_title)
                    text.text = items[position].description

                    subtitle.isVisible =
                        android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_strategy_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.BUSINESS -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.business_title)
                    text.text = items[position].description

                    subtitle.isVisible =
                        android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_business_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.NUTRITION -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.nutrition_title)
                    text.text = items[position].description

                    subtitle.isVisible =
                        android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_nutrition_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.ENVIRONMENT -> {
                    expandButton.text =
                        if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun)
                            items[adapterPosition].name
                        else App.resourcesProvider.getStringLocale(R.string.environment_title)
                    text.text = items[position].description

                    subtitle.isVisible =
                        android.os.Build.VERSION.SDK_INT >= App.TARGET_SDK
                    if (!App.preferences.isPremiun)
                        subtitle.isVisible = true

                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_environment_subtitle)

                    generateBtn.isVisible = false
                    generateProgress.isVisible = false
                    generateProgressText.isVisible = false

                    pointViewContainer.isVisible = false
                }
                AboutType.INJURY -> {
                    pointViewContainer.isVisible = false
                    if (injuryStatus == BaseViewModel.InjuryStatus.NOT_STARTED) {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            val gd = GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                intArrayOf(Color.parseColor("#4D8DBC"), Color.parseColor("#5352BD"))
                            )
                            gd.cornerRadius = 0f

                            pointView.setBackgroundDrawable(gd)

                            pointViewContainer.isVisible = true

                            val start = Color.parseColor("#4D8DBC")
//                    val mid = Color.parseColor("#5352BD")
                            val end = Color.parseColor("#5352BD")

                            val gradient = pointView.background as GradientDrawable

                            val evaluator = ArgbEvaluator()
                            val animator = TimeAnimator.ofFloat(0.0f, 1.0f)
                            animator.duration = 1500
                            animator.repeatCount = ValueAnimator.INFINITE
                            animator.repeatMode = ValueAnimator.REVERSE
                            animator.addUpdateListener {
                                val fraction = it.animatedFraction
                                val newStart = evaluator.evaluate(fraction, start, end) as Int
//                        val newMid = evaluator.evaluate(fraction, mid, start) as Int
                                val newEnd = evaluator.evaluate(fraction, end, start) as Int

                                gradient.colors = intArrayOf(newStart, newStart)
                            }

                            animator.start()
                        }

                        val paddingDp = 6f
                        val paddingTopDp = 18
                        val density = context.resources.displayMetrics.density
                        val paddingPixel = (paddingDp * density).toInt()
                        val paddingTopPixel = (paddingTopDp * density).toInt()

                        expandButton.setPadding(paddingPixel, paddingTopPixel, paddingPixel, 0)
                    }

                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.injury_title)

                    subtitle.isVisible = true
                    subtitle.text = App.resourcesProvider.getStringLocale(R.string.about_injury_subtitle)

                    if (injuryStatus == BaseViewModel.InjuryStatus.FINISHED) {
                        text.text = items[position].description

                        generateBtn.isVisible = false
                        generateProgress.isVisible = false
                        generateProgressText.isVisible = false
                    } else {
                        generateProgressText.text = App.resourcesProvider.getStringLocale(R.string.injury_generating_text)

                        if (injuryStatus == BaseViewModel.InjuryStatus.NOT_STARTED) {
                            text.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.injury_desc_before))

                            generateBtn.isVisible = true
                            generateBtnText.text = App.resourcesProvider.getStringLocale(R.string.injury_generate_text)

                            generateProgress.isVisible = false
                            generateProgressText.isVisible = false

                            generateBtn.setOnClickListener {
                                YandexMetrica.reportEvent("Tab2AboutTraumaGenerateTapped")
                                Amplitude.getInstance().logEvent("tab2AboutTraumaGenerateTapped")

                                EventBus.getDefault().post(StartInjuryEvent())

                                text.setTextAnimation07(App.resourcesProvider.getStringLocale(R.string.injury_desc_progress))

                                generateBtn.isVisible = false

                                generateProgress.isVisible = true
                                generateProgressText.isVisible = true

                                generateProgress.max = 100
                                generateProgress.progress = 0

                                EventBus.getDefault().post(UpdateCurrentUserInjurySettingsEvent())
                            }
                        } else {
                            Amplitude.getInstance().logEvent("tab2AboutTraumaGeneratedTapped")
                            generateProgress.isVisible = true
                            generateProgressText.isVisible = true
                            generateBtn.isVisible = false

                            text.text = Html.fromHtml(App.resourcesProvider.getStringLocale(R.string.injury_desc_progress))

                            generateProgress.max = 100

                            if (injuryPercent != null) {
                                generateProgress.progress = injuryPercent

                                if (injuryPercent >= 100) {
                                    generateProgress.isVisible = false
                                    generateProgressText.isVisible = false

                                    text.text = items[position].description
                                } else {

                                }
                            }
                        }
                    }
                }
            }

            expandButton.isSelected = isSelected
            expandableLayout.setExpanded(
                if (android.os.Build.VERSION.SDK_INT < App.TARGET_SDK && App.preferences.isPremiun) true
                else isSelected,
                false
            )

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

            generateProgressText.setTextColor(ContextCompat.getColor(
                context,
                R.color.lightColor
            ))

            generateProgress.progressDrawable = ContextCompat.getDrawable(
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

//                if (position == selectedItem) {
//                    selectedItem = UNSELECTED
//                } else {

                else if (holder != null && !holder.expandableLayout.isExpanded) {
                    expandButton.isSelected = true
                    expandableLayout.expand()
                    selectedItem = position

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        val smoothScroller: SmoothScroller =
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

                when (items[adapterPosition].type) {
                    AboutType.TYPE -> {
                        YandexMetrica.reportEvent("Tab2AboutTypeTapped")
                        Amplitude.getInstance().logEvent("tab2AboutTypeTapped")
                    }
                    AboutType.PROFILE -> {
                        YandexMetrica.reportEvent("Tab2AboutProfileTapped")
                        Amplitude.getInstance().logEvent("tab2AboutProfileTapped")
                    }
                    AboutType.AUTHORITY -> {
                        YandexMetrica.reportEvent("Tab2AboutAuthorityTapped")
                        Amplitude.getInstance().logEvent("tab2AboutAuthorityTapped")
                    }
                    AboutType.STRATEGY -> {
                        YandexMetrica.reportEvent("Tab2AboutStrategyTapped")
                        Amplitude.getInstance().logEvent("tab2AboutStrategyTapped")
                    }
                    AboutType.BUSINESS -> {
                        YandexMetrica.reportEvent("Tab2AboutBusinessTapped")
                        Amplitude.getInstance().logEvent("tab2AboutBusinessTapped")
                    }
                    AboutType.INJURY -> {
                        YandexMetrica.reportEvent("Tab2AboutTraumaTapped")
                        Amplitude.getInstance().logEvent("tab2AboutTraumaTapped")
                    }
                    AboutType.NUTRITION -> {
                        YandexMetrica.reportEvent("Tab2AboutNutrition")
                        Amplitude.getInstance().logEvent("tab2AboutNutritionTapped")
                    }
                    AboutType.ENVIRONMENT -> {
                        YandexMetrica.reportEvent("Tab2AboutEnvironment")
                        Amplitude.getInstance().logEvent("tab2AboutEnvironmentTapped")
                    }
                }
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            Log.d("ExpandableLayout", "State: $state")

            if (state == ExpandableLayout.State.EXPANDED) {
                    if (items[adapterPosition].type == AboutType.INJURY) {

                        if (injuryStatus != BaseViewModel.InjuryStatus.NOT_STARTED && injuryPercent != null) {
                            if (injuryPercent == 100) {
                                expandButton.setTextAnimation(items[adapterPosition].name?: "")
                            }
                        }

                        pointViewContainer.isVisible = false

                        val paddingDp = 16f
                        val paddingTopDp = 18
                        val density = context.resources.displayMetrics.density
                        val paddingPixel = (paddingDp * density).toInt()
                        val paddingTopPixel = (paddingTopDp * density).toInt()

                        expandButton.setPadding(paddingPixel,paddingTopPixel, paddingPixel, 0)
                    } else {
                        expandButton.setTextAnimation(items[adapterPosition].name?: "")
                    }

                    subtitle.isVisible = false
//                }

                icArrow.animate()
                    .rotation(-90f)
                    .duration = 300
            }

            if (state == ExpandableLayout.State.COLLAPSED) {
                kotlin.runCatching {
//                    expandButton.setTextAnimation(
//                        when (items[adapterPosition].type) {
//                            AboutType.TYPE -> App.resourcesProvider.getStringLocale(R.string.type_title)
//                            AboutType.PROFILE -> App.resourcesProvider.getStringLocale(R.string.profile_title)
//                            AboutType.AUTHORITY -> App.resourcesProvider.getStringLocale(R.string.authority_title)
//                            AboutType.STRATEGY -> App.resourcesProvider.getStringLocale(R.string.strategy_title)
//                            AboutType.INJURY -> App.resourcesProvider.getStringLocale(R.string.injury_title)
//                            AboutType.NUTRITION -> App.resourcesProvider.getStringLocale(R.string.nutrition_title)
//                            AboutType.ENVIRONMENT -> App.resourcesProvider.getStringLocale(R.string.environment_title)
//                        }
//                    )
                    subtitle.isVisible = true

                    icArrow.animate()
                        .rotation(90f)
                        .duration = 300

                    if (items[adapterPosition].type == AboutType.INJURY && injuryStatus == BaseViewModel.InjuryStatus.NOT_STARTED) {
                        pointViewContainer.isVisible = true

                        val paddingDp = 6f
                        val paddingTopDp = 18
                        val density = context.resources.displayMetrics.density
                        val paddingPixel = (paddingDp * density).toInt()
                        val paddingTopPixel = (paddingTopDp * density).toInt()

                        expandButton.setPadding(paddingPixel,paddingTopPixel, paddingPixel, 0)
                    }
                }
            }

            if (state == ExpandableLayout.State.EXPANDING) {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
//                        override fun getVerticalSnapPreference(): Int {
//                            return SNAP_TO_START
//                        }
//
//                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
//                            return 0.5f//3000f / recyclerView.computeVerticalScrollRange()
//                        }
//
//                    }
//                    smoothScroller.targetPosition = adapterPosition
//
//
//                    (recyclerView.layoutManager as LinearLayoutManager)
//                        .startSmoothScroll(smoothScroller)
//                }
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
        const val UNSELECTED = -1
    }
}