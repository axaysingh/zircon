package org.codetome.zircon.internal.component.impl

import org.assertj.core.api.Assertions.assertThat
import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.builder.ScreenBuilder
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.builder.VirtualTerminalBuilder
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.component.builder.HeaderBuilder
import org.codetome.zircon.api.component.builder.LabelBuilder
import org.codetome.zircon.api.component.builder.PanelBuilder
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.resource.CP437TilesetResource
import org.codetome.zircon.internal.component.impl.wrapping.BorderWrappingStrategy
import org.codetome.zircon.internal.component.impl.wrapping.ShadowWrappingStrategy
import org.codetome.zircon.internal.event.EventBus
import org.codetome.zircon.internal.event.EventType
import org.codetome.zircon.internal.font.FontLoaderRegistry
import org.codetome.zircon.internal.font.impl.TestFontLoader
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class DefaultContainerTest {

    lateinit var target: DefaultContainer
    lateinit var goodFont: Font
    lateinit var badFont: Font

    @Before
    fun setUp() {
        goodFont = GOOD_FONT.toFont()
        badFont = BAD_FONT.toFont()
        target = DefaultContainer(
                initialSize = SIZE,
                position = POSITION,
                componentStyles = STYLES,
                wrappers = WRAPPERS,
                initialFont = goodFont)
    }

    @Test
    fun shouldProperlySetPositionsWhenAContainerWithComponentsIsAddedToTheComponentTree() {
        FontLoaderRegistry.setFontLoader(TestFontLoader())
        val terminal = VirtualTerminalBuilder.newBuilder()
                .initialTerminalSize(Size.of(40, 25))
                .font(CP437TilesetResource.REX_PAINT_16X16.toFont())
                .build()
        val screen = ScreenBuilder.createScreenFor(terminal)

        val panel0 = PanelBuilder.newBuilder()
                .wrapWithBox()
                .title("Panel")
                .size(Size.of(32, 16))
                .position(Position.OFFSET_1x1)
                .build()

        val panel1 = PanelBuilder.newBuilder()
                .wrapWithBox()
                .title("Panel2")
                .size(Size.of(16, 10))
                .position(Position.OFFSET_1x1)
                .build()

        val header0 = HeaderBuilder.newBuilder()
                .position(Position.of(1, 0))
                .text("Header")
                .build()

        val header1 = HeaderBuilder.newBuilder()
                .position(Position.of(1, 0))
                .text("Header2")
                .build()


        panel0.addComponent(header0)
        panel1.addComponent(header1)
        panel0.addComponent(panel1)

        screen.addComponent(panel0)

        assertThat(panel0.getPosition()).isEqualTo(Position.of(1, 1))
        assertThat(panel1.getPosition()).isEqualTo(Position.of(3, 3))
        assertThat(header0.getPosition()).isEqualTo(Position.of(3, 2))
        assertThat(header1.getPosition()).isEqualTo(Position.of(5, 4))
    }

    @Test
    fun shouldProperlySetPositionsWhenAContainerIsAddedToTheComponentTreeThenComponentsAreAddedToIt() {
        val terminal = VirtualTerminalBuilder.newBuilder()
                .initialTerminalSize(Size.of(40, 25))
                .font(CP437TilesetResource.REX_PAINT_16X16.toFont())
                .build()
        val screen = ScreenBuilder.createScreenFor(terminal)

        val panel0 = PanelBuilder.newBuilder()
                .wrapWithBox()
                .title("Panel")
                .size(Size.of(32, 16))
                .position(Position.OFFSET_1x1)
                .build()

        val panel1 = PanelBuilder.newBuilder()
                .wrapWithBox()
                .title("Panel2")
                .size(Size.of(16, 10))
                .position(Position.OFFSET_1x1)
                .build()

        val header0 = HeaderBuilder.newBuilder()
                .position(Position.of(1, 0))
                .text("Header")
                .build()

        val header1 = HeaderBuilder.newBuilder()
                .position(Position.of(1, 0))
                .text("Header2")
                .build()


        panel0.addComponent(panel1)
        panel0.addComponent(header0)
        panel1.addComponent(header1)

        screen.addComponent(panel0)

        assertThat(panel0.getPosition()).isEqualTo(Position.of(1, 1))
        assertThat(panel1.getPosition()).isEqualTo(Position.of(3, 3))
        assertThat(header0.getPosition()).isEqualTo(Position.of(3, 2))
        assertThat(header1.getPosition()).isEqualTo(Position.of(5, 4))
    }

    @Test
    fun shouldProperlySetPositionsWhenAComponentIsAddedToAContainerAfterItIsAttachedToTheScreen() {
        val terminal = VirtualTerminalBuilder.newBuilder()
                .initialTerminalSize(Size.of(40, 25))
                .font(CP437TilesetResource.REX_PAINT_16X16.toFont())
                .build()
        val screen = ScreenBuilder.createScreenFor(terminal)

        val panel0 = PanelBuilder.newBuilder()
                .wrapWithBox()
                .title("Panel")
                .size(Size.of(32, 16))
                .position(Position.OFFSET_1x1)
                .build()

        val header0 = HeaderBuilder.newBuilder()
                .position(Position.of(1, 0))
                .text("Header")
                .build()

        screen.addComponent(panel0)

        panel0.addComponent(header0)


        assertThat(panel0.getPosition()).isEqualTo(Position.of(1, 1))
        assertThat(header0.getPosition()).isEqualTo(Position.of(3, 2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionIfComponentWithUnsupportedFontSizeIsAdded() {
        target.addComponent(LabelBuilder.newBuilder()
                .text("foo")
                .font(badFont)
                .build())
    }

    @Test
    fun shouldNotAcceptFocus() {
        assertThat(target.acceptsFocus()).isFalse()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldNotLetToAddAComponentWhichIntersectsWithAnother() {
        val pos = Position.of(1, 1)
        val comp = LabelBuilder.newBuilder().position(pos).text("text").build()
        val otherComp = LabelBuilder.newBuilder().position(pos.withRelativeX(1)).text("text").build()
        target.addComponent(comp)
        target.addComponent(otherComp)
    }

    @Test
    fun shouldSetCurrentFontToAddedComponentWithNoFont() {
        val comp = LabelBuilder.newBuilder().text("foo").build()
        target.addComponent(comp)
        assertThat(comp.getCurrentFont().getId()).isEqualTo(goodFont.getId())
    }

    @Test
    fun shouldNotAcceptGivenFocus() {
        assertThat(target.giveFocus()).isFalse()
    }

    @Test
    fun shouldProperlyRemoveComponentFromSelf() {
        val comp = LabelBuilder.newBuilder()
                .text("xLength")
                .position(Position.DEFAULT_POSITION)
                .build()
        target.addComponent(comp)
        val removalHappened = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentRemoval, {
            removalHappened.set(true)
        })

        assertThat(target.removeComponent(comp)).isTrue()
        assertThat(removalHappened.get()).isTrue()
    }

    @Test
    fun shouldProperlyRemoveComponentFromChild() {
        val comp = LabelBuilder.newBuilder()
                .text("xLength")
                .position(Position.DEFAULT_POSITION)
                .build()
        val panel = PanelBuilder.newBuilder()
                .size(SIZE - Size.ONE)
                .position(Position.DEFAULT_POSITION).build()
        panel.addComponent(comp)
        target.addComponent(panel)
        val removalHappened = AtomicBoolean(false)
        EventBus.subscribe(EventType.ComponentRemoval, {
            removalHappened.set(true)
        })

        assertThat(target.removeComponent(comp)).isTrue()
        assertThat(removalHappened.get()).isTrue()
    }

    companion object {
        val GOOD_FONT = CP437TilesetResource.AESOMATICA_16X16
        val BAD_FONT = CP437TilesetResource.BISASAM_20X20
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
