package com.myhumandesignhd.ui.view.bodygraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.myhumandesignhd.App
import com.myhumandesignhd.event.BodygraphCenterClickEvent
import com.myhumandesignhd.model.Center
import com.myhumandesignhd.model.Design
import com.myhumandesignhd.model.Personality
import com.myhumandesignhd.ui.view.bodygraph.ext.ActiveLineState
import com.myhumandesignhd.ui.view.bodygraph.ext.BodygraphActiveLine
import com.myhumandesignhd.ui.view.bodygraph.ext.BodygraphActiveMultiline
import com.myhumandesignhd.ui.view.bodygraph.ext.BodygraphLine
import com.myhumandesignhd.ui.view.bodygraph.ext.BodygraphNumber
import com.myhumandesignhd.ui.view.bodygraph.ext.initFiguresPaths
import com.myhumandesignhd.ui.view.bodygraph.ext.initLines
import com.myhumandesignhd.ui.view.bodygraph.ext.initNumbers
import com.myhumandesignhd.util.spToPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class BodygraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    internal var widthInt: Int = 0
    internal var heightInt: Int = 0

    internal var cellSize = 0
    internal var squareSideSize = 0
    internal var centerX = 0

    private var centerY = 0

    internal lateinit var numbers: ArrayList<BodygraphNumber>
    internal lateinit var lines: ArrayList<BodygraphLine>

    internal lateinit var bottomSquarePoints: HashMap<SquarePoint, Point>
    internal lateinit var centerSquarePoints: HashMap<SquarePoint, Point>
    internal lateinit var topSquarePoints: HashMap<SquarePoint, Point>
    internal lateinit var rhombPoints: HashMap<RhombPoint, Point>
    internal lateinit var leftTrianglePoints: HashMap<SideTrianglePoint, Point>
    internal lateinit var rightTrianglePoints: HashMap<SideTrianglePoint, Point>
    internal lateinit var topReverseTrianglePoints: HashMap<TopTrianglePoint, Point>
    internal lateinit var topTrianglePoints: HashMap<TopTrianglePoint, Point>
    internal lateinit var strangeTrianglePoints: HashMap<StrangeTrianglePoint, Point>

    internal lateinit var bottomSquarePath: Path
    internal lateinit var centerSquarePath: Path
    internal lateinit var topSquarePath: Path
    internal lateinit var rhombPath: Path
    internal lateinit var leftTrianglePath: Path
    internal lateinit var rightTrianglePath: Path
    internal lateinit var topReverseTrianglePath: Path
    internal lateinit var topTrianglePath: Path
    internal lateinit var strangeTrianglePath: Path

    private lateinit var activeCenters: List<Center>
    private lateinit var inactiveCenters: List<Center>
    private lateinit var design: Design
    private lateinit var personality: Personality
    private var isViewReadyToSetupGatesAndChannels = false
    private var isGatesAndChannelsSetupFinished = false

    private var isTouchable = false

    private val activeGates: HashSet<Int> = hashSetOf()
    private val activeLines: MutableList<BodygraphActiveLine> = mutableListOf()
    private val activeMultilines: MutableList<BodygraphActiveMultiline> = mutableListOf()

    private var isAllowDrawLines = false
    private var isAllowDrawText = true
    private var drawLinesWithAnimation = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
    private var speedAnimationFactor = 1f

    private val bottomSquarePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeBottomSquarePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#393572"
                else "#6851DF"
            )
        )
    }

    private val centerSquarePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeCenterSquarePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#A4314B"
                else "#CA4B77"
            )
        )
    }

    private val topSquarePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeTopSquarePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#393572"
                else "#6851DF"
            )
        )
    }

    private val leftTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeLeftTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#393572"
                else "#6851DF"
            )
        )
    }

    private val rightTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeRightTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#393572"
                else "#6851DF"
            )
        )
    }

    private val rhombPaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeRhombPaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#757429"
                else "#CCCA31"
            )
        )
    }

    private val topReverseTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeTopReverseTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#567D2D"
                else "#70A538"
            )
        )
    }

    private val topTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeTopTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#757429"
                else "#CCCA31"
            )
        )
    }

    private val strangeTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#3F3F3F"
                else "#999999"
            )
        )
    }

    private val activeStrangeTrianglePaint: Paint by lazy {
        initPaint(
            Color.parseColor(
                if (App.preferences.isDarkTheme) "#A4314B"
                else "#CA4B77"
            )
        )
    }

    internal val textPaint: Paint by lazy {
        val paint = Paint()
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#80FFFFFF")
        paint.textSize = context.spToPx(6f).toFloat()//App.resourcesProvider.getDimen(R.dimen.bodygraph_text_size)

        paint
    }

    internal val activeTextPaint: Paint by lazy {
        val paint = Paint()
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.WHITE
        paint.textSize = context.spToPx(6f).toFloat()//App.resourcesProvider.getDimen(R.dimen.bodygraph_text_size)

        paint
    }

    private val linePaint: Paint by lazy {
        val paint = Paint()
        paint.color = Color.parseColor("#4D494D")
        paint.strokeWidth = 3f//4f
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        paint
    }

    private val designLinePaint: Paint by lazy {
        val paint = Paint()
        paint.color = Color.parseColor("#BC4D68")
        paint.strokeWidth = 3f//5f
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        paint
    }

    private val personalityLinePaint: Paint by lazy {
        val paint = Paint()
        paint.color = Color.parseColor("#5352BD")
        paint.strokeWidth = 3f//5f
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        paint
    }

    private val mixLinePaint: Paint by lazy {
        val paint = Paint()
        paint.color = Color.parseColor("#5352BD")
        paint.strokeWidth = 3f//5f
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true
        paint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)

        paint
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthInt = w
        heightInt = h
        Log.d("keke_size_w", w.toString())
        Log.d("keke_size_h", h.toString())

        cellSize = ((2.78f / 100) * widthInt).toInt()
//        cellSize = ((1.78f / 100) * heightInt).toInt()
        squareSideSize = 7 * cellSize

        centerX = widthInt / 2
        centerY = heightInt / 2

        GlobalScope.launch(Dispatchers.Main) {
            initFiguresPaths()
            initNumbers()
            initLines()
        }.invokeOnCompletion {
            isViewReadyToSetupGatesAndChannels = true

            if (::design.isInitialized && ::personality.isInitialized)
                setupGatesAndChannels()

            topPoint = Point(topTrianglePoints[TopTrianglePoint.Center]!!)
//            topPoint.y += 5 * cellSize
        }

