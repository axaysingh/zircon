package org.codetome.zircon.internal.font.impl

import org.codetome.zircon.api.font.CharacterMetadata
import org.codetome.zircon.internal.font.MetadataPickingStrategy
import java.util.*

class PickRandomMetaStrategy : MetadataPickingStrategy {

    private val random = Random()

    override fun pickMetadata(metas: List<CharacterMetadata>): CharacterMetadata {
        return metas[random.nextInt(metas.size)]
    }
}