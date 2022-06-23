package com.myhumandesignhd.ui.transit.adapter

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate
import com.myhumandesignhd.model.TransitionPlanet
import com.myhumandesignhd.util.ext.alpha1
import com.myhumandesignhd.util.ext.setTextAnimation
import kotlinx.android.synthetic.main.item_about_gates_title.view.*
import kotlinx.android.synthetic.main.item_transit.view.*

class TransitAdapter : EpoxyAdapter() {

    fun createList(
        gates: List<TransitionGate>,
        channels: List<TransitionChannel>,
        recyclerView: RecyclerView,
        birthDesignPlanets: ArrayList<TransitionPlanet>,
        birthPersonalityPlanets: ArrayList<TransitionPlanet>,
        currentDesignPlanets: ArrayList<TransitionPlanet>,
        currentPersonalityPlanets: ArrayList<TransitionPlanet>,
    ) {
        removeAllModels()

        addModel(
            TransitModel(
                gates.isNullOrEmpty() && channels.isNullOrEmpty(),
                birthDesignPlanets,
                birthPersonalityPlanets,
                currentDesignPlanets,
                currentPersonalityPlanets,
            )
        )

        var position = 0
        if (gates.isNotEmpty()) {
            addModel(GatesTitleModel(true))
            position++
        }
        gates.map {
            addModel(GateModel(it, position + 1, recyclerView))
            position++
        }

        if (channels.isNotEmpty()) {
            addModel(GatesTitleModel(false))
            position++
        }
        channels.map {
            addModel(ChannelModel(it, position + 1, recyclerView))
            position++
        }
        notifyDataSetChanged()
    }
}

