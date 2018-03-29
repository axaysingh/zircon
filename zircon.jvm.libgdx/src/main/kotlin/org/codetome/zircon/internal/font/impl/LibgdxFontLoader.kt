package org.codetome.zircon.internal.font.impl

import org.codetome.zircon.api.font.CharacterMetadata
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.internal.font.FontLoader
import org.codetome.zircon.internal.font.MetadataPickingStrategy
import org.codetome.zircon.internal.font.cache.DefaultFontRegionCache
import org.codetome.zircon.internal.font.cache.NoFontRegionCache
import org.codetome.zircon.internal.font.transformer.LibgdxFontRegionCloner
import org.codetome.zircon.internal.font.transformer.LibgdxFontRegionColorizer
import java.io.InputStream

class LibgdxFontLoader : FontLoader {

    override fun fetchPhysicalFont(size: Float,
                                   source: InputStream,
                                   cacheFonts: Boolean,
                                   withAntiAlias: Boolean): Font {
        TODO("Implement font loading")
    }

    override fun fetchTiledFont(width: Int,
                                height: Int,
                                source: InputStream,
                                cacheFonts: Boolean,
                                metadata: Map<Char, List<CharacterMetadata>>,
                                metadataPickingStrategy: MetadataPickingStrategy): Font {
        return LibgdxTiledFont(
                source = source,
                metadata = metadata,
                width = width,
                height = height,
                cache = if (cacheFonts) {
                    DefaultFontRegionCache()
                } else {
                    NoFontRegionCache()
                },
                regionTransformers = TILE_TRANSFORMERS)
    }

    companion object {
        private val TILE_TRANSFORMERS = listOf(
                LibgdxFontRegionCloner(),
                LibgdxFontRegionColorizer())
    }
}
