package ru.get.hd.ui.view.bodygraph.ext

import android.graphics.Path
import android.graphics.Point
import ru.get.hd.ui.view.bodygraph.BodygraphView
import kotlin.math.cos
import kotlin.math.sin

/**
Init Points
 */
fun BodygraphView.initBottomSquarePoints() {
    bottomSquarePoints = hashMapOf(
        BodygraphView.SquarePoint.LeftBottom to Point(
            centerX - (squareSideSize / 2),
            heightInt
        ),
        BodygraphView.SquarePoint.RightBottom to Point(
            centerX + (squareSideSize / 2),
            heightInt
        ),
        BodygraphView.SquarePoint.LeftTop to Point(
            centerX - (squareSideSize / 2),
            heightInt - squareSideSize
        ),
        BodygraphView.SquarePoint.RightTop to Point(
            centerX + (squareSideSize / 2),
            heightInt - squareSideSize
        )
    )
}

fun BodygraphView.initCenterSquarePoints() {
    centerSquarePoints = hashMapOf(
        BodygraphView.SquarePoint.LeftBottom to Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f)).toInt()
        ),
        BodygraphView.SquarePoint.RightBottom to Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f)).toInt()
        ),
        BodygraphView.SquarePoint.LeftTop to Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize).toInt()
        ),
        BodygraphView.SquarePoint.RightTop to Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize).toInt()
        )
    )
}

fun BodygraphView.initTopSquarePoints() {
    topSquarePoints = hashMapOf(
        BodygraphView.SquarePoint.LeftBottom to Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize)).toInt()
        ),
        BodygraphView.SquarePoint.RightBottom to Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize)).toInt()
        ),
        BodygraphView.SquarePoint.LeftTop to Point(
            centerX - (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize).toInt()
        ),
        BodygraphView.SquarePoint.RightTop to Point(
            centerX + (squareSideSize / 2),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize).toInt()
        )
    )
}

fun BodygraphView.initRhombPoints() {
    rhombPoints = hashMapOf(
        BodygraphView.RhombPoint.Bottom to Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize)).toInt()
        ),
        BodygraphView.RhombPoint.Top to Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize)).toInt()
        ),
        BodygraphView.RhombPoint.Left to Point(
            centerX - (squareSideSize / 2) - cellSize,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (1.25f * squareSideSize)).toInt()
        ),
        BodygraphView.RhombPoint.Right to Point(
            centerX + (squareSideSize / 2) + cellSize,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (1.25f * squareSideSize)).toInt()
        )
    )
}

fun BodygraphView.initLeftTrianglePoints() {
    val y2 = (heightInt - (squareSideSize * 1.125f)).toInt()
    val y1 = (heightInt - (squareSideSize * 1.125f) - (squareSideSize * 1.25f)).toInt()
    val centerPointX = (y2 - y1) * sin(Math.toRadians(60.0))
    val centerPointY = (y2 - y1) * cos(Math.toRadians(60.0)) + y1

    leftTrianglePoints = hashMapOf(
        BodygraphView.SideTrianglePoint.Bottom to Point(
            0,
            (heightInt - (squareSideSize * 1.125f)).toInt()
        ),
        BodygraphView.SideTrianglePoint.Top to Point(
            0,
            (heightInt - (squareSideSize * 1.125f) - (squareSideSize * 1.25f)).toInt()
        ),
        BodygraphView.SideTrianglePoint.Center to Point(
            centerPointX.toInt(),
            centerPointY.toInt()
        )
    )
}

fun BodygraphView.initRightTrianglePoints() {
    val y2 = (heightInt - (squareSideSize * 1.125f)).toInt()
    val y1 = (heightInt - (squareSideSize * 1.125f) - (squareSideSize * 1.25f)).toInt()
    val centerPointX = (y2 - y1) * sin(Math.toRadians(60.0))
    val centerPointY = (y2 - y1) * cos(Math.toRadians(60.0)) + y1

    rightTrianglePoints = hashMapOf(
        BodygraphView.SideTrianglePoint.Bottom to Point(
            widthInt,
            (heightInt - (squareSideSize * 1.125f)).toInt()
        ),
        BodygraphView.SideTrianglePoint.Top to Point(
            widthInt,
            (heightInt - (squareSideSize * 1.125f) - (squareSideSize * 1.25f)).toInt()
        ),
        BodygraphView.SideTrianglePoint.Center to Point(
            widthInt - centerPointX.toInt(),
            centerPointY.toInt()
        )
    )
}

