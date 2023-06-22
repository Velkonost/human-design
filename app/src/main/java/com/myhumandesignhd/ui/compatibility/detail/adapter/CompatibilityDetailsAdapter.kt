package com.myhumandesignhd.ui.compatibility.detail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.OpenCompatibilityChannelEvent
import com.myhumandesignhd.model.CompatibilityChannel
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.NewDescriptions
import com.myhumandesignhd.util.convertDpToPx
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.overlay.BalloonOverlayRect
import kotlinx.android.synthetic.main.item_about_gates_title.view.*
import kotlinx.android.synthetic.main.item_bodygraph_channels.view.*
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_compatibility_channel.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_about.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_channels.view.*
import kotlinx.android.synthetic.main.item_compatibility_detail_channels.view.channelsRecycler
import kotlinx.android.synthetic.main.item_compatibility_detail_profiles.view.*
import kotlinx.android.synthetic.main.item_compatibility_info.view.*
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemContainer
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemText
import kotlinx.android.synthetic.main.item_description_gate_item.view.aboutItemTitle
import kotlinx.android.synthetic.main.item_description_gate_item.view.gateNumber
import org.greenrobot.eventbus.EventBus
import pl.droidsonroids.gif.GifDrawable

class CompatibilityDetailsAdapter : EpoxyAdapter() {

    fun createList(
        firstTitle: String,
        secondTitle: String,
        firstName: String,
        secondName: String,
        compatibility: CompatibilityResponse,
        chart1ResId: Int,
        chart2ResId: Int,
        context: Context,
    ) {
        removeAllModels()

        addModel(
            AboutModel(
                firstTitle, secondTitle,
                firstName, secondName,
                compatibility.description,
                chart1ResId, chart2ResId,
                compatibility.newDescriptions
            )
        )
        addModel(
            ProfilesModel(
                compatibility.line,
                compatibility.profileTitle,
                compatibility.profileDescription,
                compatibility.descrTitle,
                compatibility.descrNext
            )
        )
        addModel(
            ChannelsModel(
                compatibility.channels,
                context
            )
        )

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
    private val chart2ResId: Int,
    private val newDescriptions: ArrayList<NewDescriptions>
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

            firstTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            firstName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            secondTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            secondName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            aboutDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val adapter = ParentDescriptionAdapter()
            adapter.createList(newDescriptions)
            descriptionRecycler.adapter = adapter

            Handler().postDelayed({
                (gif.drawable as GifDrawable).start()
            }, 1000)


        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_detail_about
}

class ProfilesModel(
    private val line: String,
    private val profileTitleValue: String,
    private val profileDescriptionValue: String,
    private val descrTitle: String,
    private val descNext: String
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            profileTitle.text =
                "$profileTitleValue ${App.resourcesProvider.getStringLocale(R.string.profile_title)} $line"
            profileDesc.text = profileDescriptionValue
            descrTitle.text = this@ProfilesModel.descrTitle
            descNext.text = this@ProfilesModel.descNext

            profileTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val paint = profileTitle.paint
            val width = paint.measureText(profileTitle.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, width, profileTitle.textSize, intArrayOf(
                    Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
                ), null, Shader.TileMode.REPEAT
            )
            profileTitle.paint.shader = textShader

            profileDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            descrTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            descNext.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )


        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_detail_profiles
}

class CompatibilityChannelsTitleModel : EpoxyModel<View>() {
    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            activeGatesTitle.text =
                App.resourcesProvider.getStringLocale(R.string.compatibility_channels_title)
            activeGatesDesc.text =
                App.resourcesProvider.getStringLocale(R.string.compatibility_channels_desc)

            activeGatesTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            val paint = activeGatesTitle.paint
            val width = paint.measureText(activeGatesTitle.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, width, activeGatesTitle.textSize, intArrayOf(
                    Color.parseColor("#58B9FF"), Color.parseColor("#5655F9")
                ), null, Shader.TileMode.REPEAT
            )
            activeGatesTitle.paint.shader = textShader

            activeGatesDesc.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_about_gates_title
}

