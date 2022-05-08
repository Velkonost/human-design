package ru.get.hd.util.ext

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristic
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.collection.lruCache
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withTranslation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyViewHolder
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.ui.bodygraph.diagram.adapter.DiagramEmptyModel
import ru.get.hd.ui.bodygraph.diagram.adapter.DiagramsAdapter
import ru.get.hd.ui.compatibility.adapter.EmptyChildrenModel
import ru.get.hd.ui.compatibility.adapter.EmptyPartnerModel
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT


fun RecyclerView.setUpRemoveItemTouchHelper(
    swiped: (viewHolder: RecyclerView.ViewHolder, swipeDir: Int) -> Unit
) {

    val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        lateinit var background: Drawable
        var initiated: Boolean = false

        val textPaint = TextPaint().apply {
            color = Color.WHITE
            isAntiAlias = true
//            textSize = resources.getDimension(backgroundTextSize)
            typeface = ResourcesCompat.getFont(context, R.font.roboto)
        }

        val paint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            typeface = ResourcesCompat.getFont(context, R.font.roboto)
        }

        private fun init() {
            initiated = true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, LEFT or RIGHT)
        }

        override fun onMove(
            rv: RecyclerView,
            vh: RecyclerView.ViewHolder,
            t: RecyclerView.ViewHolder
        ): Boolean = false

//        override fun getSwipeDirs(
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder
//        ): Int {
//
//            var canSwipe = //!(recyclerView.adapter is DiagramsAdapter &&
//                (recyclerView.adapter)!!.itemCount > 1
//
//            if (
//                (viewHolder is EpoxyViewHolder)
//                &&
//                (
//                        viewHolder.model is EmptyPartnerModel
//                                || viewHolder.model is EmptyChildrenModel
//                                || viewHolder.model is DiagramEmptyModel
//                        )
//            ) canSwipe = false
//
//            if (
//                recyclerView.adapter is DiagramsAdapter
//                && recyclerView.adapter!!.itemCount <= 2
//            ) canSwipe = false
//
//            return if (canSwipe) super.getSwipeDirs(
//                recyclerView,
//                viewHolder
//            ) else ItemTouchHelper.ACTION_STATE_IDLE
//        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            swiped(viewHolder, swipeDir)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            if (viewHolder.adapterPosition == -1) {
                return
            }
            if (!initiated) {
                init()
            }


            if (actionState == ACTION_STATE_SWIPE) {
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                );
            }

            val bounds = Rect()
            val x = itemView.width - bounds.right - 20
            val y = itemView.bottom + bounds.height() - (itemView.height + bounds.height()) / 2

//            ContextCompat.getDrawable(
//                context,
//                if (App.preferences.isDarkTheme) R.drawable.ic_dark_close
//                else R.drawable.ic_light_close
//            )?.let {
//                val imgSize = resources.getDimension(R.dimen.icon_size_remove).toInt()
//                val margin = resources.getDimension(R.dimen.three_quarters_activity_vertical_margin)
//                val bitmap = it.toBitmap(imgSize, imgSize, Bitmap.Config.ARGB_8888)
//                c.drawBitmap(
//                    bitmap,
//                    (x - 2 * bitmap.width).toFloat(),
//                    (y - bitmap.height / 2).toFloat(),
//                    paint
//                )
//            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }



        private var swipeBack = false
        private var buttonShowedState: ButtonsState = ButtonsState.GONE
        private val buttonWidth = 300f

        override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            if (swipeBack) {
                swipeBack = false
                return 0
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection)
        }


        // SwipeController.java
        @SuppressLint("ClickableViewAccessibility")
        private fun setTouchListener(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float,
            actionState: Int, isCurrentlyActive: Boolean
        ) {
            recyclerView.setOnTouchListener { v, event ->
                swipeBack =
                    event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                if (swipeBack) {
                    if (dX < -buttonWidth) buttonShowedState =
                        ButtonsState.RIGHT_VISIBLE else if (dX > buttonWidth) buttonShowedState =
                        ButtonsState.LEFT_VISIBLE
                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                        setItemsClickable(recyclerView, false)
                    }
                }
                false
            }
        }

        private fun setTouchDownListener(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float,
            actionState: Int, isCurrentlyActive: Boolean
        ) {
            recyclerView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
                false
            }
        }

        private fun setTouchUpListener(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float,
            actionState: Int, isCurrentlyActive: Boolean
        ) {
            recyclerView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        0f,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    recyclerView.setOnTouchListener { v, event -> false }
                    setItemsClickable(recyclerView, true)
                    swipeBack = false
                    buttonShowedState = ButtonsState.GONE
                }
                false
            }
        }

        private fun setItemsClickable(
            recyclerView: RecyclerView,
            isClickable: Boolean
        ) {
            for (i in 0 until recyclerView.childCount) {
                recyclerView.getChildAt(i).isClickable = isClickable
            }
        }

    }

    val touchHelper = ItemTouchHelper(simpleItemTouchCallback)
    touchHelper.attachToRecyclerView(this)
}