class TransitModel(
    private val isEmptyTextVisible: Boolean,
    private val birthDesignPlanets: ArrayList<TransitionPlanet>,
    private val birthPersonalityPlanets: ArrayList<TransitionPlanet>,
    private val currentDesignPlanets: ArrayList<TransitionPlanet>,
    private val currentPersonalityPlanets: ArrayList<TransitionPlanet>,
) : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            nextTransitTitle.text =
                App.resourcesProvider.getStringLocale(R.string.next_transit_text)
            nextTransitTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            emptyText.isVisible = isEmptyTextVisible
            view.emptyText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
            emptyText.text = App.resourcesProvider.getStringLocale(R.string.transit_no_channels)

            leftZnak1.setTextAnimation("${birthDesignPlanets[0].gate}.${birthDesignPlanets[0].line}") {
                designTitle.alpha1(500)
                znak1Red.alpha1(500)
            }

            leftZnak2.setTextAnimation("${birthDesignPlanets[1].gate}.${birthDesignPlanets[1].line}") {
                znak2Red.alpha1(500)
            }

            leftZnak3.setTextAnimation("${birthDesignPlanets[2].gate}.${birthDesignPlanets[2].line}") {
                znak3Red.alpha1(500)
            }

            leftZnak4.setTextAnimation("${birthDesignPlanets[3].gate}.${birthDesignPlanets[3].line}") {
                znak4Red.alpha1(500)
            }

            leftZnak5.setTextAnimation("${birthDesignPlanets[4].gate}.${birthDesignPlanets[4].line}") {
                znak5Red.alpha1(500)
            }

            leftZnak6.setTextAnimation("${birthDesignPlanets[5].gate}.${birthDesignPlanets[5].line}") {
                znak6Red.alpha1(500)
            }

            leftZnak7.setTextAnimation("${birthDesignPlanets[6].gate}.${birthDesignPlanets[6].line}") {
                znak7Red.alpha1(500)
            }

            leftZnak8.setTextAnimation("${birthDesignPlanets[7].gate}.${birthDesignPlanets[7].line}") {
                znak8Red.alpha1(500)
            }

            leftZnak9.setTextAnimation("${birthDesignPlanets[8].gate}.${birthDesignPlanets[8].line}") {
                znak9Red.alpha1(500)
            }

            leftZnak10.setTextAnimation("${birthDesignPlanets[9].gate}.${birthDesignPlanets[9].line}") {
                znak10Red.alpha1(500)
            }

            leftZnak11.setTextAnimation("${birthDesignPlanets[10].gate}.${birthDesignPlanets[10].line}") {
                znak11Red.alpha1(500)
            }

            leftZnak12.setTextAnimation("${birthDesignPlanets[11].gate}.${birthDesignPlanets[11].line}") {
                znak12Red.alpha1(500)
            }

            leftZnak13.setTextAnimation("${birthDesignPlanets[12].gate}.${birthDesignPlanets[12].line}") {
                znak13Red.alpha1(500)
            }

            rightZnak1.setTextAnimation("${currentDesignPlanets[0].gate}.${currentDesignPlanets[0].line}")
            rightZnak2.setTextAnimation("${currentDesignPlanets[1].gate}.${currentDesignPlanets[1].line}")
            rightZnak3.setTextAnimation("${currentDesignPlanets[2].gate}.${currentDesignPlanets[2].line}")
            rightZnak4.setTextAnimation("${currentDesignPlanets[3].gate}.${currentDesignPlanets[3].line}")
            rightZnak5.setTextAnimation("${currentDesignPlanets[4].gate}.${currentDesignPlanets[4].line}")
            rightZnak6.setTextAnimation("${currentDesignPlanets[5].gate}.${currentDesignPlanets[5].line}")
            rightZnak7.setTextAnimation("${currentDesignPlanets[6].gate}.${currentDesignPlanets[6].line}")
            rightZnak8.setTextAnimation("${currentDesignPlanets[7].gate}.${currentDesignPlanets[7].line}")
            rightZnak9.setTextAnimation("${currentDesignPlanets[8].gate}.${currentDesignPlanets[8].line}")
            rightZnak10.setTextAnimation("${currentDesignPlanets[9].gate}.${currentDesignPlanets[9].line}")
            rightZnak11.setTextAnimation("${currentDesignPlanets[10].gate}.${currentDesignPlanets[10].line}")
            rightZnak12.setTextAnimation("${currentDesignPlanets[11].gate}.${currentDesignPlanets[11].line}")
            rightZnak13.setTextAnimation("${currentDesignPlanets[12].gate}.${currentDesignPlanets[12].line}")

            blueZnak1.setTextAnimation("${currentPersonalityPlanets[0].gate}.${currentPersonalityPlanets[0].line}") {
                transitTitle.alpha1(500)
                znak1Blue.alpha1(500)
            }

            blueZnak2.setTextAnimation("${currentPersonalityPlanets[1].gate}.${currentPersonalityPlanets[1].line}") {
                znak2Blue.alpha1(500)
            }

            blueZnak3.setTextAnimation("${currentPersonalityPlanets[2].gate}.${currentPersonalityPlanets[2].line}") {
                znak3Blue.alpha1(500)
            }

            blueZnak4.setTextAnimation("${currentPersonalityPlanets[3].gate}.${currentPersonalityPlanets[3].line}") {
                znak4Blue.alpha1(500)
            }

            blueZnak5.setTextAnimation("${currentPersonalityPlanets[4].gate}.${currentPersonalityPlanets[4].line}") {
                znak5Blue.alpha1(500)
            }

            blueZnak6.setTextAnimation("${currentPersonalityPlanets[5].gate}.${currentPersonalityPlanets[5].line}") {
                znak6Blue.alpha1(500)
            }

            blueZnak7.setTextAnimation("${currentPersonalityPlanets[6].gate}.${currentPersonalityPlanets[6].line}") {
                znak7Blue.alpha1(500)
            }

            blueZnak8.setTextAnimation("${currentPersonalityPlanets[7].gate}.${currentPersonalityPlanets[7].line}") {
                znak8Blue.alpha1(500)
            }

            blueZnak9.setTextAnimation("${currentPersonalityPlanets[8].gate}.${currentPersonalityPlanets[8].line}") {
                znak9Blue.alpha1(500)
            }

            blueZnak10.setTextAnimation("${currentPersonalityPlanets[9].gate}.${currentPersonalityPlanets[9].line}") {
                znak10Blue.alpha1(500)
            }

            blueZnak11.setTextAnimation("${currentPersonalityPlanets[10].gate}.${currentPersonalityPlanets[10].line}") {
                znak11Blue.alpha1(500)
            }

            blueZnak12.setTextAnimation("${currentPersonalityPlanets[11].gate}.${currentPersonalityPlanets[11].line}") {
                znak12Blue.alpha1(500)
            }

            blueZnak13.setTextAnimation("${currentPersonalityPlanets[12].gate}.${currentPersonalityPlanets[12].line}") {
                znak13Blue.alpha1(500)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val rotate1 = RotateAnimation(
                    0f,
                    360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate1.repeatCount = Animation.INFINITE
                rotate1.fillAfter = true
                rotate1.duration = 50000
                rotate1.interpolator = LinearInterpolator()
                circle1.startAnimation(rotate1)

                val rotate2 = RotateAnimation(
                    0f,
                    -360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate2.repeatCount = Animation.INFINITE
                rotate2.fillAfter = true
                rotate2.duration = 50000
                rotate2.interpolator = LinearInterpolator()
                circle2.startAnimation(rotate2)

                val rotate3 = RotateAnimation(
                    0f,
                    360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate3.repeatCount = Animation.INFINITE
                rotate3.fillAfter = true
                rotate3.duration = 50000
                rotate3.interpolator = LinearInterpolator()
                circle3.startAnimation(rotate3)

                val rotate4 = RotateAnimation(
                    0f,
                    -360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate4.repeatCount = Animation.INFINITE
                rotate4.fillAfter = true
                rotate4.duration = 50000
                rotate4.interpolator = LinearInterpolator()
                circle4.startAnimation(rotate4)

                val rotate5 = RotateAnimation(
                    0f,
                    360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate5.repeatCount = Animation.INFINITE
                rotate5.fillAfter = true
                rotate5.duration = 50000
                rotate5.interpolator = LinearInterpolator()
                circle5.startAnimation(rotate5)

                val rotate6 = RotateAnimation(
                    0f,
                    360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate6.repeatCount = Animation.INFINITE
                rotate6.fillAfter = true
                rotate6.duration = 50000
                rotate6.interpolator = LinearInterpolator()
                circle6.startAnimation(rotate6)

                val rotate7 = RotateAnimation(
                    0f,
                    -360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate7.repeatCount = Animation.INFINITE
                rotate7.fillAfter = true
                rotate7.duration = 50000
                rotate7.interpolator = LinearInterpolator()
                circle7.startAnimation(rotate7)

                val rotate8 = RotateAnimation(
                    0f,
                    360f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate8.repeatCount = Animation.INFINITE
                rotate8.fillAfter = true
                rotate8.duration = 50000
                rotate8.interpolator = LinearInterpolator()
                circle8.startAnimation(rotate8)
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_transit
}


class GatesTitleModel(
    private val isGates: Boolean
) : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            activeGatesTitle.text =
                App.resourcesProvider.getStringLocale(
                    if (isGates) R.string.gates_title
                    else R.string.channels_title
                )

            activeGatesTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_transit_gates_title
}
