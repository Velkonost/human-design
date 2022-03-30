package ru.get.hd.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.graphics.Path.FillType
import android.util.Log
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.PathMeasure
import android.graphics.DashPathEffect
import android.graphics.PointF











class BodygraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var mPaint: Paint? = null

//    private var widthInDp: Float = 0f
//    private var heightInDp: Float = 0f

    private var widthInt: Int = 0
    private var heightInt: Int = 0


    private val mIntervals = floatArrayOf(0f, 0f)
    private val drawSpeed = 2f
    private val currentPath = -1
    private val mPathMeasure = PathMeasure()
    private val mListPath: ArrayList<Path> = ArrayList<Path>(0)

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f

    private var cellSize = 0
    private var squareSideSize = 0
    private var centerX = 0

    private var listOfPoints: ArrayList<Point> = ArrayList()
    private var inte = 0

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.color = Color.BLUE
        mPaint!!.strokeWidth = 10f
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        mPaint!!.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthInt = w
        heightInt = h

        cellSize = ((2.78f / 100) * widthInt).toInt()
        squareSideSize = 7 * cellSize
        centerX = widthInt / 2

        startX = (0 + (cellSize / 2)).toFloat()
        startY = (heightInt - (squareSideSize * 1.125f) - cellSize).toFloat()

        endX = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat()
        endY = (heightInt - (cellSize)).toFloat()

        divideLineIntoEqualParts()
    }


    private fun divideLineIntoEqualParts() {

/*
 * Courtesy : www.dummies.com
 * (x,y) = (x1 + k(x2 - x1),y1 + k(y2 - y1))
 * */
        listOfPoints.clear()
        for (k in 1..50) {
            listOfPoints.add(Point((startX + k * (endX - startX) / 50).toInt(), (startY + k * (endY - startY) / 50).toInt()))
        }
//        Log.d("listOfPoints : size : ", listOfPoints.size().toString() + "")
    }

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()


//        Log.d("keke_displ_width", display.width.toString()) // all screen
//        Log.d("keke_displ_height", display.height.toString()) // all screen


//        paint.color = Color.BLACK
//        canvas.drawPaint(paint)


        paint.strokeWidth = 4f
        paint.color = Color.RED
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true

//        val centerX = widthInt / 2

//        val cellSize = ((2.78f / 100) * widthInt).toInt() //(squareSideSize / 8).toInt()

