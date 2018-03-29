package org.codetome.zircon.playground

import org.codetome.zircon.api.*
import org.codetome.zircon.api.builder.GameAreaBuilder
import org.codetome.zircon.api.builder.TextCharacterBuilder
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.color.TextColorFactory
import org.codetome.zircon.api.component.GameComponent
import org.codetome.zircon.api.component.builder.GameComponentBuilder
import org.codetome.zircon.api.game.*
import org.codetome.zircon.api.resource.CP437TilesetResource

object Playground {

    val TERMINAL_SIZE = Size.of(30, 20)

    @JvmStatic
    fun main(args: Array<String>) {

        val gameScreen = SwingTerminalBuilder.newBuilder()
                .font(CP437TilesetResource.REX_PAINT_16X16.toFont())
                .initialTerminalSize(TERMINAL_SIZE)
                .buildScreen()

        val componentSize = Size3D.from2DSize(TERMINAL_SIZE, 5)

        val gameArea: GameArea = GameAreaBuilder.newBuilder()
                .size(componentSize)
                .layersPerBlock(1)
                .build()
        val gameComponent: GameComponent = GameComponentBuilder.newBuilder()
                .projectionMode(ProjectionMode.ISOMETRIC) // you need to set the projection mode (default is TOP_DOWN)
                .gameArea(gameArea)
                .visibleSize(componentSize) // you need to set a visible size for the component (default is 1x1)
                .build()
        val WALL: TextCharacter = TextCharacterBuilder.newBuilder()
                .character(Symbols.BLOCK_SOLID)
                .modifiers(GameModifiers.BLOCK_FRONT)
                .foregroundColor(ANSITextColor.BLUE)
                .build()
        val TOP: TextCharacter = TextCharacterBuilder.newBuilder()
                .character('^')
                .modifiers(GameModifiers.BLOCK_TOP)
                .foregroundColor(ANSITextColor.CYAN)
                .backgroundColor(TextColorFactory.fromString("#666666")) // setting a bgcolor helps
                .build()
        val block: MutableList<TextCharacter> = mutableListOf(WALL, TOP)


        gameScreen.addComponent(gameComponent)
        gameArea.setBlockAt(Position3D.of(3, 5, 0), block)
        gameScreen.display()
    }
}
