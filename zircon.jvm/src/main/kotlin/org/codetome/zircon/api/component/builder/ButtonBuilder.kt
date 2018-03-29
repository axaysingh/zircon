package org.codetome.zircon.api.component.builder

import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.Builder
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.component.Button
import org.codetome.zircon.api.component.ComponentStyles
import org.codetome.zircon.api.font.Font
import org.codetome.zircon.api.graphics.Layer
import org.codetome.zircon.internal.component.WrappingStrategy
import org.codetome.zircon.internal.component.impl.wrapping.ButtonWrappingStrategy
import org.codetome.zircon.internal.component.impl.DefaultButton
import org.codetome.zircon.internal.font.impl.FontSettings
import java.awt.image.BufferedImage
import java.util.*

data class ButtonBuilder(
        private var font: Font = FontSettings.NO_FONT,
        private var text: String = "",
        private var position: Position = Position.DEFAULT_POSITION,
        private var componentStyles: ComponentStyles = ComponentStylesBuilder.DEFAULT) : Builder<Button> {

    /**
     * Sets the [Font] to use with the resulting [Layer].
     */
    fun font(font: Font) = also {
        this.font = font
    }

    fun text(text: String) = also {
        this.text = text
    }

    fun position(position: Position) = also {
        this.position = position
    }

    fun componentStyles(componentStyles: ComponentStyles) = also {
        this.componentStyles = componentStyles
    }

    override fun build(): Button {
        require(text.isNotBlank()) {
            "A Button can't be blank!"
        }
        val wrappers = LinkedList<WrappingStrategy>()
        wrappers.add(ButtonWrappingStrategy())
        return DefaultButton(
                text = text,
                initialSize = Size.of(text.length + 2, 1),
                position = position,
                componentStyles = componentStyles,
                wrappers = wrappers,
                initialFont = font)
    }

    override fun createCopy() = copy()

    companion object {

        @JvmStatic
        fun newBuilder() = ButtonBuilder()
    }
}