fun BodygraphView.initTopReverseTrianglePoints() {
    topReverseTrianglePoints = hashMapOf(
        BodygraphView.TopTrianglePoint.Center to Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize)).toInt()
        ),
        BodygraphView.TopTrianglePoint.Left to Point(
            (centerX - (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize).toInt()
        ),
        BodygraphView.TopTrianglePoint.Right to Point(
            (centerX + (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize).toInt()
        )
    )
}

fun BodygraphView.initTopTrianglePoints() {
    topTrianglePoints = hashMapOf(
        BodygraphView.TopTrianglePoint.Left to Point(
            (centerX - (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize)).toInt()
        ),
        BodygraphView.TopTrianglePoint.Right to Point(
            (centerX + (0.5625f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize)).toInt()
        ),
        BodygraphView.TopTrianglePoint.Center to Point(
            centerX,
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - (0.625f * squareSideSize) - (1.25f * squareSideSize) - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize - (0.25f * squareSideSize) - squareSideSize).toInt()
        )
    )
}

fun BodygraphView.initStrangeTrianglePoints() {
    strangeTrianglePoints = hashMapOf(
        BodygraphView.StrangeTrianglePoint.Top to Point(
            (centerX + (squareSideSize / 2) - 0.5 * (cellSize) + (0.5f * squareSideSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - squareSideSize).toInt()
        ),
        BodygraphView.StrangeTrianglePoint.Left to Point(
            (centerX + (squareSideSize / 2) - 0.5 * (cellSize)).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - 0.5f * squareSideSize).toInt()

        ),
        BodygraphView.StrangeTrianglePoint.Right to Point(
            (centerX + (squareSideSize / 2) - 1.5 * (cellSize) + squareSideSize).toInt(),
            (heightInt - (squareSideSize * 1.25f) - squareSideSize - 0.35f * squareSideSize).toInt()
        )
    )
}

/**
 * Init Paths
 */
fun BodygraphView.initBottomSquarePath() {
    initBottomSquarePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        bottomSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.x.toFloat(),
        bottomSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y.toFloat()
    )
    path.lineTo(
        bottomSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.x.toFloat(),
        bottomSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y.toFloat()
    )
    path.lineTo(
        bottomSquarePoints[BodygraphView.SquarePoint.RightBottom]!!.x.toFloat(),
        bottomSquarePoints[BodygraphView.SquarePoint.RightBottom]!!.y.toFloat()
    )
    path.lineTo(
        bottomSquarePoints[BodygraphView.SquarePoint.RightTop]!!.x.toFloat(),
        bottomSquarePoints[BodygraphView.SquarePoint.RightTop]!!.y.toFloat()
    )
    path.lineTo(
        bottomSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.x.toFloat(),
        bottomSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y.toFloat()
    )
    path.close()

    bottomSquarePath = path
}

fun BodygraphView.initCenterSquarePath() {
    initCenterSquarePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.x.toFloat(),
        centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y.toFloat()
    )
    path.lineTo(
        centerSquarePoints[BodygraphView.SquarePoint.RightBottom]!!.x.toFloat(),
        centerSquarePoints[BodygraphView.SquarePoint.RightBottom]!!.y.toFloat()
    )
    path.lineTo(
        centerSquarePoints[BodygraphView.SquarePoint.RightTop]!!.x.toFloat(),
        centerSquarePoints[BodygraphView.SquarePoint.RightTop]!!.y.toFloat()
    )
    path.lineTo(
        centerSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.x.toFloat(),
        centerSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y.toFloat()
    )
    path.lineTo(
        centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.x.toFloat(),
        centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y.toFloat()
    )

    path.close()

    centerSquarePath = path
}

fun BodygraphView.initTopSquarePath() {
    initTopSquarePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.x.toFloat(),
        topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y.toFloat()
    )
    path.lineTo(
        topSquarePoints[BodygraphView.SquarePoint.RightBottom]!!.x.toFloat(),
        topSquarePoints[BodygraphView.SquarePoint.RightBottom]!!.y.toFloat()
    )
    path.lineTo(
        topSquarePoints[BodygraphView.SquarePoint.RightTop]!!.x.toFloat(),
        topSquarePoints[BodygraphView.SquarePoint.RightTop]!!.y.toFloat()
    )
    path.lineTo(
        topSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.x.toFloat(),
        topSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y.toFloat()
    )
    path.lineTo(
        topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.x.toFloat(),
        topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y.toFloat()
    )
    path.close()

    topSquarePath = path
}

