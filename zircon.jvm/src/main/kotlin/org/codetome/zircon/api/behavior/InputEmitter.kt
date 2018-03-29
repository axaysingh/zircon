package org.codetome.zircon.api.behavior

import org.codetome.zircon.api.input.Input
import java.util.function.Consumer

/**
 * Represents an object which (re) emits the [Input]s
 * it has received from the underlying technology (like Swing or libGDX).
 */
interface InputEmitter {

    /**
     * Adds an input listener to this [InputEmitter].
     * It will be notified when an [Input] is consumed
     * by this object.
     */
    fun onInput(listener: Consumer<Input>)
}