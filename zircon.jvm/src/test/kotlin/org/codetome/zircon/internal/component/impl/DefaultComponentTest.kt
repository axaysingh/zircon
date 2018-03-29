package org.codetome.zircon.internal.component.impl

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.builder.TextImageBuilder
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.component.ColorTheme
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.input.Input
import org.codetome.zircon.api.input.MouseAction
import org.codetome.zircon.api.input.MouseActionType
import org.codetome.zircon.api.resource.CP437TilesetResource
import org.codetome.zircon.internal.behavior.impl.DefaultBoundable
import org.codetome.zircon.internal.component.impl.wrapping.BorderWrappingStrategy
import org.codetome.zircon.internal.component.impl.wrapping.ShadowWrappingStrategy
import org.codetome.zircon.internal.event.EventBus
import org.codetome.zircon.internal.event.EventType
import org.codetome.zircon.internal.font.FontLoaderRegistry
import org.codetome.zircon.internal.font.impl.TestFontLoader
import org.codetome.zircon.internal.font.impl.VirtualFontLoader
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

class DefaultComponentTest {

    lateinit var target: DefaultComponent
    lateinit var font: Font

    @Before
    fun setUp() {
        FontLoaderRegistry.setFontLoader(TestFontLoader())
        font = FONT.toFont()
        target = object : DefaultComponent(
                initialSize = SIZE,
                position = POSITION,
                componentStyles = STYLES,
                wrappers = WRAPPERS,
                initialFont = font) {
            override fun applyColorTheme(colorTheme: ColorTheme) {
                TODO("not implemented")
            }

            override fun acceptsFocus(): Boolean {
                TODO("not implemented")
            }

            override fun giveFocus(input: Optional<Input>): Boolean {
                TODO("not implemented")
            }

            override fun takeFocus(input: Optional<Input>) {
                TODO("not implemented")
            }
        }
    }

    @Test
    fun shouldUseFontFromComponentWhenTransformingToLayer() {
        val result = target.transformToLayers()
        result.forEach{
            assertThat(it.getCurrentFont().getId()).isEqualTo(font.getId())
        }
    }

    @Test
    fun shouldProperlyApplyStylesOnInit() {
        assertThat(target.getComponentStyles().getCurrentStyle())
                .isEqualTo(STYLES.getCurrentStyle())
    }

    @Test
    fun shouldProperlyApplyStylesOnMouseOver() {
        val componentChanged = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentChange, {
            componentChanged.set(true)
        })

        EventBus.emit(EventType.MouseOver(target.getId()))

