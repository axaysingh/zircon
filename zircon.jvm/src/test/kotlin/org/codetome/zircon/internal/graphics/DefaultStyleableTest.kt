package org.codetome.zircon.internal.graphics

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.color.TextColorFactory
import org.codetome.zircon.internal.behavior.impl.DefaultStyleable
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

class DefaultStyleableTest {

    lateinit var target: DefaultStyleable

    @Before
    fun setUp() {
        target = DefaultStyleable(AtomicReference(StyleSetBuilder.DEFAULT_STYLE))
    }

    @Test
    fun shouldHaveNoModifiersByDefault() {
        assertThat(target.getActiveModifiers()).isEmpty()
    }

    @Test
    fun shouldHaveProperFGByDefault() {
        assertThat(target.getForegroundColor())
                .isEqualTo(TextColorFactory.DEFAULT_FOREGROUND_COLOR)
    }

    @Test
    fun shouldHaveProperBGByDefault() {
        assertThat(target.getBackgroundColor())
                .isEqualTo(TextColorFactory.DEFAULT_BACKGROUND_COLOR)
    }

    @Test
    fun shouldProperlyEnableModifier() {
        val modifier = Modifiers.BOLD

        target.enableModifiers(modifier)

        assertThat(target.getActiveModifiers()).containsExactly(modifier)
    }

    @Test
    fun shouldProperlyDisableModifier() {
        val modifier = Modifiers.BOLD

        target.enableModifiers(modifier)
        target.disableModifiers(modifier)

        assertThat(target.getActiveModifiers()).isEmpty()
    }

    @Test
    fun shouldProperlyEnableModifiers() {
        target.enableModifiers(setOf(Modifiers.BOLD, Modifiers.CROSSED_OUT))

        assertThat(target.getActiveModifiers()).containsExactlyInAnyOrder(Modifiers.BOLD, Modifiers.CROSSED_OUT)
    }

    @Test
    fun shouldProperlySetModifiers() {
        val modifiers = setOf(Modifiers.BOLD, Modifiers.CROSSED_OUT)

        target.setModifiers(modifiers)

        assertThat(target.getActiveModifiers()).containsExactlyInAnyOrder(Modifiers.BOLD, Modifiers.CROSSED_OUT)
    }

    @Test
    fun shouldProperlyClearModifiers() {
        target.enableModifiers(Modifiers.BOLD)

        target.clearModifiers()

        assertThat(target.getActiveModifiers())
                .isEmpty()
    }

    @Test
    fun twoIdenticalStyleSetsShouldBeEqual() {
        assertThat(StyleSetBuilder.DEFAULT_STYLE).isEqualTo(StyleSetBuilder.DEFAULT_STYLE)
    }
}