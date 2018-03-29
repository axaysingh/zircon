package org.codetome.zircon.internal.component.impl

import org.assertj.core.api.Assertions
import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.resource.ColorThemeResource
import org.codetome.zircon.api.component.ComponentState
import org.codetome.zircon.api.component.builder.CheckBoxBuilder
import org.codetome.zircon.api.color.TextColorFactory
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.input.MouseAction
import org.codetome.zircon.api.input.MouseActionType
import org.codetome.zircon.api.resource.CP437TilesetResource
import org.codetome.zircon.internal.event.EventBus
import org.codetome.zircon.internal.event.EventType
import org.codetome.zircon.internal.font.FontLoaderRegistry
import org.codetome.zircon.internal.font.impl.TestFontLoader
import org.codetome.zircon.internal.font.impl.VirtualFontLoader
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class DefaultCheckBoxTest {

    lateinit var target: DefaultCheckBox
    lateinit var font: Font

    @Before
    fun setUp() {
        FontLoaderRegistry.setFontLoader(TestFontLoader())
        font = FONT.toFont()
        target = CheckBoxBuilder.newBuilder()
                .componentStyles(COMPONENT_STYLES)
                .font(font)
                .position(POSITION)
                .text(TEXT)
                .build() as DefaultCheckBox
    }

    @Test
    fun shouldProperlyAddCheckBoxText() {
        val surface = target.getDrawSurface()
        val offset = 4
        TEXT.forEachIndexed { i, char ->
            Assertions.assertThat(surface.getCharacterAt(Position.of(i + offset, 0)).get())
                    .isEqualTo(TextCharacterBuilder.newBuilder()
                            .character(char)
                            .styleSet(DEFAULT_STYLE)
                            .build())
        }
    }

    @Test
    fun shouldUseProperFont() {
        Assertions.assertThat(target.getCurrentFont().getId())
                .isEqualTo(font.getId())
    }

    @Test
    fun shouldProperlyReturnText() {
        Assertions.assertThat(target.getText()).isEqualTo(TEXT)
    }

    @Test
    fun shouldProperlyApplyTheme() {
        target.applyColorTheme(THEME)
        val styles = target.getComponentStyles()
        Assertions.assertThat(styles.getStyleFor(ComponentState.DEFAULT))
                .isEqualTo(EXPECTED_DEFAULT_STYLE)
        Assertions.assertThat(styles.getStyleFor(ComponentState.MOUSE_OVER))
                .isEqualTo(EXPECTED_MOUSE_OVER_STYLE)
        Assertions.assertThat(styles.getStyleFor(ComponentState.FOCUSED))
                .isEqualTo(EXPECTED_FOCUSED_STYLE)
        Assertions.assertThat(styles.getStyleFor(ComponentState.ACTIVE))
                .isEqualTo(EXPECTED_ACTIVE_STYLE)
        Assertions.assertThat(styles.getStyleFor(ComponentState.DISABLED))
                .isEqualTo(EXPECTED_DEFAULT_STYLE)
    }

    @Test
    fun shouldAcceptFocus() {
        Assertions.assertThat(target.acceptsFocus()).isTrue()
    }

    @Test
    fun shouldProperlyGiveFocus() {
        target.applyColorTheme(THEME)
        val componentChanged = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentChange, {
            componentChanged.set(true)
        })

        val result = target.giveFocus()

        Assertions.assertThat(result).isTrue()
        Assertions.assertThat(componentChanged.get()).isTrue()
        Assertions.assertThat(target.getComponentStyles().getCurrentStyle()).isEqualTo(EXPECTED_FOCUSED_STYLE)
    }

    @Test
    fun shouldProperlyTakeFocus() {
        target.applyColorTheme(THEME)
        val componentChanged = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentChange, {
            componentChanged.set(true)
        })

        target.takeFocus()

        Assertions.assertThat(componentChanged.get()).isTrue()
        Assertions.assertThat(target.getComponentStyles().getCurrentStyle()).isEqualTo(EXPECTED_DEFAULT_STYLE)
    }

//    @Test TODO: re-enable in next release
//    fun shouldProperlyHandleMousePress() {
//        target.applyColorTheme(THEME)
//        val componentChanged = AtomicBoolean(false)
//        EventBus.subscribe(EventType.ComponentChange, {
//            componentChanged.set(true)
//        })
//
//        EventBus.emit(
//                type = EventType.MousePressed(target.getId()),
//                data = MouseAction(MouseActionType.MOUSE_PRESSED, 1, Position.DEFAULT_POSITION))
//
//        Assertions.assertThat(componentChanged.get()).isTrue()
//        Assertions.assertThat(target.getComponentStyles().getCurrentStyle()).isEqualTo(EXPECTED_ACTIVE_STYLE)
//    }

    @Test
    fun shouldProperlyHandleMouseRelease() {
        target.applyColorTheme(THEME)
        val componentChanged = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentChange, {
            componentChanged.set(true)
        })

        EventBus.emit(
                type = EventType.MouseReleased(target.getId()),
                data = MouseAction(MouseActionType.MOUSE_PRESSED, 1, Position.DEFAULT_POSITION))

        Assertions.assertThat(componentChanged.get()).isTrue()
        Assertions.assertThat(target.getComponentStyles().getCurrentStyle()).isEqualTo(EXPECTED_MOUSE_OVER_STYLE)
    }

    companion object {
        val THEME = ColorThemeResource.ADRIFT_IN_DREAMS.getTheme()
        val TEXT = "Button text"
        val POSITION = Position.of(4, 5)
        val FONT = CP437TilesetResource.WANDERLUST_16X16
        val DEFAULT_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITextColor.RED)
                .foregroundColor(ANSITextColor.GREEN)
                .modifiers(Modifiers.CROSSED_OUT)
                .build()
        val COMPONENT_STYLES = ComponentStylesBuilder.newBuilder()
                .defaultStyle(DEFAULT_STYLE)
                .build()

        val EXPECTED_DEFAULT_STYLE = StyleSetBuilder.newBuilder()
                .foregroundColor(THEME.getAccentColor())
                .backgroundColor(TextColorFactory.TRANSPARENT)
                .build()

        val EXPECTED_MOUSE_OVER_STYLE = StyleSetBuilder.newBuilder()
                .foregroundColor(THEME.getBrightBackgroundColor())
                .backgroundColor(THEME.getAccentColor())
                .build()

        val EXPECTED_FOCUSED_STYLE = StyleSetBuilder.newBuilder()
                .foregroundColor(THEME.getDarkBackgroundColor())
                .backgroundColor(THEME.getAccentColor())
                .build()

        val EXPECTED_ACTIVE_STYLE = StyleSetBuilder.newBuilder()
                .foregroundColor(THEME.getDarkForegroundColor())
                .backgroundColor(THEME.getAccentColor())
                .build()
    }
}
