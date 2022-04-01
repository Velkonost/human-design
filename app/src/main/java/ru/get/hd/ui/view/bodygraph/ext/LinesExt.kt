package ru.get.hd.ui.view.bodygraph.ext

import android.graphics.Point
import ru.get.hd.ui.view.bodygraph.BodygraphView

data class BodygraphLine(
    var firstNumber: BodygraphNumber,
    var secondNumber: BodygraphNumber
)

data class BodygraphActiveLine(
    val line: BodygraphLine,
    var isDesign: Boolean,
    var isPersonality: Boolean,
    val state: ActiveLineState,
    val listOfPoints: ArrayList<Point>,
    var drawIndex: Int = 0
)

data class BodygraphActiveMultiline(
    val firstLine: BodygraphActiveLine,
    val secondLine: BodygraphActiveLine,
    val thirdLine: BodygraphActiveLine? = null
)

enum class ActiveLineState(val state: String) {
    HalfActive("half-active"),
    Active("active"),
    Inactive("inactive")
}

fun BodygraphView.initLines() {

    val lineFrom57To20PointsForLineFrom34 = arrayListOf<Point>()
    val lineFrom57To20PointsForLineFrom10 = arrayListOf<Point>()

    for (k in 1..6) {
        lineFrom57To20PointsForLineFrom34.add(
            Point(
                (numbers.find { it.text == "57" }!!.x.toInt()
                        + k * (numbers.find { it.text == "20" }!!.x.toInt()
                        - numbers.find { it.text == "57" }!!.x.toInt()) / 6),
                (numbers.find { it.text == "57" }!!.y.toInt()
                        + k * (numbers.find { it.text == "20" }!!.y.toInt()
                        - numbers.find { it.text == "57" }!!.y.toInt()) / 6)
            )
        )
    }

    for (k in 1..8) {
        lineFrom57To20PointsForLineFrom10.add(
            Point(
                (numbers.find { it.text == "57" }!!.x.toInt()
                        + k * (numbers.find { it.text == "20" }!!.x.toInt()
                        - numbers.find { it.text == "57" }!!.x.toInt()) / 8),
                (numbers.find { it.text == "57" }!!.y.toInt()
                        + k * (numbers.find { it.text == "20" }!!.y.toInt()
                        - numbers.find { it.text == "57" }!!.y.toInt()) / 8)
            )
        )
    }

    val lineFrom34X = lineFrom57To20PointsForLineFrom34[1].x
    val lineFrom34Y = lineFrom57To20PointsForLineFrom34[1].y

    val lineFrom10X = lineFrom57To20PointsForLineFrom10[3].x
    val lineFrom10Y = lineFrom57To20PointsForLineFrom10[3].y

    lines = arrayListOf(
        BodygraphLine(
            firstNumber = numbers.find { it.text == "58" }!!,
            secondNumber = numbers.find { it.text == "18" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "38" }!!,
            secondNumber = numbers.find { it.text == "28" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "54" }!!,
            secondNumber = numbers.find { it.text == "32" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "41" }!!,
            secondNumber = numbers.find { it.text == "30" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "39" }!!,
            secondNumber = numbers.find { it.text == "55" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "19" }!!,
            secondNumber = numbers.find { it.text == "49" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "53" }!!,
            secondNumber = numbers.find { it.text == "42" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "60" }!!,
            secondNumber = numbers.find { it.text == " 3" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "52" }!!,
            secondNumber = numbers.find { it.text == " 9" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "50" }!!,
            secondNumber = numbers.find { it.text == "27" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "57" }!!,
            secondNumber = numbers.find { it.text == "20" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "48" }!!,
            secondNumber = numbers.find { it.text == "16" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == " 6" }!!,
            secondNumber = numbers.find { it.text == "59" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "22" }!!,
            secondNumber = numbers.find { it.text == "12" }!!.copy(
                y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 3.25 * cellSize).toFloat() - cellSize
            )
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "35" }!!.copy(
                y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 4.5 * cellSize).toFloat() - cellSize
            ),
            secondNumber = numbers.find { it.text == "36" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == " 5" }!!,
            secondNumber = numbers.find { it.text == "15" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "14" }!!,
            secondNumber = numbers.find { it.text == " 2" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "29" }!!,
            secondNumber = numbers.find { it.text == "46" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == " 1" }!!,
            secondNumber = numbers.find { it.text == "8" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "31" }!!,
            secondNumber = numbers.find { it.text == " 7" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "33" }!!,
            secondNumber = numbers.find { it.text == "13" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "62" }!!,
            secondNumber = numbers.find { it.text == "17" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "23" }!!,
            secondNumber = numbers.find { it.text == "43" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "56" }!!,
            secondNumber = numbers.find { it.text == "11" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "47" }!!,
            secondNumber = numbers.find { it.text == "64" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "24" }!!,
            secondNumber = numbers.find { it.text == "61" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "63" }!!,
            secondNumber = numbers.find { it.text == " 4" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "26" }!!,
            secondNumber = numbers.find { it.text == "44" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "51" }!!,
            secondNumber = numbers.find { it.text == "25" }!!
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "40" }!!,
            secondNumber = numbers.find { it.text == "37" }!!
        ),

        BodygraphLine(
            firstNumber = numbers.find { it.text == "34" }!!,
            secondNumber = numbers.find { it.text == "34" }!!.copy(
                x = lineFrom34X.toFloat(),
                y = lineFrom34Y.toFloat()
            )
        ),
        BodygraphLine(
            firstNumber = numbers.find { it.text == "10" }!!,
            secondNumber = numbers.find { it.text == "10" }!!.copy(
                x = lineFrom10X.toFloat(),
                y = lineFrom10Y.toFloat()
            )
        )

    )
}

fun checkIsPointOnLine(
    pointX: Float,
    pointY: Float,
    lineStartX: Float,
    lineStartY: Float,
    lineEndX: Float,
    lineEndY: Float
): Boolean {
    val mX = lineEndX - lineStartX
    val mY = lineEndY - lineStartY
    val smX = lineStartX - lineEndX
    val smY = lineStartY - lineEndY
    var pmX = mX
    var pmY = mY
    var psmX = smX
    var psmY = smY
    var yY = lineEndY - pointY
    var xX = lineEndX - pointX
    var sX = lineStartX - pointX
    var sY = lineStartY - pointY
    val m = mY / mX
    val b = lineStartY - m * lineStartX
    if (mX < 0) {
        pmX = mX * -1
    }
    if (mY < 0) {
        pmY = mY * -1
    }
    if (smX < 0) {
        psmX = smX * -1
    }
    if (smY < 0) {
        psmY = smY * -1
    }
    if (yY < 0) {
        yY *= -1
    }
    if (xX < 0) {
        xX *= -1
    }
    if (sX < 0) {
        sX *= -1
    }
    if (sY < 0) {
        sY *= -1
    }
    return if (lineStartY == lineEndY && pointY == lineStartY) {
        when {
            lineStartX >= lineEndX -> {
                pointX in lineEndX..lineStartX
            }
            lineEndX >= lineStartX -> {
                pointX in lineStartX..lineEndX
            }
            else -> false
        }
    } else if (lineStartX == lineEndX && pointX == lineStartX) {
        when {
            lineStartY >= lineEndY -> {
                pointY in lineEndY..lineStartY
            }
            lineEndY >= lineStartY -> {
                pointY in lineStartY..lineEndY
            }
            else -> false
        }
    } else if (xX <= pmX && sX <= psmX && yY <= pmY && sY <= psmY) {
        pointY == m * pointX + b
    } else false
}