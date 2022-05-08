package ru.get.hd.ui.compatibility.detail.info.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_compatibility_info.view.*
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.model.Faq
import ru.get.hd.ui.faq.adapter.FaqModel
import android.animation.ObjectAnimator





class CompatibilityInfoAdapter : EpoxyAdapter() {

    fun createList() {
        removeAllModels()

        addModel(CompatibilityInfoModel(1))
        addModel(CompatibilityInfoModel(2))
        addModel(CompatibilityInfoModel(3))
        addModel(CompatibilityInfoModel(4))

        notifyDataSetChanged()
    }
}

class CompatibilityInfoModel(
    private val position: Int,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {

            compatibilityInfoCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.darkSettingsCard
                else R.color.lightSettingsCard
            ))

            compatibilityInfoArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            val textShader: Shader = LinearGradient(
                0f, 0f, 0f, 20f,
                when (position) {
                    1 -> intArrayOf(Color.parseColor("#CE7559"), Color.parseColor("#BD5252"))
                    2 -> intArrayOf(Color.parseColor("#5290BD"), Color.parseColor("#6A59CE"))
                    3 -> intArrayOf(Color.parseColor("#46A59F"), Color.parseColor("#117D43"))
                    else -> intArrayOf(Color.parseColor("#7D2793"), Color.parseColor("#4E128A"))
                },
                floatArrayOf(0f, 1f),
                TileMode.CLAMP
            )

            compatibilityInfoTitle.paint.shader = textShader
            compatibilityInfoTitle.text = App.resourcesProvider.getStringLocale(
                when (position) {
                    1 -> R.string.compatibility_info_title_1
                    2 -> R.string.compatibility_info_title_2
                    3 -> R.string.compatibility_info_title_3
                    else -> R.string.compatibility_info_title_4
                }
            )

            compatibilityInfoDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            compatibilityInfoDesc.text = App.resourcesProvider.getStringLocale(
                when (position) {
                    1 -> R.string.compatibility_info_desc_1
                    2 -> R.string.compatibility_info_desc_2
                    3 -> R.string.compatibility_info_desc_3
                    else -> R.string.compatibility_info_desc_4
                }
            )

            compatibilityInfoCard.setOnClickListener {
                isExpanded = !isExpanded

                if (isExpanded) {
                    val animation = ObjectAnimator.ofInt(
                        compatibilityInfoDesc,
                        "maxLines",
                        30
                    )
                    animation.duration = 1000
                    animation.start()
                    compatibilityInfoArrow
                        .animate().rotation(-90f).duration = 300
                    compatibilityInfoArrow.alpha = 0.3f

                } else {
                    val animation = ObjectAnimator.ofInt(
                        compatibilityInfoDesc,
                        "maxLines",
                        3
                    )
                    animation.duration = 1000
                    animation.start()
                    compatibilityInfoArrow
                        .animate().rotation(90f).duration = 300
                    compatibilityInfoArrow.alpha = 1f
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_info
}