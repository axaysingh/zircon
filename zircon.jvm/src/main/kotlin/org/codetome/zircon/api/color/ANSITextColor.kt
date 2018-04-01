package org.codetome.zircon.api.color

import org.codetome.zircon.api.color.TextColorFactory.DEFAULT_ALPHA
import org.codetome.zircon.internal.color.DefaultTextColor
import java.awt.Color

/**
 * Default ANSI colors.
 */
enum class ANSITextColor(private val red: Int,
                         private val green: Int,
                         private val blue: Int,
                         private val alpha: Int) : TextColor {

    BLACK(0, 0, 0, DEFAULT_ALPHA),
    RED(170, 0, 0, DEFAULT_ALPHA),
    GREEN(0, 170, 0, DEFAULT_ALPHA),
    YELLOW(170, 85, 0, DEFAULT_ALPHA),
    BLUE(0, 0, 170, DEFAULT_ALPHA),
    MAGENTA(170, 0, 170, DEFAULT_ALPHA),
    CYAN(0, 170, 170, DEFAULT_ALPHA),
    WHITE(170, 170, 170, DEFAULT_ALPHA),
    DEFAULT(0, 0, 0, DEFAULT_ALPHA);

    override fun generateCacheKey() = name

    override fun getRed() = red

    override fun getGreen() = green

    override fun getBlue() = blue

    override fun getAlpha() = alpha

    override fun tint(): TextColor {
        return TextColorFactory.fromRGB(red, green, blue, alpha).tint()
    }

    override fun shade(): TextColor {
        return TextColorFactory.fromRGB(red, green, blue, alpha).shade()
    }

    override fun invert(): TextColor {
        return TextColorFactory.fromRGB(red, green, blue, alpha).invert()
    }
}
