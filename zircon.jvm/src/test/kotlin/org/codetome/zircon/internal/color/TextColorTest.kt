package org.codetome.zircon.internal.color

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.color.TextColorFactory
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.internal.color.DefaultTextColor
import org.junit.Test

class TextColorTest {

    @Test
    fun shouldProperlyParseColorFromEnumNameWhenFromStringIsCalled() {
        val result = TextColorFactory.fromString("BLUE")

        assertThat(result).isEqualTo(ANSITextColor.BLUE)
    }

    @Test
    fun shouldProperlyParseColorFromUppercaseCssFormatNameWhenFromStringIsCalled() {
        val result = TextColorFactory.fromString("#AA4455")

        assertThat(result).isEqualTo(EXPECTED_RESULT_COLOR)
    }

    @Test
    fun shouldProperlyParseColorFromLowercaseCssFormatNameWhenFromStringIsCalled() {
        val result = TextColorFactory.fromString("#aa4455")

        assertThat(result).isEqualTo(EXPECTED_RESULT_COLOR)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailToParseFromMangledCssFormatWhenFromStringIsCalled() {
        TextColorFactory.fromString("#xx4455")
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailToParseWithUnknownEnumNameWhenFromStringIsCalled() {
        TextColorFactory.fromString("wtf-blue")
    }

    @Test
    fun shouldProperlyCreateFromRedGreenBlueWhenCreateFromIsCalled() {
        assertThat(TextColorFactory.fromRGB(
                red = EXPECTED_RESULT_COLOR.getRed(),
                green = EXPECTED_RESULT_COLOR.getGreen(),
                blue = EXPECTED_RESULT_COLOR.getBlue()))
                .isEqualTo(EXPECTED_RESULT_COLOR)
    }

    companion object {
        val EXPECTED_RESULT_COLOR = DefaultTextColor(170, 68, 85)
    }
}