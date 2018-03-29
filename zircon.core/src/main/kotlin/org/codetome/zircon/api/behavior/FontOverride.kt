package org.codetome.zircon.api.behavior

import org.codetome.zircon.api.font.Font
import org.codetome.zircon.internal.font.impl.FontSettings

/**
 * Interface which adds functionality for overriding [Font]s used
 * in its implementors (components, layers, etc).
 */
interface FontOverride {

    /**
     * Tells whether there is currently an override [Font] present or not.
     */
    fun hasOverrideFont(): Boolean = getCurrentFont() !== FontSettings.NO_FONT

    /**
     * Returns the currently used [Font].
     */
    fun getCurrentFont(): Font

    /**
     * Sets the [Font] to use.
     * @return true if successful, false if not (if the font was set from another thread for example).
     */
    fun useFont(font: Font): Boolean

    /**
     * Sets the override [Font] to its default value (which is `NO_FONT`).
     */
    fun resetFont()
}
