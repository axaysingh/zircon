package org.codetome.zircon.internal.font.impl

import org.codetome.zircon.api.TextCharacter
import org.codetome.zircon.api.font.Font
import java.util.*

class NoFont : Font {

    private val id = UUID.randomUUID()

    override fun getId() = id

    override fun getWidth() = signalNoOp()

    override fun getHeight() = signalNoOp()

    override fun hasDataForChar(char: Char) = signalNoOp()

    override fun fetchRegionForChar(textCharacter: TextCharacter) = signalNoOp()

    override fun fetchMetadataForChar(char: Char) = signalNoOp()

    private fun signalNoOp(): Nothing = TODO("No Font was supplied! Try setting a Font!")
}