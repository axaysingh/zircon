package org.codetome.zircon.api.game

import org.codetome.zircon.api.Beta
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size

/**
 * Represents the size of a 3D space. Extends [org.codetome.zircon.api.Size]
 * with a `zLength` (z axis) dimension.
 * Explanation:
 *         ^ (zLength, z axis, positive direction)
 *         \
 *         \
 *         \
 *         \
 *         O---------> (xLength, x axis, positive direction)
 *        /
 *      /
 *    /
 *  L
 * (yLength, y axis, positive direction)
 *
 */
@Suppress("DataClassPrivateConstructor")
@Beta
data class Size3D private constructor(val xLength: Int,
                                      val yLength: Int,
                                      val zLength: Int) : Comparable<Size3D> {

    operator fun plus(other: Size3D) = Size3D.of(xLength + other.xLength, yLength + other.yLength, zLength + other.zLength)

    operator fun minus(other: Size3D) = Size3D.of(xLength - other.xLength, yLength - other.yLength, zLength - other.zLength)

    override fun compareTo(other: Size3D): Int {
        return if(fetchPositionCount() > other.fetchPositionCount()) {
            1
        } else if(fetchPositionCount() < other.fetchPositionCount()) {
            -1
        } else {
            0
        }
    }

    /**
     * Creates a collection of [Position]s in the order in which they should
     * be iterated when drawing:
     * - from bottom to top (z axis),
     * - from furthest to closest (y axis),
     * - from left to right (x axis)
     */
    fun fetchPositions(): Iterable<Position3D> {
        return (0 until zLength).flatMap { z ->
            (0 until yLength).flatMap { y ->
                (0 until xLength).map { x ->
                    Position3D.of(x, y, z)
                }
            }
        }
    }

    /**
     * Tells whether this [Size3D] contains the given [Position3D].
     */
    fun containsPosition(position: Position3D): Boolean {
        val (x, y, z) = position
        return xLength > x && yLength > y && zLength > z
    }

    /**
     * Returns the number of [Position3D]s which are covered
     * with this [Size3D].
     */
    fun fetchPositionCount(): Long = xLength.toLong() * yLength.toLong() * zLength.toLong()

    /**
     * Transforms this [Size3D] to a [Size]. Note that
     * the `zLength` component is lost during the conversion!
     */
    fun to2DSize() = Size.of(xLength, yLength)

    companion object {

        @JvmField
        val ONE = of(1, 1, 1)

        /**
         * Factory method for [Size3D].
         */
        @JvmStatic
        fun of(xLength: Int, yLength: Int, zLength: Int) = Size3D(
                xLength = xLength,
                yLength = yLength,
                zLength = zLength)

        /**
         * Creates a new [Size3D] from a [Size].
         * If `zLength` is not supplied, it defaults to `0`.
         */
        @JvmOverloads
        @JvmStatic
        fun from2DSize(size: Size, zLength: Int = 0) = Size3D(
                xLength = size.xLength,
                yLength = size.yLength,
                zLength = zLength)
    }
}
