package org.codetome.zircon.api.shape

import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.internal.graphics.DefaultShape

object FilledRectangleFactory : ShapeFactory<RectangleParameters> {

    override fun createShape(shapeParameters: RectangleParameters) = shapeParameters.let { (topLeft, size) ->
        DefaultShape((0 until size.yLength).flatMap { y ->
            (0 until size.xLength).map { x ->
                Position.of(topLeft.x + x, topLeft.y + y)
            }
        }.toSet()).offsetToDefaultPosition()
    }

    /**
     * Creates the points for a filled rectangle.
     *
     * For example, calling this method with size being the size of a terminal and top-left
     * value being the terminals top-left (0x0) corner will create a shape which when drawn
     * will fill the whole terminal.
     * **Note that** all resulting shapes will be offset to the top left (0x0) position!
     * @see [org.codetome.zircon.api.graphics.Shape.offsetToDefaultPosition] for more info!
     */
    @JvmStatic
    fun buildFilledRectangle(rectParams: RectangleParameters) = createShape(rectParams)

    /**
     * Creates the points for a filled rectangle.
     *
     * For example, calling this method with size being the size of a terminal and top-left
     * value being the terminals top-left (0x0) corner will create a shape which when drawn
     * will fill the whole terminal.
     * **Note that** all resulting shapes will be offset to the top left (0x0) position!
     * @see [org.codetome.zircon.api.graphics.Shape.offsetToDefaultPosition] for more info!
     */
    @JvmStatic
    fun buildFilledRectangle(topLeft: Position, size: Size)
            = buildFilledRectangle(RectangleParameters(topLeft, size))
}
