import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

const val WIDTH = 500
const val HEIGHT = 500

fun main() {
    val image = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
//    drawShapeOne(image)
//    drawShapeTwo(image)
//    drawShapeThree(image)
    drawShapeFour(image)

    try {
        val file = File("image.png")
        ImageIO.write(image, "png", file)
    } catch (e: IOException) {
        println(e)
    }
}

fun findIntersectX(
    x1: Int, y1: Int,
    x2: Int, y2: Int,
    x3: Int, y3: Int,
    x4: Int, y4: Int
): Int {
    val num = (x1 * y2 - y1 * x2) * (x3 - x4) -
            (x1 - x2) * (x3 * y4 - y3 * x4)
    val den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
    return num / den
}

fun findIntersectY(
    x1: Int, y1: Int,
    x2: Int, y2: Int,
    x3: Int, y3: Int,
    x4: Int, y4: Int
): Int {
    val num = (x1 * y2 - y1 * x2) * (y3 - y4) -
            (y1 - y2) * (x3 * y4 - y3 * x4)
    val den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
    return num / den
}

fun findIntersectPoint(
    x1: Int, y1: Int,
    x2: Int, y2: Int,
    x3: Int, y3: Int,
    x4: Int, y4: Int
): Point {
    return Point(
        findIntersectX(x1, y1, x2, y2, x3, y3, x4, y4),
        findIntersectY(x1, y1, x2, y2, x3, y3, x4, y4)
    )
}

fun clip(
    polyPoints: MutableList<Point>,
    x1: Int, y1: Int,
    x2: Int, y2: Int
) {
    val newPolyPoints = mutableListOf<Point>()
    for ((index, value) in polyPoints.withIndex()) {
        val k = (index + 1) % polyPoints.size
        val ix = polyPoints[index].x
        val iy = polyPoints[index].y
        val kx = polyPoints[k].x
        val ky = polyPoints[k].y

        val iPos = (x2 - x1) * (iy - y1) - (y2 - y1) * (ix - x1)
        val kPos = (x2 - x1) * (ky - y1) - (y2 - y1) * (kx - x1)

        if (iPos < 0 && kPos < 0) {             // Обе точки в пределах области
            newPolyPoints.add(Point(kx, ky))
        } else if (iPos >= 0 && kPos < 0) {     // Только первая точка в области
            newPolyPoints.add(findIntersectPoint(x1, y1, x2, y2, ix, iy, kx, ky))
            newPolyPoints.add(Point(kx, ky))
        } else if (iPos < 0 && kPos >= 0) {     // Только вторая точка в области
            newPolyPoints.add(findIntersectPoint(x1, y1, x2, y2, ix, iy, kx, ky))
        }
    }
    polyPoints.clear()
    polyPoints.addAll(newPolyPoints)
}

fun drawWithClipping(
    polygon: MutableList<Point>,
    clipper: List<Point>,
    image: BufferedImage
) {
    for ((index, value) in clipper.withIndex()) {
        val k = (index + 1) % clipper.size

        clip(polygon,
            clipper[index].x, clipper[index].y,
            clipper[k].x, clipper[k].y)
    }

    print("Точки итогового полигона: ")
    for (p in polygon) {
        print("(${p.x}, ${p.y}) ")
    }

    drawPolygon(polygon, image)
}

fun drawPolygon(polygon: List<Point>, image: BufferedImage, rgb: Int = -1) {
    for (i in polygon.indices) {
        val k = (i + 1) % polygon.size
        Draw.drawLine(polygon[i].x, polygon[i].y,
            polygon[k].x, polygon[k].y,
            image, rgb)
    }
}

fun drawShapeOne(image: BufferedImage) {
    val color = Color(200, 150, 255).rgb
    val polygon = mutableListOf(
        Point(100,150),
        Point(350,150),
        Point(225,350)
    )

    val clipper = listOf(
        Point(100,300),
        Point(350,300),
        Point(225,100)
    )

    drawPolygon(clipper, image, color)
    drawPolygon(polygon, image, color)
    drawWithClipping(polygon, clipper, image)
}

fun drawShapeTwo(image: BufferedImage) {
    val color = Color(200, 0, 255).rgb
    val polygon = mutableListOf(
        Point(100, 150),
        Point(200, 250),
        Point(300, 200)
    )

    drawPolygon(polygon, image, color)

    val clipper = listOf(
        Point(150, 150),
        Point(150, 200),
        Point(200, 200),
        Point(200, 150)
    )
    drawPolygon(clipper, image, color)
    drawWithClipping(polygon, clipper, image)
}

fun drawShapeThree(image: BufferedImage) {
    val color = Color(200, 0, 255).rgb
    val polygon = mutableListOf(
        Point(280, 24),
        Point(30, 155),
        Point(47, 230),
        Point(230, 320),
        Point(490, 220),
        Point(430, 55)
    )

    drawPolygon(polygon, image, color)

    val clipper = listOf(
        Point(110, 85),
        Point(110, 280),
        Point(415, 280),
        Point(415, 85)
    )
    drawPolygon(clipper, image, color)
    drawWithClipping(polygon, clipper, image)
}

fun drawShapeFour(image: BufferedImage) {
    val color = Color(200, 0, 255).rgb
    val polygon = mutableListOf(
        Point(280, 24),
        Point(30, 155),
        Point(47, 230),
        Point(230, 320),
        Point(415, 220),
        Point(415, 55)
    )

    drawPolygon(polygon, image, color)

    val clipper = listOf(
        Point(110, 85),
        Point(110, 280),
        Point(415, 280),
        Point(415, 85)
    )
    drawPolygon(clipper, image, color)
    drawWithClipping(polygon, clipper, image)
}

class Point(
    val x: Int,
    val y: Int
)