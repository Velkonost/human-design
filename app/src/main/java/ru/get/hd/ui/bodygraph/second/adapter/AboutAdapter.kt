package ru.get.hd.ui.bodygraph.second.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

class AboutAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val items: List<AboutItem>
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

        private val icArrow: ImageView = itemView.findViewById(R.id.icArrow)

        fun bind() {
            val position = adapterPosition
            val isSelected = position == selectedItem


            when(items[position].type) {
                AboutType.TYPE -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.type_title)
                    text.text = items[position].description
                }
                AboutType.PROFILE -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.profile_title)
                    text.text = items[position].description
                }
                AboutType.AUTHORITY -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.authority_title)
                    text.text = items[position].description
                }
                AboutType.STRATEGY -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.strategy_title)
                    text.text = items[position].description
                }
                AboutType.INJURY -> {
                    expandButton.text = App.resourcesProvider.getStringLocale(R.string.injury_title)
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
                expandButton.setTextAnimation(items[adapterPosition].name)

                icArrow.animate()
                    .rotation(-90f)
                    .duration = 300
            }

            if (state == ExpandableLayout.State.COLLAPSED) {
                expandButton.setTextAnimation(
                    when(items[position].type) {
                        AboutType.TYPE -> App.resourcesProvider.getStringLocale(R.string.type_title)
                        AboutType.PROFILE -> App.resourcesProvider.getStringLocale(R.string.profile_title)
                        AboutType.AUTHORITY -> App.resourcesProvider.getStringLocale(R.string.authority_title)
                        AboutType.STRATEGY -> App.resourcesProvider.getStringLocale(R.string.strategy_title)
                        AboutType.INJURY -> App.resourcesProvider.getStringLocale(R.string.injury_title)
                    }
                )

                icArrow.animate()
                    .rotation(90f)
                    .duration = 300
            }
//            if (state == ExpandableLayout.State.EXPANDING) {
//                recyclerView.smoothScrollToPosition(adapterPosition)
//            }
        }

        init {
//            expandableLayout.setInterpolator(OvershootInterpolator())
            expandableLayout.setOnExpansionUpdateListener(this)

            expandButton = itemView.findViewById(R.id.expand_button)
            expandButton.setOnClickListener(this)
            icArrow.setOnClickListener(this)

            text = itemView.findViewById(R.id.description)
        }
    }

    companion object {
        private const val UNSELECTED = -1
    }
}