class ChannelsModel(
    private val channels: List<CompatibilityChannel>,
    private val context: Context
) : EpoxyModel<View>() {

    private var root: View? = null

    private var isHelpShowing = false
    private var countDownTimer: CountDownTimer? = null

    private val channelsAdapter: CompatibilityChannelsAdapter by lazy {
        CompatibilityChannelsAdapter()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            view.channelsRecycler.adapter = channelsAdapter
            channelsAdapter.createList(channels, view.channelsRecycler)
        }
    }

    private fun lastPosUpdate(memeID: Long, scrollPos: Int) {

        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        countDownTimer = object : CountDownTimer(300, 300) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (!App.preferences.compatibilityChannelsHelpShown && App.preferences.isCompatibilityDetailChannelsAddedNow) {
                    val balloon = Balloon.Builder(context)
                        .setArrowSize(15)
                        .setArrowOrientation(ArrowOrientation.BOTTOM)
                        .setArrowPositionRules(ArrowPositionRules.ALIGN_BALLOON)
                        .setArrowPosition(0.3f)
                        .setPadding(10)
                        .setTextGravity(Gravity.CENTER)
                        .setWidth(BalloonSizeSpec.WRAP)
                        .setMaxWidth(300)
                        .setHeight(BalloonSizeSpec.WRAP)
                        .setTextSize(12f)
                        .setCornerRadius(10f)
                        .setText(App.resourcesProvider.getStringLocale(R.string.help_compatibility_channel))
                        .setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.lightColor
                            )
                        )
                        .setTextIsHtml(true)
                        .setOverlayColorResource(R.color.helpBgColor)
                        .setOverlayShape(BalloonOverlayRect)
                        .setIsVisibleOverlay(true)
                        .setBackgroundColor(
                            Color.parseColor("#4D494D")
                        )
                        .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                        .setOnBalloonDismissListener {
                            isHelpShowing = false
                            App.preferences.compatibilityChannelsHelpShown = true
                        }
                        .build()

                    channelsAdapter.getModels().forEach { model ->
                        if (model is CompatibilityChannelModel) {
                            if (model.getTypeView().isVisible() && !isHelpShowing) {
                                isHelpShowing = true
                                balloon.showAlignBottom(
                                    model.getTypeView(),
                                    xOff = context.convertDpToPx(64f).toInt()
                                )

                            }
                        }
                    }
                }
//                Log.d(tagg, "scroll finished")
            }
        }
        countDownTimer!!.start()
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_detail_channels
}

@SuppressLint("ClickableViewAccessibility")
fun NestedScrollView.onScrollStateChanged(
    startDelay: Long = 100,
    stopDelay: Long = 400,
    listener: (Boolean) -> Unit
) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_SCROLL, MotionEvent.ACTION_MOVE -> {
                handler.postDelayed({
                    listener.invoke(true)
                }, startDelay)
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                handler.postDelayed({
                    listener.invoke(false)
                }, stopDelay)
            }
        }
        false // Do not consume events
    }
}

class CompatibilityChannelsAdapter : EpoxyAdapter() {

    fun createList(
        models: List<CompatibilityChannel>,
        recyclerView: RecyclerView
    ) {
        removeAllModels()

        addModel(CompatibilityChannelsTitleModel())
        var position = 0
        models.map {
            addModel(CompatibilityChannelModel(it, position + 1, recyclerView))
            position++
        }
        notifyDataSetChanged()
    }

    fun getModelByPosition(position: Int): CompatibilityChannelModel {
        return models[position] as CompatibilityChannelModel
    }

    fun getModels() = models
}

class CompatibilityChannelModel(
    private val model: CompatibilityChannel,
    private val position: Int,
    private val recyclerView: RecyclerView,
    private var isExpanded: Boolean = false
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            aboutItemTitle.text = model.title
            aboutItemText.text = model.description
            gateNumber.text = model.number

            aboutItemContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_about_item_dark
                else R.drawable.bg_about_item_light
            )

            aboutItemTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            aboutItemText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.lightColor
                    else R.color.darkColor
                )
            )

            gateNumber.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_gate_number_dark
                else R.drawable.bg_gate_number_light
            )

            typeTitle.text = when (model.type) {
                "0" -> App.resourcesProvider.getStringLocale(R.string.channel_type_1)
                "1" -> App.resourcesProvider.getStringLocale(R.string.channel_type_2)
                "2" -> App.resourcesProvider.getStringLocale(R.string.channel_type_3)
                else -> App.resourcesProvider.getStringLocale(R.string.channel_type_4)
            }

            typeTitle.setTextColor(
                Color.parseColor(
                    when (model.type) {
                        "0" -> "#CE7959"
                        "1" -> "#538DBE"
                        "2" -> "#45A2A2"
                        else -> "#802793"
                    }
                )
            )

            typeTitle.background = ContextCompat.getDrawable(
                context,
                when (model.type) {
                    "0" -> R.drawable.bg_channel_type_1
                    "1" -> R.drawable.bg_channel_type_2
                    "2" -> R.drawable.bg_channel_type_3
                    else -> R.drawable.bg_channel_type_4
                }
            )

            aboutItemContainer.setOnClickListener {
                EventBus.getDefault().post(OpenCompatibilityChannelEvent(model))
            }

        }
    }

    fun getTypeView() = root!!.typeTitle


    override fun getDefaultLayout(): Int = R.layout.item_compatibility_channel
}

fun View.isVisible(): Boolean {
    if (!isShown) {
        return false
    }
    val actualPosition = Rect()
    val isGlobalVisible = getGlobalVisibleRect(actualPosition)
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val screen = Rect(0, 0, screenWidth, screenHeight)
    return isGlobalVisible //&& Rect.intersects(actualPosition, screen)
}