//        val squareSideSize = 7 * cellSize//((22.22f / 100) * widthInt).toInt()













        val linePaint = Paint()
        linePaint.color = Color.GREEN
        linePaint.strokeWidth = 4f
        linePaint.style = Paint.Style.FILL_AND_STROKE
        linePaint.isAntiAlias = true



        val lineFrom18To58Path = Path()
        lineFrom18To58Path.fillType = FillType.EVEN_ODD

        lineFrom18To58Path.moveTo(
            (0 + (cellSize / 2)).toFloat(),
            (heightInt - (squareSideSize * 1.125f) - cellSize).toFloat(),
        )
        lineFrom18To58Path.lineTo(
            (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            (heightInt - (cellSize)).toFloat(),
        )
        lineFrom18To58Path.close()
//        canvas.drawPath(
//            lineFrom18To58Path,
//            linePaint
//        )

        canvas.drawLine(
            (0 + (cellSize / 2)).toFloat(),
            (heightInt - (squareSideSize * 1.125f) - cellSize).toFloat(),
            (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            (heightInt - (cellSize)).toFloat(),
            paint
        )


        if(inte < listOfPoints.size) {
            canvas.drawLine(
                listOfPoints[0].x.toFloat(),
                listOfPoints[0].y.toFloat(),
                listOfPoints[inte].x.toFloat(),
                listOfPoints[inte].y.toFloat(),
                linePaint
            )
            inte++

            if (inte < listOfPoints.size) {
                postInvalidateDelayed(35)
            } else {
                canvas.drawLine(
                    (0 + (cellSize / 2)).toFloat(),
                    (heightInt - (squareSideSize * 1.125f) - cellSize).toFloat(),
                   listOfPoints[inte - 1].x.toFloat(),
                    listOfPoints[inte - 1].y.toFloat(),
                    linePaint
                )
            }
        } else {
            canvas.drawLine(
                (0 + (cellSize / 2)).toFloat(),
                (heightInt - (squareSideSize * 1.125f) - cellSize).toFloat(),
                listOfPoints[inte - 1].x.toFloat(),
                listOfPoints[inte - 1].y.toFloat(),
                linePaint
            )
        }









            val centerBottomSquareLeftBottomPoint = Point(
            centerX - (squareSideSize / 2),
            heightInt
        )

        val centerBottomSquareRightBottomPoint = Point(
            centerX + (squareSideSize / 2),
            heightInt
        )

        val centerBottomSquareLeftTopPoint = Point(
            centerX - (squareSideSize / 2),
            heightInt - squareSideSize
        )

        val centerBottomSquareRightTopPoint = Point(
            centerX + (squareSideSize / 2),
            heightInt - squareSideSize
        )

        val centerBottomSquarePath = Path()
        centerBottomSquarePath.fillType = FillType.EVEN_ODD

        centerBottomSquarePath.moveTo(centerBottomSquareLeftTopPoint.x.toFloat(), centerBottomSquareLeftTopPoint.y.toFloat())
        centerBottomSquarePath.lineTo(centerBottomSquareLeftBottomPoint.x.toFloat(), centerBottomSquareLeftBottomPoint.y.toFloat())
        centerBottomSquarePath.lineTo(centerBottomSquareRightBottomPoint.x.toFloat(), centerBottomSquareRightBottomPoint.y.toFloat())
        centerBottomSquarePath.lineTo(centerBottomSquareRightTopPoint.x.toFloat(), centerBottomSquareRightTopPoint.y.toFloat())
        centerBottomSquarePath.lineTo(centerBottomSquareLeftTopPoint.x.toFloat(), centerBottomSquareLeftTopPoint.y.toFloat())

        centerBottomSquarePath.close()
        canvas.drawPath(centerBottomSquarePath, paint)

        val leftTriangleSideSize = (squareSideSize * 1.25f).toInt()

        val leftTriangleBottomPoint = Point(
            0,
            (heightInt - (squareSideSize * 1.125f)).toInt()
        )

        val leftTriangleTopPoint = Point(
            0,
            (heightInt - (squareSideSize * 1.125f) - leftTriangleSideSize).toInt()
        )

        val y2 = (heightInt - (squareSideSize * 1.125f)).toInt()
        val y1 = (heightInt - (squareSideSize * 1.125f) - leftTriangleSideSize).toInt()
        val leftTriangleCenterPointX = (y2 - y1) * sin(Math.toRadians(60.0))
        val leftTriangleCenterPointY = (y2 - y1) * cos(Math.toRadians(60.0)) + y1

        val leftTriangleCenterPoint = Point(
            leftTriangleCenterPointX.toInt(),
            leftTriangleCenterPointY.toInt()
        )

        val leftTrianglePath = Path()
        leftTrianglePath.fillType = FillType.EVEN_ODD

        leftTrianglePath.moveTo(leftTriangleBottomPoint.x.toFloat(), leftTriangleBottomPoint.y.toFloat())
        leftTrianglePath.lineTo(leftTriangleTopPoint.x.toFloat(), leftTriangleTopPoint.y.toFloat())
        leftTrianglePath.lineTo(leftTriangleCenterPoint.x.toFloat(), leftTriangleCenterPoint.y.toFloat())
        leftTrianglePath.lineTo(leftTriangleBottomPoint.x.toFloat(), leftTriangleBottomPoint.y.toFloat())

        leftTrianglePath.close()
        canvas.drawPath(leftTrianglePath, paint)

        val rightTriangleBottomPoint = Point(
            widthInt,
            (heightInt - (squareSideSize * 1.125f)).toInt()
        )

        val rightTriangleTopPoint = Point(
            widthInt,
            (heightInt - (squareSideSize * 1.125f) - leftTriangleSideSize).toInt()
        )

        val rightTriangleCenterPoint = Point(
            widthInt - leftTriangleCenterPointX.toInt(),
            leftTriangleCenterPointY.toInt()
        )

        val rightTrianglePath = Path()
        rightTrianglePath.fillType = FillType.EVEN_ODD

        rightTrianglePath.moveTo(rightTriangleBottomPoint.x.toFloat(), rightTriangleBottomPoint.y.toFloat())
        rightTrianglePath.lineTo(rightTriangleTopPoint.x.toFloat(), rightTriangleTopPoint.y.toFloat())
        rightTrianglePath.lineTo(rightTriangleCenterPoint.x.toFloat(), rightTriangleCenterPoint.y.toFloat())
        rightTrianglePath.lineTo(rightTriangleBottomPoint.x.toFloat(), rightTriangleBottomPoint.y.toFloat())

        rightTrianglePath.close()
        canvas.drawPath(rightTrianglePath, paint)

        val centerSquareLeftBottomPoint = Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f)).toInt()
        )

        val centerSquareRightBottomPoint = Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f)).toInt()
        )

        val centerSquareLeftTopPoint = Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize).toInt()
        )

        val centerSquareRightTopPoint = Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize).toInt()
        )

        val centerSquarePath = Path()
        centerSquarePath.fillType = FillType.EVEN_ODD

        centerSquarePath.moveTo(centerSquareLeftBottomPoint.x.toFloat(), centerSquareLeftBottomPoint.y.toFloat())
        centerSquarePath.lineTo(centerSquareRightBottomPoint.x.toFloat(), centerSquareRightBottomPoint.y.toFloat())
        centerSquarePath.lineTo(centerSquareRightTopPoint.x.toFloat(), centerSquareRightTopPoint.y.toFloat())
        centerSquarePath.lineTo(centerSquareLeftTopPoint.x.toFloat(), centerSquareLeftTopPoint.y.toFloat())
        centerSquarePath.lineTo(centerSquareLeftBottomPoint.x.toFloat(), centerSquareLeftBottomPoint.y.toFloat())

        centerSquarePath.close()
        canvas.drawPath(centerSquarePath, paint)

        val rhombBottomPoint = Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize)).toInt()
        )

        val rhombTopPoint = Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize)).toInt()
        )

        val rhombLeftPoint = Point(
            centerX - (squareSideSize / 2) - cellSize,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (1.25f * squareSideSize)).toInt()
        )

        val rhombRightPoint = Point(
            centerX + (squareSideSize / 2) + cellSize,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (1.25f * squareSideSize)).toInt()
        )

        val rhombPath = Path()
        rhombPath.moveTo(rhombBottomPoint.x.toFloat(), rhombBottomPoint.y.toFloat())
        rhombPath.lineTo(rhombLeftPoint.x.toFloat(), rhombLeftPoint.y.toFloat())
        rhombPath.lineTo(rhombTopPoint.x.toFloat(), rhombTopPoint.y.toFloat())
        rhombPath.lineTo(rhombRightPoint.x.toFloat(), rhombRightPoint.y.toFloat())
        rhombPath.lineTo(rhombBottomPoint.x.toFloat(), rhombBottomPoint.y.toFloat())
        rhombPath.close()

        canvas.drawPath(rhombPath, paint)

        val topSquareLeftBottomPoint = Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize)).toInt()
        )

        val topSquareRightBottomPoint = Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize)).toInt()
        )

        val topSquareLeftTopPoint = Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize).toInt()
        )

        val topSquareRightTopPoint = Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize).toInt()
        )

        val topSquarePath = Path()
        topSquarePath.fillType = FillType.EVEN_ODD

        topSquarePath.moveTo(topSquareLeftBottomPoint.x.toFloat(), topSquareLeftBottomPoint.y.toFloat())
        topSquarePath.lineTo(topSquareRightBottomPoint.x.toFloat(), topSquareRightBottomPoint.y.toFloat())
        topSquarePath.lineTo(topSquareRightTopPoint.x.toFloat(), topSquareRightTopPoint.y.toFloat())
        topSquarePath.lineTo(topSquareLeftTopPoint.x.toFloat(), topSquareLeftTopPoint.y.toFloat())
        topSquarePath.lineTo(topSquareLeftBottomPoint.x.toFloat(), topSquareLeftBottomPoint.y.toFloat())


        topSquarePath.close()
        canvas.drawPath(topSquarePath, paint)


        val topReverseTriangleCenterPoint = Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize)).toInt()
        )

        val topReverseTriangleLeftPoint = Point(
            (centerX - (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize).toInt()
        )

        val topReverseTriangleRightPoint = Point(
            (centerX + (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize).toInt()
        )

        val topReverseTrianglePath = Path()
        topReverseTrianglePath.fillType = FillType.EVEN_ODD

        topReverseTrianglePath.moveTo(topReverseTriangleCenterPoint.x.toFloat(), topReverseTriangleCenterPoint.y.toFloat())
        topReverseTrianglePath.lineTo(topReverseTriangleLeftPoint.x.toFloat(), topReverseTriangleLeftPoint.y.toFloat())
        topReverseTrianglePath.lineTo(topReverseTriangleRightPoint.x.toFloat(), topReverseTriangleRightPoint.y.toFloat())
        topReverseTrianglePath.lineTo(topReverseTriangleCenterPoint.x.toFloat(), topReverseTriangleCenterPoint.y.toFloat())

        topReverseTrianglePath.close()
        canvas.drawPath(topReverseTrianglePath, paint)

        val topTriangleLeftPoint = Point(
            (centerX - (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize)).toInt()
        )

        val topTriangleRightPoint = Point(
            (centerX + (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize)).toInt()
        )

        val topTriangleCenterPoint = Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize).toInt()
        )

        val topTrianglePath = Path()
        topTrianglePath.fillType = FillType.EVEN_ODD

        topTrianglePath.moveTo(topTriangleLeftPoint.x.toFloat(), topTriangleLeftPoint.y.toFloat())
        topTrianglePath.lineTo(topTriangleRightPoint.x.toFloat(), topTriangleRightPoint.y.toFloat())
        topTrianglePath.lineTo(topTriangleCenterPoint.x.toFloat(), topTriangleCenterPoint.y.toFloat())
        topTrianglePath.lineTo(topTriangleLeftPoint.x.toFloat(), topTriangleLeftPoint.y.toFloat())

        topTrianglePath.close()

        canvas.drawPath(topTrianglePath, paint)

        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 12f

        canvas.drawText("58",
            (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            (heightInt - (cellSize)).toFloat(),
            textPaint
        )

        canvas.drawText("18",
            (0 + (cellSize / 2)).toFloat(),
            (heightInt - (squareSideSize * 1.125f) - cellSize).toFloat(),
            textPaint
        )





    }

}

