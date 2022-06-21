package com.myhumandesignhd.ui.view.bodygraph.ext

import android.graphics.Point
import com.myhumandesignhd.ui.view.bodygraph.BodygraphView

data class BodygraphNumber(
    val text: String,
    val x: Float,
    var y: Float,
    val highlight: Boolean = false
)

fun BodygraphView.initNumbers() {
    val leftTriangleFromBottomToCenterPoints = arrayListOf<Point>()
    val leftTriangleFromTopToCenterPoints = arrayListOf<Point>()

    val rightTriangleFromBottomToCenterPoints = arrayListOf<Point>()
    val rightTriangleFromTopToCenterPoints = arrayListOf<Point>()

    val rhombFromBottomToLeftPoints = arrayListOf<Point>()
    val rhombFromBottomToRightPoints = arrayListOf<Point>()
    val rhombFromTopToLeftPoints = arrayListOf<Point>()
    val rhombFromTopToRightPoints = arrayListOf<Point>()

    val topReverseTriangleFromCenterToLeftPoints = arrayListOf<Point>()
    val topReverseTriangleFromCenterToRightPoints = arrayListOf<Point>()

    val strangeTriangleFromLeftToTopPoints = arrayListOf<Point>()

    for (k in 1..4) {
        strangeTriangleFromLeftToTopPoints.add(
            Point(
                (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.x
                        + k * (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Top]!!.x
                        - strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.x) / 4),
                (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.y
                        + k * (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Top]!!.y
                        - strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.y) / 4)
            )
        )

        leftTriangleFromBottomToCenterPoints.add(
            Point(
                (leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x
                        + k * (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x
                        - leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x) / 4),
                (leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y
                        + k * (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y
                        - leftTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y) / 4)
            )
        )

        leftTriangleFromTopToCenterPoints.add(
            Point(
                (leftTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.x
                        + k * (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x
                        - leftTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.x) / 4),
                (leftTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.y
                        + k * (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y
                        - leftTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.y) / 4)
            )
        )

        rightTriangleFromBottomToCenterPoints.add(
            Point(
                (rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x
                        + k * (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x
                        - rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.x) / 4),
                (rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y
                        + k * (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y
                        - rightTrianglePoints[BodygraphView.SideTrianglePoint.Bottom]!!.y) / 4)
            )
        )

        rightTriangleFromTopToCenterPoints.add(
            Point(
                (rightTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.x
                        + k * (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x
                        - rightTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.x) / 4),
                (rightTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.y
                        + k * (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y
                        - rightTrianglePoints[BodygraphView.SideTrianglePoint.Top]!!.y) / 4)
            )
        )
    }

    for (k in 1..3) {


        rhombFromBottomToLeftPoints.add(
            Point(
                (rhombPoints[BodygraphView.RhombPoint.Bottom]!!.x
                        + k * (rhombPoints[BodygraphView.RhombPoint.Left]!!.x
                        - rhombPoints[BodygraphView.RhombPoint.Bottom]!!.x) / 3),
                (rhombPoints[BodygraphView.RhombPoint.Bottom]!!.y
                        + k * (rhombPoints[BodygraphView.RhombPoint.Left]!!.y
                        - rhombPoints[BodygraphView.RhombPoint.Bottom]!!.y) / 3)
            )
        )

        rhombFromBottomToRightPoints.add(
            Point(
                (rhombPoints[BodygraphView.RhombPoint.Bottom]!!.x
                        + k * (rhombPoints[BodygraphView.RhombPoint.Right]!!.x
                        - rhombPoints[BodygraphView.RhombPoint.Bottom]!!.x) / 3),
                (rhombPoints[BodygraphView.RhombPoint.Bottom]!!.y
                        + k * (rhombPoints[BodygraphView.RhombPoint.Right]!!.y
                        - rhombPoints[BodygraphView.RhombPoint.Bottom]!!.y) / 3)
            )
        )

        rhombFromTopToLeftPoints.add(
            Point(
                (rhombPoints[BodygraphView.RhombPoint.Top]!!.x
                        + k * (rhombPoints[BodygraphView.RhombPoint.Left]!!.x
                        - rhombPoints[BodygraphView.RhombPoint.Top]!!.x) / 3),
                (rhombPoints[BodygraphView.RhombPoint.Top]!!.y
                        + k * (rhombPoints[BodygraphView.RhombPoint.Left]!!.y
                        - rhombPoints[BodygraphView.RhombPoint.Top]!!.y) / 3)
            )
        )

        rhombFromTopToRightPoints.add(
            Point(
                (rhombPoints[BodygraphView.RhombPoint.Top]!!.x
                        + k * (rhombPoints[BodygraphView.RhombPoint.Right]!!.x
                        - rhombPoints[BodygraphView.RhombPoint.Top]!!.x) / 3),
                (rhombPoints[BodygraphView.RhombPoint.Top]!!.y
                        + k * (rhombPoints[BodygraphView.RhombPoint.Right]!!.y
                        - rhombPoints[BodygraphView.RhombPoint.Top]!!.y) / 3)
            )
        )

        topReverseTriangleFromCenterToLeftPoints.add(
            Point(
                (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x
                        + k * (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.x
                        - topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x) / 3),
                (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y
                        + k * (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Left]!!.y
                        - topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y) / 3)
            )
        )

        topReverseTriangleFromCenterToRightPoints.add(
            Point(
                (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x
                        + k * (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.x
                        - topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.x) / 3),
                (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y
                        + k * (topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y
                        - topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y) / 3)
            )
        )
    }

    numbers = arrayListOf(

        /**
         * Bottom Square
         */
        /* Left Side */
        BodygraphNumber(
            text = "58",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            y = (heightInt - (cellSize)).toFloat()
        ),
        BodygraphNumber(
            text = "38",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            y = (heightInt - 3 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "54",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat() ,
            y = (heightInt - 5 * cellSize).toFloat()
        ),

        /* Top Side */
        BodygraphNumber(
            text = "53",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + cellSize).toFloat(),
            y = (heightInt - 6 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "60",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + 2.5 * cellSize).toFloat(),
            y = (heightInt - 6 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "52",
            x = (centerX - (squareSideSize / 2) + 4.5 * cellSize).toFloat(),
            y = (heightInt - 6 * cellSize).toFloat()
        ),

        /* Right Side */
        BodygraphNumber(
            text = "41",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (heightInt - (cellSize)).toFloat()
        ),
        BodygraphNumber(
            text = "39",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (heightInt - 3 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "19",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (heightInt - 5 * cellSize).toFloat()
        ),

        /**
         * Center Square
         */
        /* Bottom Side */
        BodygraphNumber(
            text = "42",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + cellSize).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - cellSize / 2).toFloat()
        ),
        BodygraphNumber(
            text = " 3",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - cellSize / 2).toFloat()
        ),
        BodygraphNumber(
            text = " 9",
            x = (centerX - (squareSideSize / 2) + 4.5 * cellSize).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - cellSize / 2).toFloat()
        ),

        /* Top Side */
        BodygraphNumber(
            text = " 5",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + cellSize).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y + cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "14",
            x = (centerX - (squareSideSize / 2) + 3.1f * cellSize).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y + cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "29",
            x = (centerX - (squareSideSize / 2) + 4.7f * cellSize).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y + cellSize).toFloat()
        ),

        /* Left Side */
        BodygraphNumber(
            text = "27",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 2 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "34",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 4.5 * cellSize).toFloat()
        ),

        /* Right Side */
        BodygraphNumber(
            text = "59",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (centerSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 2 * cellSize).toFloat()
        ),

        /**
         * Top Square
         */
        /* Bottom Side */
        BodygraphNumber(
            text = "31",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + cellSize).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - cellSize / 2).toFloat()
        ),
        BodygraphNumber(
            text = "8",
            x = (centerX - (squareSideSize / 2) + 3f * cellSize).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - cellSize / 2).toFloat()
        ),
        BodygraphNumber(
            text = "33",
            x = (centerX - (squareSideSize / 2) + 4.6f * cellSize).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - cellSize / 2).toFloat()
        ),

        /* Top Side */
        BodygraphNumber(
            text = "62",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + cellSize).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y + cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "23",
            x = (centerX - (squareSideSize / 2) + 3.1f * cellSize).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y + cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "56",
            x = (centerX - (squareSideSize / 2) + 4.6f * cellSize).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftTop]!!.y + cellSize).toFloat()
        ),

        /* Left Side */
        BodygraphNumber(
            text = "20",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 2 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "16",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2)).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 4.5 * cellSize).toFloat()
        ),

        /* Right Side */
        BodygraphNumber(
            text = "35",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 4.5 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "12",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 3.25 * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "45",
            x = (centerX + (squareSideSize / 2) - 1.5 * (cellSize)).toFloat(),
            y = (topSquarePoints[BodygraphView.SquarePoint.LeftBottom]!!.y - 2 * cellSize).toFloat()
        ),

        /**
         * Left Triangle
         */
        BodygraphNumber(
            text = "18",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 6.5f * cellSize),
            leftTriangleFromBottomToCenterPoints[0].y.toFloat() - 0.5f * cellSize,
        ),
        BodygraphNumber(
            text = "28",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 5f * cellSize),
            leftTriangleFromBottomToCenterPoints[1].y.toFloat() - 0.25f * cellSize,
        ),
        BodygraphNumber(
            text = "32",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 3.5f * cellSize),
            leftTriangleFromBottomToCenterPoints[2].y.toFloat(),
        ),
        BodygraphNumber(
            text = "50",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 2f * cellSize),
            leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y.toFloat() + 0.25f * cellSize,
        ),

        BodygraphNumber(
            text = "48",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 6.5f * cellSize),
            leftTriangleFromTopToCenterPoints[0].y.toFloat() + cellSize,
        ),
        BodygraphNumber(
            text = "57",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 5f * cellSize),
            leftTriangleFromTopToCenterPoints[1].y.toFloat() + 0.75f * cellSize,
        ),
        BodygraphNumber(
            text = "44",
            (leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x - 3.5f * cellSize),
            leftTriangleFromTopToCenterPoints[2].y.toFloat() + 0.5f * cellSize,
        ),

        /**
         * Right Triangle
         */

        BodygraphNumber(
            text = "30",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 5.5f * cellSize),
            leftTriangleFromBottomToCenterPoints[0].y.toFloat() - 0.5f * cellSize,
        ),
        BodygraphNumber(
            text = "55",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 4f * cellSize),
            leftTriangleFromBottomToCenterPoints[1].y.toFloat() - 0.25f * cellSize,
        ),
        BodygraphNumber(
            text = "49",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 2.5f * cellSize),
            leftTriangleFromBottomToCenterPoints[2].y.toFloat(),
        ),
        BodygraphNumber(
            text = " 6",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 1f * cellSize),
            leftTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.y.toFloat() + 0.25f * cellSize,
        ),

        BodygraphNumber(
            text = "36",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 5.5f * cellSize),
            leftTriangleFromTopToCenterPoints[0].y.toFloat() + cellSize,
        ),
        BodygraphNumber(
            text = "22",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 4f * cellSize),
            leftTriangleFromTopToCenterPoints[1].y.toFloat() + 0.75f * cellSize,
        ),
        BodygraphNumber(
            text = "37",
            (rightTrianglePoints[BodygraphView.SideTrianglePoint.Center]!!.x + 2.5f * cellSize),
            leftTriangleFromTopToCenterPoints[2].y.toFloat() + 0.5f * cellSize,
        ),

        /**
         * Rhomb
         */
        BodygraphNumber(
            text = " 2",
            x = (centerX - (squareSideSize / 2) + 3.1f * cellSize).toFloat(),
            rhombFromBottomToLeftPoints[0].y.toFloat() + 0.5f * cellSize
        ),
        BodygraphNumber(
            text = "15",
            x = rhombFromBottomToLeftPoints[1].x.toFloat() + 0.75f * cellSize,
            y = rhombFromBottomToLeftPoints[1].y.toFloat() + 0.4f * cellSize
        ),
        BodygraphNumber(
            text = "10",
            x = rhombFromBottomToLeftPoints[2].x.toFloat() + 0.5f * cellSize,
            y = rhombFromBottomToLeftPoints[2].y.toFloat() + 0.25f * cellSize
        ),

        BodygraphNumber(
            text = "46",
            x = rhombFromBottomToRightPoints[1].x.toFloat() - 1.75f * cellSize,
            y = rhombFromBottomToLeftPoints[1].y.toFloat() + 0.4f * cellSize
        ),
        BodygraphNumber(
            text = "25",
            x = rhombFromBottomToRightPoints[2].x.toFloat() - 1.5f * cellSize,
            y = rhombFromBottomToLeftPoints[2].y.toFloat() + 0.25f * cellSize
        ),
        BodygraphNumber(
            text = " 7",
            x = rhombFromBottomToLeftPoints[1].x.toFloat() + 0.75f * cellSize,
            y = rhombFromTopToLeftPoints[1].y.toFloat()
        ),
        BodygraphNumber(
            text = "13",
            x = rhombFromBottomToRightPoints[1].x.toFloat() - 1.75f * cellSize,
            y = rhombFromTopToLeftPoints[1].y.toFloat()
        ),
        BodygraphNumber(
            text = " 1",
            x = (centerX - (squareSideSize / 2) + 3.1f * cellSize).toFloat(),
            y = rhombFromTopToLeftPoints[0].y.toFloat()
        ),


        /**
         * Top Reverse Triangle
         */
        BodygraphNumber(
            text = "24",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat(),
            y = topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat() + cellSize
        ),
        BodygraphNumber(
            text = " 4",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat() + 2.5f * cellSize,
            y = topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat() + cellSize
        ),
        BodygraphNumber(
            text = "47",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat() - 2.5f * cellSize,
            y = topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat() + cellSize
        ),
        BodygraphNumber(
            text = "43",
            x = (centerX - (squareSideSize / 2) + 3.1f * cellSize).toFloat(),
            y = topReverseTrianglePoints[BodygraphView.TopTrianglePoint.Center]!!.y.toFloat() - 1.5f * cellSize
        ),
        BodygraphNumber(
            text = "17",
            x = (centerX - (squareSideSize / 2) + (cellSize / 2) + cellSize).toFloat(),
            y = (topReverseTriangleFromCenterToLeftPoints[1].y + 0.75f * cellSize)
        ),
        BodygraphNumber(
            text = "11",
            x = (centerX - (squareSideSize / 2) + 4.5 * cellSize).toFloat(),
            y = (topReverseTriangleFromCenterToLeftPoints[1].y + 0.75f * cellSize)
        ),

        /**
         * Top Triangle
         */
        BodygraphNumber(
            text = "61",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat(),
            y = topTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat() - 0.5f * cellSize
        ),
        BodygraphNumber(
            text = "63",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat() + 2.5f * cellSize,
            y = topTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat() - 0.5f * cellSize
        ),
        BodygraphNumber(
            text = "64",
            x = (centerX - (squareSideSize / 2) + 3 * cellSize).toFloat() - 2.5f * cellSize,
            y = topTrianglePoints[BodygraphView.TopTrianglePoint.Right]!!.y.toFloat() - 0.5f * cellSize
        ),

        /**
         * Strange Triangle
         */
        BodygraphNumber(
            text = "40",
            x = (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Right]!!.x - 1.5f * cellSize).toFloat(),
            y = (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Right]!!.y - 0.5f * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "26",
            x = (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.x + .7f * cellSize).toFloat(),
            y = (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Left]!!.y - .1f * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "40",
            x = (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Top]!!.x - .6f * cellSize).toFloat(),
            y = (strangeTrianglePoints[BodygraphView.StrangeTrianglePoint.Top]!!.y + 1.2f * cellSize).toFloat()
        ),
        BodygraphNumber(
            text = "51",
            x = strangeTriangleFromLeftToTopPoints[1].x.toFloat() + .1f * cellSize,
            y = strangeTriangleFromLeftToTopPoints[1].y.toFloat() + .5f * cellSize
        ),
    )


}