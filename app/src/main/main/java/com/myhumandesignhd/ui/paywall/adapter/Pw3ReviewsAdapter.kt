package com.myhumandesignhd.ui.paywall.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import kotlinx.android.synthetic.main.item_paywall_3_review.view.*

class Pw3ReviewsAdapter: EpoxyAdapter() {

    fun createList() {
        removeAllModels()

        addModel(ReviewModel(1))
        addModel(ReviewModel(2))
        addModel(ReviewModel(3))
        addModel(ReviewModel(4))
        addModel(ReviewModel(5))

        notifyDataSetChanged()
    }
}

class ReviewModel(
    private val position: Int
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            title.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            text.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            container.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_paywall_3_review_dark
                else R.drawable.bg_paywall_3_review_light
            )

            when(position) {
                1 -> {
                    icon.setImageResource(R.drawable.ic_paywall_3_review_1)
                    title.text = context.getString(R.string.paywall_3_review_1_title)
                    text.text = context.getString(R.string.paywall_3_review_1_text)
                }
                2 -> {
                    icon.setImageResource(R.drawable.ic_paywall_3_review_2)
                    title.text = context.getString(R.string.paywall_3_review_2_title)
                    text.text = context.getString(R.string.paywall_3_review_2_text)
                }
                3 -> {
                    icon.setImageResource(R.drawable.ic_paywall_3_review_3)
                    title.text = context.getString(R.string.paywall_3_review_3_title)
                    text.text = context.getString(R.string.paywall_3_review_3_text)
                }
                4 -> {
                    icon.setImageResource(R.drawable.ic_paywall_3_review_4)
                    title.text = context.getString(R.string.paywall_3_review_4_title)
                    text.text = context.getString(R.string.paywall_3_review_4_text)
                }
                else -> {
                    icon.setImageResource(R.drawable.ic_paywall_3_review_5)
                    title.text = context.getString(R.string.paywall_3_review_5_title)
                    text.text = context.getString(R.string.paywall_3_review_5_text)
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_paywall_3_review
}