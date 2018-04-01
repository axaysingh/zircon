package org.codetome.zircon.internal.font.impl

import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.extension.toAWTColor
import org.codetome.zircon.api.font.CharacterMetadata
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.font.FontTextureRegion
import org.codetome.zircon.api.util.FontUtils
import org.codetome.zircon.internal.extensions.isNotPresent
import org.codetome.zircon.internal.font.FontRegionCache
import org.codetome.zircon.internal.font.transformer.*
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.util.*

/**
 * Represents a physical font which is backed by [java.awt.Font].
 */
class Java2DPhysicalFont(private val source: java.awt.Font,
                         private val width: Int,
                         private val height: Int,
                         private val cache: FontRegionCache<FontTextureRegion<BufferedImage>>,
                         private val withAntiAlias: Boolean) : Font {

    private val id = UUID.randomUUID()

    init {
        require(FontUtils.isFontMonospaced(source)) {
            "Font '${source.name} is not monospaced!"
        }
    }

    override fun getId(): UUID = id

    override fun getWidth() = width

    override fun getHeight() = height

    override fun hasDataForChar(char: Char) = source.canDisplay(char)

    override fun fetchRegionForChar(textCharacter: TextCharacter): FontTextureRegion<*> {

        val maybeRegion: Optional<FontTextureRegion<BufferedImage>> = cache.retrieveIfPresent(textCharacter)

        var region: FontTextureRegion<BufferedImage> = if (maybeRegion.isNotPresent()) {
            val image = BufferedImage(width, height, BufferedImage.TRANSLUCENT)
            val g = image.graphics as Graphics2D
            g.color = textCharacter.getBackgroundColor().toAWTColor()
            g.fillRect(0, 0, width, height)
            g.color = textCharacter.getForegroundColor().toAWTColor()
            g.font = source
            if (withAntiAlias) {
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            }

            val str = textCharacter.getCharacter().toString()

            val fm = g.getFontMetrics(source)
            val rect = fm.getStringBounds(str, g)

            val textHeight = rect.height.toInt()
            val textWidth = rect.width.toInt()
            val panelHeight = this.getHeight()
            val panelWidth = this.getWidth()

            val x = (panelWidth - textWidth) / 2
            val y = (panelHeight - textHeight) / 2 + fm.ascent

            g.drawString(str, x, y)
            g.dispose()
            val region = Java2DFontTextureRegion(image)
            cache.store(textCharacter, region)
            region
        } else {
            maybeRegion.get()
        }
        textCharacter.getModifiers().forEach {
            region = MODIFIER_TRANSFORMER_LOOKUP[it]?.transform(region, textCharacter) ?: region
        }
        return region
    }

    override fun fetchMetadataForChar(char: Char) = listOf<CharacterMetadata>()

    companion object {
        val MODIFIER_TRANSFORMER_LOOKUP = mapOf(
                Pair(Modifiers.UNDERLINE, Java2DUnderlineTransformer()),
                Pair(Modifiers.VERTICAL_FLIP, Java2DVerticalFlipper()),
                Pair(Modifiers.HORIZONTAL_FLIP, Java2DHorizontalFlipper()),
                Pair(Modifiers.CROSSED_OUT, Java2DCrossedOutTransformer()),
                Pair(Modifiers.BLINK, NoOpTransformer()),
                Pair(Modifiers.HIDDEN, Java2DHiddenTransformer())
        ).toMap()
    }
}