enum class ButtonsState {
    GONE, LEFT_VISIBLE, RIGHT_VISIBLE
}

@RequiresApi(Build.VERSION_CODES.O)
fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null,
    maxLines: Int = Int.MAX_VALUE,
    breakStrategy: Int = Layout.BREAK_STRATEGY_SIMPLE,
    hyphenationFrequency: Int = Layout.HYPHENATION_FREQUENCY_NONE,
    justificationMode: Int = Layout.JUSTIFICATION_MODE_NONE
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-$textDir-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize-" +
            "$maxLines-$breakStrategy-$hyphenationFrequency-$justificationMode"

    val staticLayout = StaticLayoutCache[cacheKey] ?: StaticLayout.Builder.obtain(
        text,
        start,
        end,
        textPaint,
        width
    )
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .setIncludePad(includePad)
        .setEllipsizedWidth(ellipsizedWidth)
        .setEllipsize(ellipsize)
        .setMaxLines(maxLines)
        .setBreakStrategy(breakStrategy)
        .setHyphenationFrequency(hyphenationFrequency)
        .setJustificationMode(justificationMode)
        .build().apply { StaticLayoutCache[cacheKey] = this }

    staticLayout.draw(this, x, y)
}

@RequiresApi(Build.VERSION_CODES.M)
fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null,
    maxLines: Int = Int.MAX_VALUE,
    breakStrategy: Int = Layout.BREAK_STRATEGY_SIMPLE,
    hyphenationFrequency: Int = Layout.HYPHENATION_FREQUENCY_NONE
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-$textDir-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize-" +
            "$maxLines-$breakStrategy-$hyphenationFrequency"

    val staticLayout = StaticLayoutCache[cacheKey] ?: StaticLayout.Builder.obtain(
        text,
        start,
        end,
        textPaint,
        width
    )
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .setIncludePad(includePad)
        .setEllipsizedWidth(ellipsizedWidth)
        .setEllipsize(ellipsize)
        .setMaxLines(maxLines)
        .setBreakStrategy(breakStrategy)
        .setHyphenationFrequency(hyphenationFrequency)
        .build().apply { StaticLayoutCache[cacheKey] = this }

    staticLayout.draw(this, x, y)
}

fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize"

    // The public constructor was deprecated in API level 28,
    // but the builder is only available from API level 23 onwards
    val staticLayout =
        StaticLayoutCache[cacheKey] ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, start, end, textPaint, width)
                .setAlignment(alignment)
                .setLineSpacing(spacingAdd, spacingMult)
                .setIncludePad(includePad)
                .setEllipsizedWidth(ellipsizedWidth)
                .setEllipsize(ellipsize)
                .build()
        } else {
            StaticLayout(
                text, start, end, textPaint, width, alignment,
                spacingMult, spacingAdd, includePad, ellipsize, ellipsizedWidth
            )
                .apply { StaticLayoutCache[cacheKey] = this }
        }

    staticLayout.draw(this, x, y)
}

private fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
    canvas.withTranslation(x, y) {
        draw(this)
    }
}

private object StaticLayoutCache {

    private const val MAX_SIZE = 50 // Arbitrary max number of cached items
    private val cache = lruCache<String, StaticLayout>(MAX_SIZE)

    operator fun set(key: String, staticLayout: StaticLayout) {
        cache.put(key, staticLayout)
    }

    operator fun get(key: String): StaticLayout? {
        return cache[key]
    }
}