package org.codetome.zircon.examples.interactive

import org.codetome.zircon.api.*
import org.codetome.zircon.api.builder.*
import org.codetome.zircon.api.color.ANSITextColor
import org.codetome.zircon.api.color.TextColorFactory
import org.codetome.zircon.api.component.builder.GameComponentBuilder
import org.codetome.zircon.api.component.builder.PanelBuilder
import org.codetome.zircon.api.game.GameModifiers
import org.codetome.zircon.api.game.Position3D
import org.codetome.zircon.api.game.ProjectionMode
import org.codetome.zircon.api.game.Size3D
import org.codetome.zircon.api.input.InputType
import org.codetome.zircon.api.resource.CP437TilesetResource
import org.codetome.zircon.api.resource.ColorThemeResource
import org.codetome.zircon.api.screen.Screen
import org.codetome.zircon.examples.TerminalUtils
import org.codetome.zircon.internal.component.impl.DefaultGameComponent
import org.codetome.zircon.internal.game.InMemoryGameArea
import org.codetome.zircon.internal.graphics.BoxType
import java.awt.Toolkit
import java.util.*
import java.util.function.Consumer

object IsometricGameArea {

    private val FONT = CP437TilesetResource.REX_PAINT_20X20

    private val WALL_COLOR = TextColorFactory.fromString("#333333")
    private val WALL_DECOR_COLOR = TextColorFactory.fromString("#444444")


    private val FRONT_WALL_COLOR = TextColorFactory.fromString("#555555")
    private val FRONT_WALL_DECOR_COLOR = TextColorFactory.fromString("#666666")

    private val ROOF_COLOR = TextColorFactory.fromString("#666666")
    private val ROOF_DECOR_COLOR = TextColorFactory.fromString("#4e4e4e")

    private val INTERIOR_COLOR = TextColorFactory.fromString("#999999")
    private val INTERIOR_DECOR_COLOR = TextColorFactory.fromString("#aaaaaa")

    private val FRONT_WALL = TextCharacterBuilder.newBuilder()
            .backgroundColor(FRONT_WALL_COLOR)
            .foregroundColor(FRONT_WALL_DECOR_COLOR)
            .modifiers(GameModifiers.BLOCK_FRONT)
            .character('-')
            .build()

    private val BACK_WALL = TextCharacterBuilder.newBuilder()
            .backgroundColor(FRONT_WALL_COLOR)
            .foregroundColor(FRONT_WALL_DECOR_COLOR)
            .modifiers(GameModifiers.BLOCK_FRONT)
            .character('-')
            .build()

    private val WALL = TextCharacterBuilder.newBuilder()
            .backgroundColor(WALL_COLOR)
            .foregroundColor(WALL_DECOR_COLOR)
            .modifiers(GameModifiers.BLOCK_TOP)
            .character('#')
            .build()

    private val ROOF = TextCharacterBuilder.newBuilder()
            .backgroundColor(ROOF_COLOR)
            .foregroundColor(ROOF_DECOR_COLOR)
            .modifiers(GameModifiers.BLOCK_TOP)
            .build()

    private val ANTENNA = TextCharacterBuilder.newBuilder()
            .backgroundColor(WALL_COLOR)
            .foregroundColor(WALL_DECOR_COLOR)
            .modifiers(GameModifiers.BLOCK_FRONT)
            .character('=')
            .build()

    private val INTERIOR = TextCharacterBuilder.newBuilder()
            .backgroundColor(INTERIOR_COLOR)
            .foregroundColor(INTERIOR_DECOR_COLOR)
            .character(Symbols.BLOCK_SPARSE)
            .modifiers(GameModifiers.BLOCK_BOTTOM)
            .build()

    private val GUY = TextCharacterBuilder.newBuilder()
            .backgroundColor(TextColorFactory.TRANSPARENT)
            .foregroundColor(ANSITextColor.RED)
            .character(Symbols.FACE_BLACK)
            .build()

    private val EXIT_CONDITIONS = listOf(InputType.Escape, InputType.EOF)

    val random = Random(123423432)

