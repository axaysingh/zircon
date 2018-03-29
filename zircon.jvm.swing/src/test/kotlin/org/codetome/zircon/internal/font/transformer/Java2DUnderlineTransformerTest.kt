package org.codetome.zircon.internal.font.transformer

import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.internal.font.impl.Java2DFontTextureRegion
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

class Java2DUnderlineTransformerTest {

    lateinit var target: Java2DUnderlineTransformer

    @Before
    fun setUp() {
        target = Java2DUnderlineTransformer()
    }

    @Test
    fun shouldProperlyRun() {
        val image = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB)
        target.transform(Java2DFontTextureRegion(image), CHAR)

        // TODO: check underline?
    }

    companion object {
        val WIDTH = 10
        val HEIGHT = 10
        val CHAR = TextCharacterBuilder.newBuilder()
                .modifiers(Modifiers.UNDERLINE)
                .build()
    }
}