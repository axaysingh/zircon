package org.codetome.zircon.api.animation

import org.codetome.zircon.api.Beta
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.graphics.Layer

/**
 * Note that this class is in **BETA**!
 * It's API is subject to change!
 */
@Beta
interface AnimationFrame {

    /**
     * Returns the [Size] of the space which is occupied by this [AnimationFrame].
     */
    fun getSize(): Size

    /**
     * Returns a list of [Layer]s which are part of this [AnimationFrame].
     */
    fun getLayers(): List<Layer>

    /**
     * Returns how many times this frame will be repeated.
     */
    fun getRepeatCount(): Int

    /**
     * Returns the [Position] at which this [AnimationFrame] should be drawn.
     */
    fun getPosition(): Position

    /**
     * Sets the [Position] at which this [AnimationFrame] should be drawn.
     */
    fun setPosition(position: Position)
}