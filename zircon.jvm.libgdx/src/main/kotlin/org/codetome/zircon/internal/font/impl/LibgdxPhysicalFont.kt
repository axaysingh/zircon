package org.codetome.zircon.internal.font.impl

import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.font.CharacterMetadata
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.font.FontTextureRegion
import org.codetome.zircon.api.util.FontUtils
import org.codetome.zircon.internal.extensions.isNotPresent
import org.codetome.zircon.internal.font.FontRegionCache
import org.codetome.zircon.internal.font.transformer.NoOpTransformer
import java.util.*

/**
 * Represents a physical font which is backed by [java.awt.Font].
 */
class LibgdxPhysicalFont(private val source: java.awt.Font,
                         private val width: Int,
                         private val height: Int,
                         private val cache: FontRegionCache<FontTextureRegion<TextureRegion>>,
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

        val maybeRegion: Optional<FontTextureRegion<TextureRegion>> = cache.retrieveIfPresent(textCharacter)

        var region: FontTextureRegion<TextureRegion> = if (maybeRegion.isNotPresent()) {
            TODO()
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
                Pair(Modifiers.BLINK, NoOpTransformer())
        ).toMap()
    }
}
