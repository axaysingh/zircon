package org.codetome.zircon.api.component.builder

import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.Builder
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.component.ComponentStyles
import org.codetome.zircon.api.component.Panel
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.graphics.Layer
import org.codetome.zircon.api.modifier.Border
import org.codetome.zircon.internal.component.WrappingStrategy
import org.codetome.zircon.internal.component.impl.DefaultPanel
import org.codetome.zircon.internal.component.impl.wrapping.BorderWrappingStrategy
import org.codetome.zircon.internal.component.impl.wrapping.BoxWrappingStrategy
import org.codetome.zircon.internal.component.impl.wrapping.ShadowWrappingStrategy
import org.codetome.zircon.internal.font.impl.FontSettings
import org.codetome.zircon.internal.graphics.BoxType
import java.util.*

data class PanelBuilder(private var font: Font = FontSettings.NO_FONT,
                        private var boxType: BoxType = BoxType.SINGLE,
                        private var title: String = "",
                        private var position: Position = Position.DEFAULT_POSITION,
                        private var componentStyles: ComponentStyles = ComponentStylesBuilder.DEFAULT,
                        private var size: Size = Size.UNKNOWN,
                        private var drawBox: Boolean = false,
                        private var drawShadow: Boolean = false,
                        private var border: Optional<Border> = Optional.empty()) : Builder<Panel> {

    /**
     * Sets the [Font] to use with the resulting [Layer].
     */
    fun font(font: Font) = also {
        this.font = font
    }

    fun wrapWithBox() = also {
        drawBox = true
    }

    fun wrapWithShadow() = also {
        drawShadow = true
    }

    fun addBorder(border: Border) = also {
        this.border = Optional.of(border)
    }

    fun boxType(boxType: BoxType) = also {
        this.boxType = boxType
    }

    fun size(size: Size) = also {
        this.size = size
    }

    fun title(title: String) = also {
        this.title = title
    }

    fun position(position: Position) = also {
        this.position = position
    }

    fun componentStyles(componentStyles: ComponentStyles) = also {
        this.componentStyles = componentStyles
    }

    override fun build(): Panel {
        require(size != Size.UNKNOWN) {
            "You must set a size for a Panel!"
        }
        val wrappers = mutableListOf<WrappingStrategy>()
        if (drawBox) {
            wrappers.add(BoxWrappingStrategy(
                    boxType = boxType,
                    title = if (title.isNotBlank()) Optional.of(title) else Optional.empty()))
        }
        if (border.isPresent) {
            wrappers.add(BorderWrappingStrategy(border.get()))
        }
        if (drawShadow) {
            wrappers.add(ShadowWrappingStrategy())
        }
        return DefaultPanel(
                title = title,
                initialSize = size,
                position = position,
                componentStyles = componentStyles,
                wrappers = wrappers,
                initialFont = font)
    }

    override fun createCopy() = this.copy()

    companion object {

        @JvmStatic
        fun newBuilder() = PanelBuilder()
    }
}