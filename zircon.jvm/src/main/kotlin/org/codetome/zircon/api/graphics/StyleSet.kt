package org.codetome.zircon.api.graphics

import org.codetome.zircon.api.Modifier
import org.codetome.zircon.api.color.TextColor
import org.codetome.zircon.internal.behavior.Cacheable

/**
 * Represents style information which is handled by Zircon like
 * - background color
 * - foreground color and
 * - modifiers
 * and a set of useful operations on them.
 */
interface StyleSet : Cacheable {

    /**
     * Returns the foreground color.
     */
    fun getForegroundColor(): TextColor

    /**
     * Returns the background color.
     */
    fun getBackgroundColor(): TextColor

    /**
     * Returns the modifiers.
     */
    fun getModifiers(): Set<Modifier>

    /**
     * Returns a copy of the style information stored in this [StyleSet].
     */
    fun createCopy(): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given background color.
     */
    fun withBackgroundColor(backgroundColor: TextColor): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given foreground color.
     */
    fun withForegroundColor(foregroundColor: TextColor): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given modifiers added.
     */
    fun withAddedModifiers(modifiers: Set<Modifier>): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given modifiers added.
     */
    fun withAddedModifiers(vararg modifiers: Modifier): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given modifiers removed.
     */
    fun withRemovedModifiers(modifiers: Set<Modifier>): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given modifiers removed.
     */
    fun withRemovedModifiers(vararg modifiers: Modifier): StyleSet

    /**
     * Creates a copy of this [StyleSet] with the given modifiers.
     */
    fun withModifiers(modifiers: Set<Modifier>): StyleSet
    /**
     * Creates a copy of this [StyleSet] with the given modifiers.
     */
    fun withModifiers(vararg modifiers: Modifier): StyleSet

    /**
     * Creates a copy of this [StyleSet] with no modifiers.
     */
    fun withoutModifiers(): StyleSet

}