fun BodygraphView.initRhombPath() {
    initRhombPoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        rhombPoints[BodygraphView.RhombPoint.Bottom]!!.x.toFloat(),
        rhombPoints[BodygraphView.RhombPoint.Bottom]!!.y.toFloat()
    )
    path.lineTo(
        rhombPoints[BodygraphView.RhombPoint.Left]!!.x.toFloat(),
        rhombPoints[BodygraphView.RhombPoint.Left]!!.y.toFloat()
    )
    path.lineTo(
        rhombPoints[BodygraphView.RhombPoint.Top]!!.x.toFloat(),
        rhombPoints[BodygraphView.RhombPoint.Top]!!.y.toFloat()
    )
    path.lineTo(
        rhombPoints[BodygraphView.RhombPoint.Right]!!.x.toFloat(),
        rhombPoints[BodygraphView.RhombPoint.Right]!!.y.toFloat()
    )
    path.lineTo(
        rhombPoints[BodygraphView.RhombPoint.Bottom]!!.x.toFloat(),
        rhombPoints[BodygraphView.RhombPoint.Bottom]!!.y.toFloat()
    )
    path.close()

    rhombPath = path
}

fun BodygraphView.initLeftTrianglePath() {
    initLeftTrianglePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x.toFloat(),
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y.toFloat()
    )
    path.lineTo(
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.x.toFloat(),
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.y.toFloat()
    )
    path.lineTo(
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x.toFloat(),
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y.toFloat()
    )
    path.lineTo(
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x.toFloat(),
        leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y.toFloat()
    )
    path.close()

    leftTrianglePath = path
}

fun BodygraphView.initRightTrianglePath() {
    initRightTrianglePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x.toFloat(),
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y.toFloat()
    )
    path.lineTo(
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.x.toFloat(),
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.y.toFloat()
    )
    path.lineTo(
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x.toFloat(),
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y.toFloat()
    )
    path.lineTo(
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x.toFloat(),
        rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y.toFloat()
    )
    path.close()

    rightTrianglePath = path

}

fun BodygraphView.initTopReverseTrianglePath() {
    initTopReverseTrianglePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x.toFloat(),
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y.toFloat()
    )
    path.lineTo(
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.x.toFloat(),
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.y.toFloat()
    )
    path.lineTo(
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.x.toFloat(),
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat()
    )
    path.lineTo(
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x.toFloat(),
        topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y.toFloat()
    )
    path.close()

    topReverseTrianglePath = path
}

fun BodygraphView.initTopTrianglePath() {
    initTopTrianglePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        topTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.x.toFloat(),
        topTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.y.toFloat()
    )
    path.lineTo(
        topTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.x.toFloat(),
        topTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat()
    )
    path.lineTo(
        topTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x.toFloat(),
        topTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y.toFloat()
    )
    path.lineTo(
        topTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.x.toFloat(),
        topTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.y.toFloat()
    )

    path.close()

    topTrianglePath = path
}

fun BodygraphView.initStrangeTrianglePath() {
    initStrangeTrianglePoints()

    val path = Path()
    path.fillType = Path.FillType.EVEN_ODD

    path.moveTo(
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Right]!!.x.toFloat(),
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Right]!!.y.toFloat()
    )

    path.lineTo(
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Top]!!.x.toFloat(),
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Top]!!.y.toFloat()
    )
    path.lineTo(
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.x.toFloat(),
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.y.toFloat()
    )
    path.lineTo(
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Right]!!.x.toFloat(),
        strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Right]!!.y.toFloat()
    )
    path.close()

    strangeTrianglePath = path
}

fun BodygraphView.initFiguresPaths() {
    initBottomSquarePath()
    initCenterSquarePath()
    initTopSquarePath()

    initRhombPath()

    initLeftTrianglePath()
    initRightTrianglePath()

    initTopReverseTrianglePath()
    initTopTrianglePath()

    initStrangeTrianglePath()
}