//        initBase()
    }

    private var topPoint = Point()
    fun getTopPoint() = topPoint

    private fun Canvas.drawNumbers() {
        if (::numbers.isInitialized)
            numbers.forEach {
                drawText(
                    it.text,
                    it.x + 0.4f * cellSize,
                    it.y,
                    if (activeGates.contains(
                            it.text.replace(" ", "").toInt()
                        )
                    ) activeTextPaint
                    else textPaint
                )
            }
    }

    private fun Canvas.drawLines() {
        if (::lines.isInitialized) {
            lines.forEach {
                drawLine(
                    it.firstNumber.x + 0.4f * cellSize,
                    it.firstNumber.y,
                    it.secondNumber.x + 0.4f * cellSize,
                    it.secondNumber.y,
                    linePaint
                )
            }
        }
    }

    private fun Canvas.drawLinesWithAnimation() {
        var callPostInvalidate = false

        if (::lines.isInitialized) {
            lines.forEach { line ->
                val multipleIndex = 1f
                if (line.drawIndex < line.listOfPoints.size * multipleIndex) {
                    drawLine(
                        line.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                        line.listOfPoints[0].y.toFloat(),
                        line.listOfPoints[line.drawIndex].x.toFloat() + 0.4f * cellSize,
                        line.listOfPoints[line.drawIndex].y.toFloat(),
                        linePaint
                    )
                    line.drawIndex++

                    if (line.drawIndex < line.listOfPoints.size * multipleIndex) {
                        callPostInvalidate = true
                    } else {
                        drawLine(
                            line.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            line.listOfPoints[0].y.toFloat(),
                            line.listOfPoints[line.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                            line.listOfPoints[line.drawIndex - 1].y.toFloat(),
                            linePaint
                        )
                    }
                } else {
                    drawLine(
                        line.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                        line.listOfPoints[0].y.toFloat(),
                        line.listOfPoints[line.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                        line.listOfPoints[line.drawIndex - 1].y.toFloat(),
                        linePaint
                    )
                }
            }
        }

        if (callPostInvalidate) postInvalidateDelayed((15 / speedAnimationFactor).toLong())
    }

    private fun Canvas.drawBottomSquare() {
        if (::bottomSquarePath.isInitialized)
            drawPath(
                bottomSquarePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 9 }) activeBottomSquarePaint
                else bottomSquarePaint
            )
    }

    private fun Canvas.drawCenterSquare() {
        if (::centerSquarePath.isInitialized)
            drawPath(
                centerSquarePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 8 }) activeCenterSquarePaint
                else centerSquarePaint
            )
    }

    private fun Canvas.drawTopSquare() {
        if (::topSquarePath.isInitialized)
            drawPath(
                topSquarePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 3 }) activeTopSquarePaint
                else topSquarePaint
            )
    }

    private fun Canvas.drawRhomb() {
        if (::rhombPath.isInitialized)
            drawPath(
                rhombPath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 4 }) activeRhombPaint
                else rhombPaint
            )
    }

    private fun Canvas.drawLeftTriangle() {
        if (::leftTrianglePath.isInitialized)
            drawPath(
                leftTrianglePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 6 }) activeLeftTrianglePaint
                else leftTrianglePaint
            )
    }

    private fun Canvas.drawRightTriangle() {
        if (::rightTrianglePath.isInitialized)
            drawPath(
                rightTrianglePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 7 }) activeRightTrianglePaint
                else rightTrianglePaint
            )
    }

    private fun Canvas.drawTopReverseTriangle() {
        if (::topReverseTrianglePath.isInitialized)
            drawPath(
                topReverseTrianglePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 2 }) activeTopReverseTrianglePaint
                else topReverseTrianglePaint
            )
    }

    private fun Canvas.drawTopTriangle() {
        if (::topTrianglePath.isInitialized)
            drawPath(
                topTrianglePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 1 }) activeTopTrianglePaint
                else topTrianglePaint
            )
    }

    private fun Canvas.drawStrangeTriangle() {
        if (::strangeTrianglePath.isInitialized)
            drawPath(
                strangeTrianglePath,
                if (::activeCenters.isInitialized && activeCenters.any { it.id == 5 }) activeStrangeTrianglePaint
                else strangeTrianglePaint
            )
    }

    private fun Canvas.drawActiveLines() {
        var callPostInvalidate = false

        if (activeLines.isNotEmpty()) {
            activeLines.forEach { line ->
                if (line.state != ActiveLineState.Inactive) {
                    val multipleIndex =
                        if (line.state == ActiveLineState.Active) 1f
                        else 0.5f

                    if (line.drawIndex < line.listOfPoints.size * multipleIndex) {
                        drawLine(
                            line.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            line.listOfPoints[0].y.toFloat(),
                            line.listOfPoints[line.drawIndex].x.toFloat() + 0.4f * cellSize,
                            line.listOfPoints[line.drawIndex].y.toFloat(),
                            if (line.isDesign && line.isPersonality) mixLinePaint
                            else if (line.isDesign) designLinePaint
                            else personalityLinePaint
                        )
                        line.drawIndex++

                        if (line.drawIndex < line.listOfPoints.size * multipleIndex) {
                            callPostInvalidate = true

                        } else {
                            drawLine(
                                line.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                line.listOfPoints[0].y.toFloat(),
                                line.listOfPoints[line.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                line.listOfPoints[line.drawIndex - 1].y.toFloat(),
                                if (line.isDesign && line.isPersonality) mixLinePaint
                                else if (line.isDesign) designLinePaint
                                else personalityLinePaint
                            )
                        }
                    } else {
                        drawLine(
                            line.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            line.listOfPoints[0].y.toFloat(),
                            line.listOfPoints[line.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                            line.listOfPoints[line.drawIndex - 1].y.toFloat(),
                            if (line.isDesign && line.isPersonality) mixLinePaint
                            else if (line.isDesign) designLinePaint
                            else personalityLinePaint
                        )
                    }
                }
            }
        }

        if (activeMultilines.isNotEmpty()) {
            activeMultilines.forEach {
                val multipleIndex =
                    if (it.firstLine.state == ActiveLineState.Active) 1f
                    else 0.5f

                if (it.thirdLine == null) {
                    /* first line */
                    if (it.firstLine.drawIndex < it.firstLine.listOfPoints.size) {
                        drawLine(
                            it.firstLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[0].y.toFloat(),
                            it.firstLine.listOfPoints[it.firstLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[it.firstLine.drawIndex].y.toFloat(),
                            if (it.firstLine.isDesign && it.firstLine.isPersonality) mixLinePaint
                            else if (it.firstLine.isDesign) designLinePaint
                            else personalityLinePaint
                        )
                        it.firstLine.drawIndex++

                        if (it.firstLine.drawIndex < it.firstLine.listOfPoints.size) {
                            callPostInvalidate = true

                        } else {
                            drawLine(
                                it.firstLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                it.firstLine.listOfPoints[0].y.toFloat(),
                                it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].y.toFloat(),
                                if (it.firstLine.isDesign && it.firstLine.isPersonality) mixLinePaint
                                else if (it.firstLine.isDesign) designLinePaint
                                else personalityLinePaint
                            )

                            /* second line */
                            if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size * multipleIndex
                            ) {
                                drawLine(
                                    it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[0].y.toFloat(),
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex].y.toFloat(),
                                    if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                    else if (it.secondLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )
                                it.secondLine.drawIndex++

                                if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size * multipleIndex) {
                                    callPostInvalidate = true

                                } else {
                                    drawLine(
                                        it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.secondLine.listOfPoints[0].y.toFloat(),
                                        it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                        it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                        if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                        else if (it.secondLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )
                                }
                            } else {
                                drawLine(
                                    it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[0].y.toFloat(),
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                    if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                    else if (it.secondLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )
                            }

                        }
                    } else {
                        drawLine(
                            it.firstLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[0].y.toFloat(),
                            it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].y.toFloat(),
                            if (it.firstLine.isDesign && it.firstLine.isPersonality) mixLinePaint
                            else if (it.firstLine.isDesign) designLinePaint
                            else personalityLinePaint
                        )

                        /* second line */
                        if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size * multipleIndex
                        ) {
                            drawLine(
                                it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[0].y.toFloat(),
                                it.secondLine.listOfPoints[it.secondLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[it.secondLine.drawIndex].y.toFloat(),
                                if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                else if (it.secondLine.isDesign) designLinePaint
                                else personalityLinePaint
                            )
                            it.secondLine.drawIndex++

                            if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size * multipleIndex) {
                                callPostInvalidate = true

                            } else {
                                drawLine(
                                    it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[0].y.toFloat(),
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                    if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                    else if (it.secondLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )
                            }
                        } else {
                            drawLine(
                                it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[0].y.toFloat(),
                                it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                else if (it.secondLine.isDesign) designLinePaint
                                else personalityLinePaint
                            )
                        }
                    }
                } else {
                    if (it.firstLine.drawIndex < it.firstLine.listOfPoints.size) {
                        drawLine(
                            it.firstLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[0].y.toFloat(),
                            it.firstLine.listOfPoints[it.firstLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[it.firstLine.drawIndex].y.toFloat(),
                            if (it.firstLine.isDesign && it.firstLine.isPersonality) mixLinePaint
                            else if (it.firstLine.isDesign) designLinePaint
                            else personalityLinePaint
                        )
                        it.firstLine.drawIndex++

                        if (it.firstLine.drawIndex < it.firstLine.listOfPoints.size) {
                            callPostInvalidate = true

                        } else {
                            drawLine(
                                it.firstLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                it.firstLine.listOfPoints[0].y.toFloat(),
                                it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].y.toFloat(),
                                if (it.firstLine.isDesign && it.firstLine.isPersonality) mixLinePaint
                                else if (it.firstLine.isDesign) designLinePaint
                                else personalityLinePaint
                            )

                            /* second line */
                            if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size) {
                                drawLine(
                                    it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[0].y.toFloat(),
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex].y.toFloat(),
                                    if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                    else if (it.secondLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )
                                it.secondLine.drawIndex++

                                if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size) {
                                    callPostInvalidate = true

                                } else {
                                    drawLine(
                                        it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.secondLine.listOfPoints[0].y.toFloat(),
                                        it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                        it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                        if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                        else if (it.secondLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )

                                    /* third line */
                                    if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                        drawLine(
                                            it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[0].y.toFloat(),
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex].y.toFloat(),
                                            if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                            else if (it.thirdLine.isDesign) designLinePaint
                                            else personalityLinePaint
                                        )
                                        it.thirdLine.drawIndex++

                                        if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                            callPostInvalidate = true

                                        } else {
                                            drawLine(
                                                it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                                it.thirdLine.listOfPoints[0].y.toFloat(),
                                                it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                                it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                                if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                                else if (it.thirdLine.isDesign) designLinePaint
                                                else personalityLinePaint
                                            )
                                        }
                                    } else {
                                        drawLine(
                                            it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[0].y.toFloat(),
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                            if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                            else if (it.thirdLine.isDesign) designLinePaint
                                            else personalityLinePaint
                                        )
                                    }
                                }
                            } else {
                                drawLine(
                                    it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[0].y.toFloat(),
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                    if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                    else if (it.secondLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )

                                /* third line */
                                if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                    drawLine(
                                        it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[0].y.toFloat(),
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex].y.toFloat(),
                                        if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                        else if (it.thirdLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )
                                    it.thirdLine.drawIndex++

                                    if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                        callPostInvalidate = true

                                    } else {
                                        drawLine(
                                            it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[0].y.toFloat(),
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                            if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                            else if (it.thirdLine.isDesign) designLinePaint
                                            else personalityLinePaint
                                        )
                                    }
                                } else {
                                    drawLine(
                                        it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[0].y.toFloat(),
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                        if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                        else if (it.thirdLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )
                                }
                            }

                        }
                    } else {
                        drawLine(
                            it.firstLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[0].y.toFloat(),
                            it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                            it.firstLine.listOfPoints[it.firstLine.drawIndex - 1].y.toFloat(),
                            if (it.firstLine.isDesign && it.firstLine.isPersonality) mixLinePaint
                            else if (it.firstLine.isDesign) designLinePaint
                            else personalityLinePaint
                        )

                        /* second line */
                        if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size) {
                            drawLine(
                                it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[0].y.toFloat(),
                                it.secondLine.listOfPoints[it.secondLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[it.secondLine.drawIndex].y.toFloat(),
                                if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                else if (it.secondLine.isDesign) designLinePaint
                                else personalityLinePaint
                            )
                            it.secondLine.drawIndex++

                            if (it.secondLine.drawIndex < it.secondLine.listOfPoints.size) {
                                callPostInvalidate = true

                            } else {
                                drawLine(
                                    it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[0].y.toFloat(),
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                    it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                    if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                    else if (it.secondLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )

                                /* third line */
                                if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                    drawLine(
                                        it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[0].y.toFloat(),
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex].y.toFloat(),
                                        if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                        else if (it.thirdLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )
                                    it.thirdLine.drawIndex++

                                    if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                        callPostInvalidate = true

                                    } else {
                                        drawLine(
                                            it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[0].y.toFloat(),
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                            it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                            if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                            else if (it.thirdLine.isDesign) designLinePaint
                                            else personalityLinePaint
                                        )
                                    }
                                } else {
                                    drawLine(
                                        it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[0].y.toFloat(),
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                        if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                        else if (it.thirdLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )
                                }
                            }
                        } else {
                            drawLine(
                                it.secondLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[0].y.toFloat(),
                                it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                it.secondLine.listOfPoints[it.secondLine.drawIndex - 1].y.toFloat(),
                                if (it.secondLine.isDesign && it.secondLine.isPersonality) mixLinePaint
                                else if (it.secondLine.isDesign) designLinePaint
                                else personalityLinePaint
                            )

                            /* third line */
                            if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                drawLine(
                                    it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.thirdLine.listOfPoints[0].y.toFloat(),
                                    it.thirdLine.listOfPoints[it.thirdLine.drawIndex].x.toFloat() + 0.4f * cellSize,
                                    it.thirdLine.listOfPoints[it.thirdLine.drawIndex].y.toFloat(),
                                    if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                    else if (it.thirdLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )
                                it.thirdLine.drawIndex++

                                if (it.thirdLine.drawIndex < it.thirdLine.listOfPoints.size * multipleIndex) {
                                    callPostInvalidate = true

                                } else {
                                    drawLine(
                                        it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[0].y.toFloat(),
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                        it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                        if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                        else if (it.thirdLine.isDesign) designLinePaint
                                        else personalityLinePaint
                                    )
                                }
                            } else {
                                drawLine(
                                    it.thirdLine.listOfPoints[0].x.toFloat() + 0.4f * cellSize,
                                    it.thirdLine.listOfPoints[0].y.toFloat(),
                                    it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].x.toFloat() + 0.4f * cellSize,
                                    it.thirdLine.listOfPoints[it.thirdLine.drawIndex - 1].y.toFloat(),
                                    if (it.thirdLine.isDesign && it.thirdLine.isPersonality) mixLinePaint
                                    else if (it.thirdLine.isDesign) designLinePaint
                                    else personalityLinePaint
                                )
                            }
                        }
                    }
                }
            }
        }

        if (callPostInvalidate)
            postInvalidateDelayed((15 / speedAnimationFactor).toLong())
    }

    private fun initPaint(color: Int): Paint {
        val paint = Paint()

        paint.strokeWidth = 4f
        paint.color = color
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

        return paint
    }

    fun setupData(
        design: Design,
        personality: Personality,
        activeCenters: List<Center>,
        inactiveCenters: List<Center>,
        isTouchable: Boolean = false
    ) {
        if (design.channels.isNotEmpty()) {
            this.design = design
        }

        if (personality.channels.isNotEmpty())
            this.personality = personality

        this.activeCenters = activeCenters
        this.inactiveCenters = inactiveCenters

        this.isTouchable = isTouchable

        if (
            isViewReadyToSetupGatesAndChannels
            && design.channels.isNotEmpty()
            && personality.channels.isNotEmpty()
        )
            setupGatesAndChannels()
    }

    fun changeIsAllowDrawLinesState(isAllow: Boolean) {
        isAllowDrawLines = isAllow
        invalidate()
    }

    fun changeIsAllowDrawTextState(isAllow: Boolean) {
        isAllowDrawText = isAllow
        invalidate()
    }

    fun changeIsDrawLinesWithAnimation(withAnimation: Boolean) {
        drawLinesWithAnimation = withAnimation && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
    }

    fun changeSpeedAnimationFactor(factor: Float) {
        speedAnimationFactor = factor
    }

    private fun setupGatesAndChannels() {
        if (isGatesAndChannelsSetupFinished) return

        activeLines.clear()
        activeMultilines.clear()

//        isGatesAndChannelsSetupFinished = true
        design.gates.forEach { activeGates.add(it) }
        personality.gates.forEach { activeGates.add(it) }

        design.channels.keys.forEach { key ->
            var line = lines.find {
                it.firstNumber.text.replace(" ", "") == key.split("-")[0]
                        && it.secondNumber.text.replace(" ", "") == key.split("-")[1]
            }

            if (line == null)
                line = lines.find {
                    it.firstNumber.text.replace(" ", "") == key.split("-")[1]
                            && it.secondNumber.text.replace(" ", "") == key.split("-")[0]
                }

            if (line != null) {
                if (design.channels[key]!!.gate == line.secondNumber.text.replace(" ", "")) {
                    val firstNumber = line.firstNumber
                    val secondNumber = line.secondNumber

                    line = line.copy(
                        firstNumber = secondNumber,
                        secondNumber = firstNumber
                    )
                }

                val state =
                    when (design.channels[key]!!.state) {
                        ActiveLineState.HalfActive.state -> ActiveLineState.HalfActive
                        ActiveLineState.Active.state -> ActiveLineState.Active
                        else -> ActiveLineState.Inactive
                    }

                if (
                    activeLines.none {
                        it.line.firstNumber.text == line.firstNumber.text
                                && it.line.secondNumber.text == line.secondNumber.text
                    }
                ) {
                    activeLines.add(
                        BodygraphActiveLine(
                            line,
                            isDesign = true,
                            isPersonality = false,
                            state,
                            divideLineIntoEqualParts(
                                line.firstNumber.x,
                                line.firstNumber.y,
                                line.secondNumber.x,
                                line.secondNumber.y
                            )
                        )
                    )
                } else {
                    activeLines.remove(
                        activeLines.first {
                            it.line.firstNumber.text == line.firstNumber.text
                                    && it.line.secondNumber.text == line.secondNumber.text
                        }
                    )

                    activeLines.add(
                        BodygraphActiveLine(
                            line,
                            isDesign = true,
                            isPersonality = false,
                            state,
                            divideLineIntoEqualParts(
                                line.firstNumber.x,
                                line.firstNumber.y,
                                line.secondNumber.x,
                                line.secondNumber.y
                            )
                        )
                    )
                }
            } else {
                /* it means MULTILINE */

                val firstNumber = key.split("-")[0]
                val secondNumber = key.split("-")[1]
                val gate = design.channels[key]!!.gate
                val state =
                    when (design.channels[key]!!.state) {
                        ActiveLineState.HalfActive.state -> ActiveLineState.HalfActive
                        ActiveLineState.Active.state -> ActiveLineState.Active
                        else -> ActiveLineState.Inactive
                    }

                when {
                    (firstNumber == "10" && secondNumber == "20") -> {

                        if (gate == "20") {
                            val firstLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                firstNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            val secondLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            firstLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val secondLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                firstNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        }


                    }
                    (firstNumber == "20" && secondNumber == "34") -> {
                        var firstLine = lines.find {
                            it.firstNumber.text == "57" && it.secondNumber.text == "20"
                        }!!.copy(
                            firstNumber = lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!.secondNumber
                        )

                        var secondLine =
                            lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                .copy()

                        if (gate == secondNumber) {
                            val temp = firstLine.copy()
                            firstLine = secondLine.copy()
                            secondLine = temp

                            secondLine.let {
                                val tempNumber = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = tempNumber

                                val secondNumberY = it.secondNumber.y
                                it.secondNumber = it.secondNumber.copy(
                                    y = secondNumberY + 3
                                )
                            }
                        } else {
                            firstLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y
                                it.secondNumber = it.secondNumber.copy(
                                    y = secondNumberY + 3,
                                    x = secondNumberX - 2
                                )
                            }
                        }

                        secondLine.let {
                            val temp = it.firstNumber
                            it.firstNumber = it.secondNumber
                            it.secondNumber = temp
                        }

                        activeMultilines.add(
                            BodygraphActiveMultiline(
                                BodygraphActiveLine(
                                    firstLine,
                                    isDesign = true,
                                    isPersonality = false,
                                    state,
                                    divideLineIntoEqualParts(
                                        firstLine.firstNumber.x,
                                        firstLine.firstNumber.y,
                                        firstLine.secondNumber.x,
                                        firstLine.secondNumber.y
                                    )
                                ),
                                BodygraphActiveLine(
                                    secondLine,
                                    isDesign = true,
                                    isPersonality = false,
                                    state,
                                    divideLineIntoEqualParts(
                                        secondLine.firstNumber.x,
                                        secondLine.firstNumber.y,
                                        secondLine.secondNumber.x,
                                        secondLine.secondNumber.y
                                    )
                                )
                            )
                        )
                    }
                    (firstNumber == "10" && secondNumber == "57") -> {
                        if (gate == "57") {
                            val firstLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            val secondLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y
                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX - 3,
                                    y = firstNumberY + 2
                                )
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val secondLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y
                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX + 3,
                                    y = firstNumberY - 3
                                )
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        }


                    }
                    (firstNumber == "34" && secondNumber == "57") -> {
                        if (gate == "57") {
                            val firstLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!.secondNumber
                            )

                            val secondLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            firstLine.let {
                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y

                                it.secondNumber = it.secondNumber.copy(
                                    x = secondNumberX + 2,
                                    y = secondNumberY - 2
                                )
                            }

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

//                                val firstNumberX = it.firstNumber.x
//                                val firstNumberY = it.firstNumber.y
//                                it.firstNumber = it.firstNumber.copy(
//                                    x = firstNumberX - 3,
//                                    y = firstNumberY + 2
//                                )
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            val secondLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!.secondNumber
                            )

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y
                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX + 3,
                                    y = firstNumberY - 3
                                )
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        }


                    }
                    (firstNumber == "10" && secondNumber == "34") -> {
                        if (gate == "34") {
                            val thirdLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val firstLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            val secondLineFirstNumber = firstLine.copy().secondNumber
                            val secondLineSecondNumber = thirdLine.copy().secondNumber

                            val secondLine = BodygraphLine(
                                firstNumber = secondLineFirstNumber,
                                secondNumber = secondLineSecondNumber
                            )

                            secondLine.let {
                                val firstNumberY = it.firstNumber.y

                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y

                                it.firstNumber = it.firstNumber.copy(
                                    y = firstNumberY + 3
                                )

                                it.secondNumber = it.secondNumber.copy(
                                    x = secondNumberX + 1,
                                    y = secondNumberY - 2
                                )
                            }

                            thirdLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        thirdLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            thirdLine.firstNumber.x,
                                            thirdLine.firstNumber.y,
                                            thirdLine.secondNumber.x,
                                            thirdLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val thirdLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            val secondLineFirstNumber = firstLine.copy().secondNumber
                            val secondLineSecondNumber = thirdLine.copy().secondNumber

                            val secondLine = BodygraphLine(
                                firstNumber = secondLineFirstNumber,
                                secondNumber = secondLineSecondNumber
                            )

                            secondLine.let {
                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y

                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y

                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX + 1,
                                    y = firstNumberY - 2
                                )

                                it.secondNumber = it.secondNumber.copy(
                                    x = secondNumberX - 1,
                                    y = secondNumberY + 3
                                )
                            }

                            thirdLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        thirdLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            thirdLine.firstNumber.x,
                                            thirdLine.firstNumber.y,
                                            thirdLine.secondNumber.x,
                                            thirdLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        }


                    }
                }
            }
        }

        personality.channels.keys.forEach { key ->
            var personalityLine = lines.find {
                it.firstNumber.text.replace(" ", "") == key.split("-")[0]
                        && it.secondNumber.text.replace(" ", "") == key.split("-")[1]
            }

            if (personalityLine == null)
                personalityLine = lines.find {
                    it.firstNumber.text.replace(" ", "") == key.split("-")[1]
                            && it.secondNumber.text.replace(" ", "") == key.split("-")[0]
                }

            if (personalityLine != null) {
                if (personality.channels[key]!!.gate == personalityLine.secondNumber.text.replace(
                        " ",
                        ""
                    )
                ) {
                    val firstNumber = personalityLine.firstNumber
                    val secondNumber = personalityLine.secondNumber

                    personalityLine = personalityLine.copy(
                        firstNumber = secondNumber,
                        secondNumber = firstNumber
                    )
                }

                val state =
                    when (personality.channels[key]!!.state) {
                        ActiveLineState.HalfActive.state -> ActiveLineState.HalfActive
                        ActiveLineState.Active.state -> ActiveLineState.Active
                        else -> ActiveLineState.Inactive
                    }

                if (
                    activeLines.none {
                        it.line.firstNumber.text == personalityLine.firstNumber.text
                                && it.line.secondNumber.text == personalityLine.secondNumber.text
                    }
                ) {
                    activeLines.add(
                        BodygraphActiveLine(
                            personalityLine,
                            isDesign = false,
                            isPersonality = true,
                            state,
                            divideLineIntoEqualParts(
                                personalityLine.firstNumber.x,
                                personalityLine.firstNumber.y,
                                personalityLine.secondNumber.x,
                                personalityLine.secondNumber.y
                            )
                        )
                    )
                } else {
                    activeLines.find {
                        it.line.firstNumber.text == personalityLine.firstNumber.text
                                && it.line.secondNumber.text == personalityLine.secondNumber.text
                    }?.let { foundLine ->
                        activeLines.add(
                            foundLine.copy(
                                isPersonality = true
                            )
                        )
                    }
                }

            } else {
                /* it means MULTILINE */

                val firstNumber = key.split("-")[0]
                val secondNumber = key.split("-")[1]
                val gate = personality.channels[key]!!.gate
                val state =
                    when (personality.channels[key]!!.state) {
                        ActiveLineState.HalfActive.state -> ActiveLineState.HalfActive
                        ActiveLineState.Active.state -> ActiveLineState.Active
                        else -> ActiveLineState.Inactive
                    }

                when {
                    (firstNumber == "10" && secondNumber == "20") -> {

                        if (gate == "20") {
                            val firstLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                firstNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            val secondLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            firstLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val secondLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                firstNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        }


                    }
                    (firstNumber == "20" && secondNumber == "34") -> {
                        var firstLine = lines.find {
                            it.firstNumber.text == "57" && it.secondNumber.text == "20"
                        }!!.copy(
                            firstNumber = lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!.secondNumber
                        )

                        var secondLine =
                            lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                .copy()

                        if (gate == secondNumber) {
                            val temp = firstLine.copy()
                            firstLine = secondLine.copy()
                            secondLine = temp

                            secondLine.let {
                                val tempNumber = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = tempNumber

                                val secondNumberY = it.secondNumber.y
                                it.secondNumber = it.secondNumber.copy(
                                    y = secondNumberY + 3
                                )
                            }
                        } else {
                            firstLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y
                                it.secondNumber = it.secondNumber.copy(
                                    y = secondNumberY + 3,
                                    x = secondNumberX - 2
                                )
                            }
                        }

                        secondLine.let {
                            val temp = it.firstNumber
                            it.firstNumber = it.secondNumber
                            it.secondNumber = temp
                        }

                        if (
                            activeMultilines.none {
                                it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                        && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                        && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                        && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                            }
                        ) {
                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = false,
                                        isPersonality = true,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = false,
                                        isPersonality = true,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        } else {
                            activeMultilines.find {
                                it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                        && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                        && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                        && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                            }?.let { foundMultiline ->
                                activeMultilines.add(
                                    foundMultiline.copy(
                                        firstLine = foundMultiline.firstLine.copy(
                                            isPersonality = true
                                        ),
                                        secondLine = foundMultiline.secondLine.copy(
                                            isPersonality = true
                                        )
                                    )
                                )
                            }
                        }
                    }
                    (firstNumber == "10" && secondNumber == "57") -> {
                        if (gate == "57") {
                            val firstLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            val secondLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y
                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX - 3,
                                    y = firstNumberY + 2
                                )
                            }

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val secondLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!.secondNumber
                            )

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y
                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX + 3,
                                    y = firstNumberY - 3
                                )
                            }

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                    (firstNumber == "34" && secondNumber == "57") -> {
                        if (gate == "57") {
                            val firstLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!.secondNumber
                            )

                            val secondLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            firstLine.let {
                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y

                                it.secondNumber = it.secondNumber.copy(
                                    x = secondNumberX + 2,
                                    y = secondNumberY - 2
                                )
                            }

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            val secondLine = lines.find {
                                it.firstNumber.text == "57" && it.secondNumber.text == "20"
                            }!!.copy(
                                secondNumber = lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!.secondNumber
                            )

                            secondLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp

                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y
                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX + 3,
                                    y = firstNumberY - 3
                                )
                            }

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text

                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = false,
                                            isPersonality = true,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                    (firstNumber == "10" && secondNumber == "34") -> {
                        if (gate == "34") {
                            val thirdLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val firstLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            val secondLineFirstNumber = firstLine.copy().secondNumber
                            val secondLineSecondNumber = thirdLine.copy().secondNumber

                            val secondLine = BodygraphLine(
                                firstNumber = secondLineFirstNumber,
                                secondNumber = secondLineSecondNumber
                            )

                            secondLine.let {
                                val firstNumberY = it.firstNumber.y

                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y

                                it.firstNumber = it.firstNumber.copy(
                                    y = firstNumberY + 3
                                )

                                it.secondNumber = it.secondNumber.copy(
                                    x = secondNumberX + 1,
                                    y = secondNumberY - 2
                                )
                            }

                            thirdLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            if (
                                activeMultilines.none {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                            && it.thirdLine != null
                                            && it.thirdLine.line.firstNumber.text == thirdLine.firstNumber.text
                                            && it.thirdLine.line.secondNumber.text == thirdLine.secondNumber.text
                                }
                            ) {
                                activeMultilines.add(
                                    BodygraphActiveMultiline(
                                        BodygraphActiveLine(
                                            firstLine,
                                            isDesign = true,
                                            isPersonality = false,
                                            state,
                                            divideLineIntoEqualParts(
                                                firstLine.firstNumber.x,
                                                firstLine.firstNumber.y,
                                                firstLine.secondNumber.x,
                                                firstLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            secondLine,
                                            isDesign = true,
                                            isPersonality = false,
                                            state,
                                            divideLineIntoEqualParts(
                                                secondLine.firstNumber.x,
                                                secondLine.firstNumber.y,
                                                secondLine.secondNumber.x,
                                                secondLine.secondNumber.y
                                            )
                                        ),
                                        BodygraphActiveLine(
                                            thirdLine,
                                            isDesign = true,
                                            isPersonality = false,
                                            state,
                                            divideLineIntoEqualParts(
                                                thirdLine.firstNumber.x,
                                                thirdLine.firstNumber.y,
                                                thirdLine.secondNumber.x,
                                                thirdLine.secondNumber.y
                                            )
                                        )
                                    )
                                )
                            } else {
                                activeMultilines.find {
                                    it.firstLine.line.firstNumber.text == firstLine.firstNumber.text
                                            && it.firstLine.line.secondNumber.text == firstLine.secondNumber.text
                                            && it.secondLine.line.firstNumber.text == secondLine.firstNumber.text
                                            && it.secondLine.line.secondNumber.text == secondLine.secondNumber.text
                                            && it.thirdLine != null
                                            && it.thirdLine.line.firstNumber.text == thirdLine.firstNumber.text
                                            && it.thirdLine.line.secondNumber.text == thirdLine.secondNumber.text
                                }?.let { foundMultiline ->
                                    activeMultilines.add(
                                        foundMultiline.copy(
                                            firstLine = foundMultiline.firstLine.copy(
                                                isPersonality = true
                                            ),
                                            secondLine = foundMultiline.secondLine.copy(
                                                isPersonality = true
                                            ),
                                            thirdLine = foundMultiline.thirdLine!!.copy(
                                                isPersonality = true
                                            )
                                        )
                                    )
                                }
                            }
                        } else {
                            val firstLine =
                                lines.find { it.firstNumber.text == "10" && it.secondNumber.text == "10" }!!
                                    .copy()

                            val thirdLine =
                                lines.find { it.firstNumber.text == "34" && it.secondNumber.text == "34" }!!
                                    .copy()

                            val secondLineFirstNumber = firstLine.copy().secondNumber
                            val secondLineSecondNumber = thirdLine.copy().secondNumber

                            val secondLine = BodygraphLine(
                                firstNumber = secondLineFirstNumber,
                                secondNumber = secondLineSecondNumber
                            )

                            secondLine.let {
                                val firstNumberX = it.firstNumber.x
                                val firstNumberY = it.firstNumber.y

                                val secondNumberX = it.secondNumber.x
                                val secondNumberY = it.secondNumber.y

                                it.firstNumber = it.firstNumber.copy(
                                    x = firstNumberX + 1,
                                    y = firstNumberY - 2
                                )

                                it.secondNumber = it.secondNumber.copy(
                                    x = secondNumberX - 1,
                                    y = secondNumberY + 3
                                )
                            }

                            thirdLine.let {
                                val temp = it.firstNumber
                                it.firstNumber = it.secondNumber
                                it.secondNumber = temp
                            }

                            activeMultilines.add(
                                BodygraphActiveMultiline(
                                    BodygraphActiveLine(
                                        firstLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            firstLine.firstNumber.x,
                                            firstLine.firstNumber.y,
                                            firstLine.secondNumber.x,
                                            firstLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        secondLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            secondLine.firstNumber.x,
                                            secondLine.firstNumber.y,
                                            secondLine.secondNumber.x,
                                            secondLine.secondNumber.y
                                        )
                                    ),
                                    BodygraphActiveLine(
                                        thirdLine,
                                        isDesign = true,
                                        isPersonality = false,
                                        state,
                                        divideLineIntoEqualParts(
                                            thirdLine.firstNumber.x,
                                            thirdLine.firstNumber.y,
                                            thirdLine.secondNumber.x,
                                            thirdLine.secondNumber.y
                                        )
                                    )
                                )
                            )
                        }


                    }
                }
            }
        }

        invalidate()
    }

    fun divideLineIntoEqualParts(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
    ): ArrayList<Point> {
        val list = arrayListOf<Point>()

        val mult =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) 150
        else 15 // FIXME
        /*
         * (x,y) = (x1 + k(x2 - x1),y1 + k(y2 - y1))
         * */
        for (k in 1..mult) {
            list.add(
                Point(
                    (startX + k * (endX - startX) / mult).toInt(),
                    (startY + k * (endY - startY) / mult).toInt()
                )
            )
        }

        return list
    }

    var clipBoundsCanvas: Rect = Rect()

    override fun onDraw(canvas: Canvas) {
        synchronized(canvas) {
            clipBoundsCanvas = canvas.clipBounds
            if (!isTouchable)
                canvas.scale(0.6f, 0.6f, centerX.toFloat(), heightInt.toFloat() - heightInt / 3)
            else {
//            canvas.translate(centerX.toFloat(), 0f)
                canvas.scale(0.99f, 0.99f, centerX.toFloat(), 0f)
            }

            if (isAllowDrawLines) {
                if (drawLinesWithAnimation) {
                    canvas.drawLinesWithAnimation()
                } else {
                    canvas.drawLines()
                }

                canvas.drawActiveLines()
            }

            canvas.drawBottomSquare()
            canvas.drawLeftTriangle()
            canvas.drawRightTriangle()
            canvas.drawCenterSquare()
            canvas.drawRhomb()
            canvas.drawTopSquare()
            canvas.drawTopReverseTriangle()
            canvas.drawTopTriangle()
            canvas.drawStrangeTriangle()

            if (isAllowDrawText)
                canvas.drawNumbers()
        }
    }

    private fun touchNotScaled(event: MotionEvent) {
        kotlin.runCatching {
            val centers = mutableListOf<Center>()
            centers.addAll(activeCenters)
            centers.addAll(inactiveCenters)

            val x = event.x
            val y = event.y

            val bottomSquareBounds = RectF()
            val scaledBottomSquarePath = Path(bottomSquarePath)

            bottomSquarePath.computeBounds(bottomSquareBounds, true)

            val centerSquareBounds = RectF()
            centerSquarePath.computeBounds(centerSquareBounds, true)

            val topSquareBounds = RectF()
            topSquarePath.computeBounds(topSquareBounds, true)

            val leftTriangleBounds = RectF()
            leftTrianglePath.computeBounds(leftTriangleBounds, true)

            val rightTriangleBounds = RectF()
            rightTrianglePath.computeBounds(rightTriangleBounds, true)

            val rhombBounds = RectF()
            rhombPath.computeBounds(rhombBounds, true)

            val topReverseTriangleBounds = RectF()
            topReverseTrianglePath.computeBounds(topReverseTriangleBounds, true)

            val topTriangleBounds = RectF()
            topTrianglePath.computeBounds(topTriangleBounds, true)

            val strangeTriangleBounds = RectF()
            strangeTrianglePath.computeBounds(strangeTriangleBounds, true)

            when {
                strangeTriangleBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 5 }!!.name,
                            desc = centers.find { it.id == 5 }!!.shortDescription,
                            x = strangeTrianglePoints[StrangeTrianglePoint.Top]!!.x.toFloat(),
                            y = strangeTrianglePoints[StrangeTrianglePoint.Top]!!.y.toFloat(),
                            alignTop = true,
                            arrowPosition = 0.6f
                        )
                    )
                }
                bottomSquareBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 9 }!!.name,
                            desc = centers.find { it.id == 9 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = bottomSquarePoints[SquarePoint.LeftTop]!!.y.toFloat(),
                            alignTop = true
                        )
                    )
                }
                centerSquareBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 8 }!!.name,
                            desc = centers.find { it.id == 8 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = centerSquarePoints[SquarePoint.LeftTop]!!.y.toFloat(),
                            alignTop = true
                        )
                    )
                }
                topSquareBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 3 }!!.name,
                            desc = centers.find { it.id == 3 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = topSquarePoints[SquarePoint.LeftBottom]!!.y.toFloat(),
                            alignTop = false
                        )
                    )
                }
                leftTriangleBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 6 }!!.name,
                            desc = centers.find { it.id == 6 }!!.shortDescription,
                            x = (leftTrianglePoints[SideTrianglePoint.Center]!!.x.toFloat()),
                            y = leftTrianglePoints[SideTrianglePoint.Top]!!.y.toFloat(),
                            alignTop = true,
                            arrowPosition = 0.35f,
                            xOffset = 0,
                            isLeftTriangle = true
                        )
                    )
                }
                rightTriangleBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 7 }!!.name,
                            desc = centers.find { it.id == 7 }!!.shortDescription,
                            x =  (rightTrianglePoints[SideTrianglePoint.Center]!!.x.toFloat()),
                            y = rightTrianglePoints[SideTrianglePoint.Center]!!.y.toFloat(),
                            alignTop = true,
                            arrowPosition = 0.6f,
                            xOffset = 0,
                            isRightTriangle = true
                        )
                    )
                }
                rhombBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 4 }!!.name,
                            desc = centers.find { it.id == 4 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = rhombPoints[RhombPoint.Top]!!.y.toFloat(),
                            alignTop = true
                        )
                    )
                }
                topReverseTriangleBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 2 }!!.name,
                            desc = centers.find { it.id == 2 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = topReverseTrianglePoints[TopTrianglePoint.Center]!!.y.toFloat(),
                            alignTop = false
                        )
                    )
                }
                topTriangleBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 1 }!!.name,
                            desc = centers.find { it.id == 1 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = topTrianglePoints[TopTrianglePoint.Left]!!.y.toFloat(),
                            alignTop = false
                        )
                    )
                }
            }
        }
    }

    private fun translateCoordinates(coordinates: FloatArray): FloatArray {
        val matrix = Matrix()
        matrix.preScale(0.6f, 0.6f)
        matrix.preTranslate(centerX.toFloat(), heightInt.toFloat() - heightInt / 3)
        matrix.invert(matrix)
        matrix.mapPoints(coordinates)
        return coordinates
    }

    private fun touchScaled(event: MotionEvent) {
        runCatching {


            val centers = mutableListOf<Center>()
            centers.addAll(activeCenters)
            centers.addAll(inactiveCenters)

            val actualCoords = translateCoordinates(floatArrayOf(event.x, event.y))
            val x = event.x
            val y = event.y

            val scaleMatrix = Matrix()


            val bottomSquareBounds = RectF()
            bottomSquarePath.computeBounds(bottomSquareBounds, true)

            val centerSquareBounds = RectF()
            centerSquarePath.computeBounds(centerSquareBounds, true)

            val topSquareBounds = RectF()
            topSquarePath.computeBounds(topSquareBounds, true)

            val leftTriangleBounds = RectF()
            leftTrianglePath.computeBounds(leftTriangleBounds, true)

            val rightTriangleBounds = RectF()
            rightTrianglePath.computeBounds(rightTriangleBounds, true)

            val rhombBounds = RectF()
            rhombPath.computeBounds(rhombBounds, true)

            val topReverseTriangleBounds = RectF()
            topReverseTrianglePath.computeBounds(topReverseTriangleBounds, true)

            val topTriangleBounds = RectF()
            topTrianglePath.computeBounds(topTriangleBounds, true)

            val strangeTriangleBounds = RectF()
            strangeTrianglePath.computeBounds(strangeTriangleBounds, true)

            when {
                strangeTriangleBounds.contains(x + 2 * cellSize, y - 4 * cellSize) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 5 }!!.name,
                            desc = centers.find { it.id == 5 }!!.shortDescription,
                            x = strangeTrianglePoints[StrangeTrianglePoint.Top]!!.x.toFloat() + 5 * cellSize,
                            y = strangeTrianglePoints[StrangeTrianglePoint.Top]!!.y.toFloat() + 3 * cellSize,
                            alignTop = true,
                            arrowPosition = 0.5f
                        )
                    )
                }

                bottomSquareBounds.contains(x, y + 4 * cellSize) -> {
                    runCatching {
                        EventBus.getDefault().post(
                            BodygraphCenterClickEvent(
                                title = centers.find { it.id == 9 }!!.name,
                                desc = centers.find { it.id == 9 }!!.shortDescription,
                                isXCenter = true,
                                x = centerX.toFloat(),
                                y = bottomSquarePoints[SquarePoint.LeftTop]!!.y.toFloat() - 3 * cellSize,
                                alignTop = true
                            )
                        )
                    }
                }

                centerSquareBounds.contains(x, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 8 }!!.name,
                            desc = centers.find { it.id == 8 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = centerSquarePoints[SquarePoint.LeftTop]!!.y.toFloat(),
                            alignTop = true
                        )
                    )
                }

                topSquareBounds.contains(x, y - 8 * cellSize) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 3 }!!.name,
                            desc = centers.find { it.id == 3 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = topSquarePoints[SquarePoint.LeftBottom]!!.y.toFloat() + 6 * cellSize,
                            alignTop = false
                        )
                    )
                }

                leftTriangleBounds.contains(x - 5 * cellSize, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 6 }!!.name,
                            desc = centers.find { it.id == 6 }!!.shortDescription,
                            x = (
                                    leftTrianglePoints[SideTrianglePoint.Center]!!.x.toFloat() * 0.6f + cellSize
                                    ),
                            y = leftTrianglePoints[SideTrianglePoint.Top]!!.y.toFloat() + cellSize,
                            alignTop = true,
                            arrowPosition = 0.4f,
                            isLeftTriangle = true
                        )
                    )
                }

                rightTriangleBounds.contains(x + 5 * cellSize, y) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 7 }!!.name,
                            desc = centers.find { it.id == 7 }!!.shortDescription,
                            x = (
                                    rightTrianglePoints[SideTrianglePoint.Center]!!.x.toFloat() * 0.6f - cellSize
                                    ),
                            y = leftTrianglePoints[SideTrianglePoint.Top]!!.y.toFloat() + cellSize,
                            alignTop = true,
                            arrowPosition = 0.7f,
                            isRightTriangle = true
                        )
                    )
                }

                rhombBounds.contains(x, y - 4 * cellSize) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 4 }!!.name,
                            desc = centers.find { it.id == 4 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = rhombPoints[RhombPoint.Top]!!.y.toFloat() + 6 * cellSize,
                            alignTop = true
                        )
                    )
                }

                topTriangleBounds.contains(x, y - 14 * cellSize) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 1 }!!.name,
                            desc = centers.find { it.id == 1 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = topTrianglePoints[TopTrianglePoint.Left]!!.y.toFloat() + 14 * cellSize,
                            alignTop = false
                        )
                    )
                }

                topReverseTriangleBounds.contains(x, y - 8 * cellSize) -> {
                    EventBus.getDefault().post(
                        BodygraphCenterClickEvent(
                            title = centers.find { it.id == 2 }!!.name,
                            desc = centers.find { it.id == 2 }!!.shortDescription,
                            isXCenter = true,
                            x = centerX.toFloat(),
                            y = topReverseTrianglePoints[TopTrianglePoint.Center]!!.y.toFloat() + 9 * cellSize,
                            alignTop = false
                        )
                    )
                }

                else -> {}

            }

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_UP -> {
                if (!isTouchable) {
                    touchScaled(event)
                } else {
                    touchNotScaled(event)
                }
            }
        }

        return true
    }

    enum class SquarePoint {
        LeftBottom,
        RightBottom,
        LeftTop,
        RightTop
    }

    enum class RhombPoint {
        Bottom,
        Top,
        Left,
        Right
    }

    enum class TopTrianglePoint {
        Left,
        Right,
        Center
    }

    enum class SideTrianglePoint {
        Bottom,
        Top,
        Center
    }

    enum class StrangeTrianglePoint {
        Top,
        Left,
        Right
    }

}

