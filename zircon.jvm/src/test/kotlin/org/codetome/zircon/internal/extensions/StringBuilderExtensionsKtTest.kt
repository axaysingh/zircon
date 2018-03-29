package org.codetome.zircon.internal.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.lang.StringBuilder

class StringBuilderExtensionsKtTest {

    val target = StringBuilder(STRING)

    @Test
    fun shouldBePresentWhenGetIfPresentIsCalledWithValidIdx() {
        assertThat(target.getIfPresent(STRING.length - 1)).isPresent
    }

    @Test
    fun shouldNotBePresentWhenGetIfPresentIsCalledWithInvalidIdx() {
        assertThat(target.getIfPresent(STRING.length)).isNotPresent
    }

    companion object {
        val STRING = "STRING"
    }
}