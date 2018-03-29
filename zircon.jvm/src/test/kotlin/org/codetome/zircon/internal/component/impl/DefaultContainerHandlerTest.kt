package org.codetome.zircon.internal.component.impl

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.component.builder.ButtonBuilder
import org.codetome.zircon.api.component.builder.PanelBuilder
import org.codetome.zircon.api.input.Input
import org.codetome.zircon.api.input.InputType
import org.codetome.zircon.api.input.KeyStroke
import org.codetome.zircon.api.input.MouseAction
import org.codetome.zircon.api.input.MouseActionType.*
import org.codetome.zircon.internal.component.impl.wrapping.BorderWrappingStrategy
import org.codetome.zircon.internal.component.impl.wrapping.ShadowWrappingStrategy
import org.codetome.zircon.internal.event.EventBus
import org.codetome.zircon.internal.event.EventType
import org.codetome.zircon.internal.font.impl.FontSettings
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class DefaultContainerHandlerTest {

    lateinit var target: DefaultContainerHandler

    @Before
    fun setUp() {
        target = DefaultContainerHandler(DefaultContainer(
                initialSize = SIZE,
                position = Position.DEFAULT_POSITION,
                componentStyles = STYLES,
                wrappers = WRAPPERS,
                initialFont = FontSettings.NO_FONT))
    }

    @Test
    fun shouldProperlyRemoveComponent() {
        val button = createButton()
        target.addComponent(button)
        assertThat(target.transformComponentsToLayers()).hasSize(2)

        val componentChanged = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentChange, {
            componentChanged.set(true)
        })


        val result = target.removeComponent(button)

        assertThat(componentChanged.get()).isTrue()
        assertThat(result).isTrue()
        assertThat(target.transformComponentsToLayers()).hasSize(1) // default container
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotLetToAddAComponentWhichIsBiggerThanTheContainer() {
        target.addComponent(PanelBuilder.newBuilder()
                .size(Size.of(999, 999))
                .build())
    }

    @Test
    fun shouldProperlyHandleMouseOverWhenActive() {
        target.activate()

        val button = createButton()
        target.addComponent(button)

        val componentHovered = AtomicBoolean(false)
        EventBus.subscribe(EventType.MouseOver(button.getId()), {
            componentHovered.set(true)
        })

        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_MOVED, 1, BUTTON_POSITION))

        assertThat(componentHovered.get()).isTrue()
    }

    @Test
    fun shouldProperlyHandleMouseOverWhenHoveredTwice() {
        target.activate()

        val button = createButton()
        target.addComponent(button)

        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_MOVED, 1, BUTTON_POSITION))

        val componentHovered = AtomicBoolean(false)
        EventBus.subscribe(EventType.MouseOver(button.getId()), {
            componentHovered.set(true)
        })

        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_MOVED, 1, BUTTON_POSITION.withRelativeX(1)))

        assertThat(componentHovered.get()).isFalse()
    }

    @Test
    fun shouldProperlyHandleMousePressedWhenActive() {
        target.activate()

        val button = createButton()
        target.addComponent(button)

        val pressed = AtomicBoolean(false)
        EventBus.subscribe<MouseAction>(EventType.MousePressed(button.getId()), {
            pressed.set(true)
        })

        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_PRESSED, 1, BUTTON_POSITION))

        assertThat(pressed.get()).isTrue()
    }

    @Test
    fun shouldProperlyHandleMouseReleasedWhenActive() {
        target.activate()

        val button = createButton()
        target.addComponent(button)

        val released = AtomicBoolean(false)
        EventBus.subscribe<MouseAction>(EventType.MouseReleased(button.getId()), {
            released.set(true)
        })

        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_RELEASED, 1, BUTTON_POSITION))

        assertThat(released.get()).isTrue()
    }

    @Test
    fun shouldNotHandleEventsWhenInactive() {
        target.deactivate()

        val button = createButton()
        target.addComponent(button)

        val events = mutableListOf<Boolean>()
        EventBus.subscribe(EventType.MouseOver(button.getId()), {
            events.add(true)
        })
        EventBus.subscribe(EventType.MousePressed(button.getId()), {
            events.add(true)
        })
        EventBus.subscribe(EventType.MouseReleased(button.getId()), {
            events.add(true)
        })

        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_MOVED, 1, BUTTON_POSITION))
        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_PRESSED, 1, BUTTON_POSITION))
        EventBus.emit<Input>(EventType.Input, MouseAction(MOUSE_RELEASED, 1, BUTTON_POSITION))


        assertThat(events).isEmpty()
    }

    @Test
    fun shouldProperlyFocusNextWhenTabPressed() {
        target.activate()

        val button = createButton()
        target.addComponent(button)

        assertThat(button.getComponentStyles().getCurrentStyle()).isNotEqualTo(FOCUSED_STYLE)

        EventBus.emit<Input>(EventType.Input, KeyStroke(type = InputType.Tab))

        assertThat(button.getComponentStyles().getCurrentStyle()).isEqualTo(FOCUSED_STYLE)
    }

    @Test
    fun shouldProperlyFocusPrevWhenShiftTabPressed() {
        target.activate()

        val button = createButton()
        target.addComponent(button)
        val other = ButtonBuilder.newBuilder()
                .text(BUTTON_TEXT)
                .position(Position.of(0, 1)
                        .relativeToBottomOf(button))
                .build()
        target.addComponent(other)

        EventBus.emit<Input>(EventType.Input, KeyStroke(type = InputType.Tab))
        EventBus.emit<Input>(EventType.Input, KeyStroke(type = InputType.Tab))

        assertThat(button.getComponentStyles().getCurrentStyle()).isEqualTo(DEFAULT_STYLE)

        EventBus.emit<Input>(EventType.Input, KeyStroke(shiftDown = true, type = InputType.ReverseTab))

        assertThat(button.getComponentStyles().getCurrentStyle()).isEqualTo(FOCUSED_STYLE)
    }

    @Test
    fun shouldProperlyHandleSpacePressedOnFocusedWhenActive() {
        target.activate()

        val button = createButton()
        target.addComponent(button)

        val released = AtomicBoolean(false)
        EventBus.subscribe<MouseAction>(EventType.MouseReleased(button.getId()), {
            released.set(true)
        })

        EventBus.emit<Input>(EventType.Input, KeyStroke(type = InputType.Tab))
        EventBus.emit<Input>(EventType.Input, KeyStroke(type = InputType.Character, character = ' '))

        assertThat(released.get()).isTrue()
    }

    private fun createButton() = ButtonBuilder.newBuilder()
            .position(BUTTON_POSITION)
            .text(BUTTON_TEXT)
            .componentStyles(STYLES)
            .build()

    companion object {
        val SIZE = Size.of(30, 20)
        val BUTTON_TEXT = "TEXT"
        val BUTTON_POSITION = Position.of(6, 7)
        val DEFAULT_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITextColor.BLUE)
                .foregroundColor(ANSITextColor.RED)
                .build()
        val ACTIVE_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITextColor.GREEN)
                .foregroundColor(ANSITextColor.YELLOW)
                .build()
        val DISABLED_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITextColor.MAGENTA)
                .foregroundColor(ANSITextColor.BLUE)
                .build()
        val FOCUSED_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITextColor.YELLOW)
                .foregroundColor(ANSITextColor.CYAN)
                .build()
        val MOUSE_OVER_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITextColor.RED)
                .foregroundColor(ANSITextColor.CYAN)
                .build()
        val STYLES = ComponentStylesBuilder.newBuilder()
                .defaultStyle(DEFAULT_STYLE)
                .activeStyle(ACTIVE_STYLE)
                .disabledStyle(DISABLED_STYLE)
                .focusedStyle(FOCUSED_STYLE)
                .mouseOverStyle(MOUSE_OVER_STYLE)
                .build()
        val WRAPPERS = listOf(
                ShadowWrappingStrategy(),
                BorderWrappingStrategy(Modifiers.BORDER))
    }
}