    @JvmStatic
    fun main(args: Array<String>) {

        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val x = screenSize.getWidth() / FONT.width
        val y = screenSize.getHeight() / FONT.height
        val terminal = TerminalUtils.fetchTerminalBuilder(args)
                .font(FONT.toFont())
                .initialTerminalSize(Size.of(x.toInt(), y.toInt()))
                .fullScreen()
                .build()
        val screen = ScreenBuilder.createScreenFor(terminal)
        screen.setCursorVisibility(false) // we don't want the cursor right now


        val gamePanel = PanelBuilder.newBuilder()
                .size(screen.getBoundableSize())
                .title("Game area")
                .wrapWithBox()
                .boxType(BoxType.TOP_BOTTOM_DOUBLE)
                .build()

        val visibleGameAreaSize = Size3D.from2DSize(gamePanel.getBoundableSize()
                .minus(Size.of(2, 2)), 8)

        val virtualSize = Size3D.of(100, 100, 30)

        val gameArea = InMemoryGameArea(
                virtualSize,
                1,
                TextCharacterBuilder.EMPTY)

        val gameComponent = GameComponentBuilder.newBuilder()
                .gameArea(gameArea)
                .projectionMode(ProjectionMode.ISOMETRIC)
                .visibleSize(visibleGameAreaSize)
                .build()

        screen.addComponent(gamePanel)
        gamePanel.addComponent(gameComponent)

        enableMovement(screen, gameComponent)

        val buildingCount = random.nextInt(5) + 3

        val pos = Position3D.of(15, random.nextInt(20) + 20, 0)

        (0 until buildingCount).forEach { idx ->

            val width = random.nextInt(5) + 5
            val depth = random.nextInt(5) + 5
            val height = random.nextInt(5) + 5

            generateBuilding(size = Size3D.of(width, depth, height),
                    offset = pos.withRelativeX(random.nextInt(5) + 10 * idx).withRelativeY(random.nextInt(10)),
                    gameArea = gameArea,
                    repeat = 2)
        }

        screen.applyColorTheme(ColorThemeResource.SOLARIZED_DARK_CYAN.getTheme())
        screen.display()
    }

    private tailrec fun generateBuilding(size: Size3D, offset: Position3D, gameArea: InMemoryGameArea, repeat: Int) {
        (0 until size.zLength).forEach { z ->
            (0 until size.yLength).forEach { y ->
                (0 until size.xLength).forEach { x ->
                    val pos = Position3D.from2DPosition(Position.of(x, y).plus(offset.to2DPosition()), z + offset.z)
                    val chars = if (y == size.yLength - 1) {
                        mutableListOf(if (size.xLength < 3 || size.yLength < 3) {
                            ANTENNA
                        } else if (random.nextInt(5) < 1) {
                            FRONT_WALL.withForegroundColor(TextColorFactory.fromString("#ffff00"))
                        } else {
                            FRONT_WALL
                        }, WALL)
                    } else if (x == 0 || x == size.xLength - 1) {
                        mutableListOf(WALL)
                    } else if (y == 0) {
                        mutableListOf(BACK_WALL, WALL)
                    } else {
                        val chars = mutableListOf(INTERIOR)
                        val extra = random.nextInt(10)
                        if (extra == 0 || extra == 1) {
                            chars.add(GUY.withForegroundColor(ANSITextColor.values()[random.nextInt(ANSITextColor.values().size)]))
                        }
                        if (extra == 2) {
                            chars.add(TextCharacterBuilder.newBuilder()
                                    .foregroundColor(TextColorFactory.fromString("#333333"))
                                    .backgroundColor(TextColorFactory.TRANSPARENT)
                                    .character(Symbols.SINGLE_LINE_T_DOUBLE_DOWN)
                                    .build())
                        }
                        chars
                    }
                    if (z == size.zLength - 1) {
                        chars.remove(WALL)
                        if (repeat == 0 || x == 0 || x == size.xLength - 1 || y == 0 || y == size.yLength - 1) {
                            chars.add(ROOF)
                        }
                    }
                    gameArea.setBlockAt(pos, chars)
                }
            }
        }
        if (repeat > 0) {
            generateBuilding(
                    size = Size3D.of(size.xLength - 2, size.yLength - 2, size.zLength),
                    offset = offset.withRelativeZ(size.zLength).withRelativeX(1).withRelativeY(1),
                    gameArea = gameArea,
                    repeat = repeat - 1)
        }
    }

    private fun enableMovement(screen: Screen, gameComponent: DefaultGameComponent) {
        screen.onInput(Consumer { input ->
            if (EXIT_CONDITIONS.contains(input.getInputType())) {
                System.exit(0)
            } else {
                if (InputType.ArrowUp === input.getInputType()) {
                    gameComponent.scrollOneBackward()
                }
                if (InputType.ArrowDown === input.getInputType()) {
                    gameComponent.scrollOneForward()
                }
                if (InputType.ArrowLeft === input.getInputType()) {
                    gameComponent.scrollOneLeft()
                }
                if (InputType.ArrowRight === input.getInputType()) {
                    gameComponent.scrollOneRight()
                }
                if (InputType.PageUp === input.getInputType()) {
                    gameComponent.scrollOneUp()
                }
                if (InputType.PageDown === input.getInputType()) {
                    gameComponent.scrollOneDown()
                }
                screen.drainLayers()
                val (x, y, z) = gameComponent.getVisibleOffset()
                screen.pushLayer(LayerBuilder.newBuilder()
                        .textImage(TextCharacterStringBuilder.newBuilder()
                                .backgroundColor(TextColorFactory.TRANSPARENT)
                                .foregroundColor(TextColorFactory.fromString("#aaaadd"))
                                .text(String.format("Position: (x=%s, y=%s, z=%s)", x, y, z))
                                .build()
                                .toTextImage())
                        .offset(Position.of(21, 1))
                        .build())
                screen.refresh()
            }
        })
    }
}