        val targetChar = target.getDrawSurface().getCharacterAt(Position.DEFAULT_POSITION).get()
        assertThat(targetChar.getBackgroundColor()).isEqualTo(MOUSE_OVER_STYLE.getBackgroundColor())
        assertThat(targetChar.getForegroundColor()).isEqualTo(MOUSE_OVER_STYLE.getForegroundColor())
        assertThat(componentChanged.get()).isTrue()
    }

    @Test
    fun shouldProperlyApplyStylesOnMouseOut() {
        EventBus.emit(EventType.MouseOver(target.getId()))
        val componentChanged = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentChange, {
            componentChanged.set(true)
        })

        EventBus.emit(EventType.MouseOut(target.getId()))

        val targetChar = target.getDrawSurface().getCharacterAt(Position.DEFAULT_POSITION).get()
        assertThat(targetChar.getBackgroundColor()).isEqualTo(DEFAULT_STYLE.getBackgroundColor())
        assertThat(targetChar.getForegroundColor()).isEqualTo(DEFAULT_STYLE.getForegroundColor())
        assertThat(componentChanged.get()).isTrue()
    }

    @Test
    fun shouldProperlySetNewPosition() {
        target.setPosition(NEW_POSITION)

        assertThat(target.getPosition()).isEqualTo(NEW_POSITION)
    }

    @Test
    fun shouldContainBoundableWhichIsContained() {
        assertThat(target.containsBoundable(DefaultBoundable(SIZE - Size.ONE, POSITION))).isTrue()
    }

    @Test
    fun shouldNotContainBoundableWhichIsContained() {
        assertThat(target.containsBoundable(DefaultBoundable(SIZE + Size.ONE, POSITION))).isFalse()
    }

    @Test
    fun shouldContainPositionWhichIsContained() {
        assertThat(target.containsPosition(POSITION)).isTrue()
    }

    @Test
    fun shouldNotContainPositionWhichIsContained() {
        assertThat(target.containsPosition(POSITION - Position.OFFSET_1x1)).isFalse()
    }

    @Test
    fun shouldProperlyDrawOntoTextImage() {
        val image = TextImageBuilder.newBuilder()
                .size(SIZE + Size.of(POSITION.x, POSITION.y))
                .build()
        target.drawOnto(image)

        assertThat(image.getCharacterAt(POSITION - Position.OFFSET_1x1).get())
                .isEqualTo(TextCharacterBuilder.EMPTY)

        target.getBoundableSize().fetchPositions().forEach {
            assertThat(image.getCharacterAt(it + POSITION).get())
                    .isEqualTo(target.getDrawSurface().getCharacterAt(it).get())
        }
    }

    @Test
    fun shouldProperlyFetchByPositionWhenContainsPosition() {
        assertThat(target.fetchComponentByPosition(POSITION).get()).isEqualTo(target)
    }

    @Test
    fun shouldNotFetchByPositionWhenDoesNotContainPosition() {
        assertThat(target.fetchComponentByPosition(Position.of(100, 100))).isNotPresent
    }

    @Test
    fun shouldProperlyListenToMousePress() {
        val pressed = AtomicBoolean(false)
        target.onMousePressed(Consumer {
            pressed.set(true)
        })

        EventBus.emit(EventType.MousePressed(target.getId()), MouseAction(MouseActionType.MOUSE_PRESSED, 1, POSITION))

        assertThat(pressed.get()).isTrue()
    }

    @Test
    fun shouldNotListenToMousePressOnOtherComponents() {
        val pressed = AtomicBoolean(false)
        target.onMousePressed(Consumer {
            pressed.set(true)
        })

        EventBus.emit(EventType.MousePressed(UUID.randomUUID()), MouseAction(MouseActionType.MOUSE_PRESSED, 1, POSITION))

        assertThat(pressed.get()).isFalse()
    }

    @Test
    fun shouldProperlyListenToMouseRelease() {
        val pressed = AtomicBoolean(false)
        target.onMouseReleased(Consumer {
            pressed.set(true)
        })

        EventBus.emit(EventType.MouseReleased(target.getId()), MouseAction(MouseActionType.MOUSE_RELEASED, 1, POSITION))

        assertThat(pressed.get()).isTrue()
    }

    @Test
    fun shouldNotListenToMouseReleaseOnOtherComponents() {
        val pressed = AtomicBoolean(false)
        target.onMouseReleased(Consumer {
            pressed.set(true)
        })

        EventBus.emit(EventType.MouseReleased(UUID.randomUUID()), MouseAction(MouseActionType.MOUSE_RELEASED, 1, POSITION))

        assertThat(pressed.get()).isFalse()
    }

    @Test
    fun shouldProperlyTransformToLayers() {
        val result = target.transformToLayers()
        assertThat(result).hasSize(1)
        assertThat(result.first().getBoundableSize()).isEqualTo(target.getBoundableSize())
        assertThat(result.first().getPosition()).isEqualTo(target.getPosition())
    }

    @Test
    fun shouldBeEqualToItself() {
        assertThat(target).isEqualTo(target)
    }

    companion object {
        val FONT = CP437TilesetResource.ROGUE_YUN_16X16
        val SIZE = Size.of(4, 4)
        val POSITION = Position.of(2, 3)
        val NEW_POSITION = Position.of(6, 7)
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
