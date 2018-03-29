package org.codetome.zircon.api.builder

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.junit.Test

class TextImageBuilderTest {

    @Test
    fun shouldBuildProperTextImage() {
        val result = TextImageBuilder.newBuilder()
                .filler(FILLER)
                .size(SIZE)
                .build()

        assertThat(result.getBoundableSize()).isEqualTo(SIZE)

        assertThat(result.getCharacterAt(Position.of(SIZE.xLength - 1, SIZE.yLength - 1)).get())
                .isEqualTo(FILLER)
    }

    companion object {
        val FILLER = TextCharacterBuilder.newBuilder().character('a').build()
        val SIZE = Size.of(5, 5)
    }
}
