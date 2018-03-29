package org.codetome.zircon.examples;

import org.codetome.zircon.api.*;
import org.codetome.zircon.api.builder.*;
import org.codetome.zircon.api.color.ANSITextColor;
import org.codetome.zircon.api.color.TextColor;
import org.codetome.zircon.api.color.TextColorFactory;
import org.codetome.zircon.api.font.Font;
import org.codetome.zircon.api.graphics.Layer;
import org.codetome.zircon.api.screen.Screen;
import org.codetome.zircon.api.terminal.Terminal;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

import static org.codetome.zircon.api.resource.CP437TilesetResource.WANDERLUST_16X16;

public class TilesetExample {

    private static final int RANDOM_CHAR_COUNT = 50;
    private static final char[] RANDOM_CHARS =
            new char[]
                    {
                            Symbols.FACE_BLACK,
                            Symbols.FACE_WHITE,
                            Symbols.CLUB,
                            Symbols.TRIANGLE_UP_POINTING_BLACK,
                            Symbols.SPADES,
                            Symbols.BULLET,
                            Symbols.DIAMOND
                    };

    private static final int TERMINAL_WIDTH = 40;
    private static final int TERMINAL_HEIGHT = 40;
    private static final Size SIZE = Size.of(TERMINAL_WIDTH, TERMINAL_HEIGHT);
    private static final TextCharacter GRASS_0 = TextCharacterBuilder.newBuilder()
            .character(',')
            .foregroundColor(TextColorFactory.fromString("#33cc44"))
            .backgroundColor(TextColorFactory.fromString("#114911"))
            .build();
    private static final TextCharacter GRASS_1 = TextCharacterBuilder.newBuilder()
            .character('`')
            .foregroundColor(TextColorFactory.fromString("#33bb44"))
            .backgroundColor(TextColorFactory.fromString("#114511"))
            .build();
    private static final TextCharacter GRASS_2 = TextCharacterBuilder.newBuilder()
            .character('\'')
            .foregroundColor(TextColorFactory.fromString("#33aa44"))
            .backgroundColor(TextColorFactory.fromString("#114011"))
            .build();
    private static final TextCharacter[] GRASSES = new TextCharacter[]{GRASS_0, GRASS_1, GRASS_2};
    private static final TextColor TEXT_COLOR = TextColorFactory.fromString("#dd6644");
    private static final TextColor TEXT_BG_COLOR = TextColorFactory.fromString("#00ff00");

    public static void main(String[] args) {
        final Terminal terminal = TerminalUtils.fetchTerminalBuilder(args)
                .font(WANDERLUST_16X16.toFont())
                .initialTerminalSize(SIZE)
                .build();
        final Screen screen = ScreenBuilder.createScreenFor(terminal);
        screen.setCursorVisibility(false);

        final Random random = new Random();
        for (int y = 0; y < TERMINAL_HEIGHT; y++) {
            for (int x = 0; x < TERMINAL_WIDTH; x++) {
                screen.setCharacterAt(Position.of(x, y), GRASSES[random.nextInt(3)]);
            }
        }
        final String text = "Tileset Example";
        for (int i = 0; i < text.length(); i++) {
            screen.setCharacterAt(Position.of(i + 2, 1),
                    TextCharacterBuilder.newBuilder()
                            .character(text.charAt(i))
                            .foregroundColor(TEXT_COLOR)
                            .backgroundColor(TEXT_BG_COLOR)
                            .build());
        }

        final int charCount = RANDOM_CHARS.length;
        final int ansiCount = ANSITextColor.values().length;

        final Layer overlay = new LayerBuilder()
                .size(screen.getBoundableSize())
                .filler(TextCharacterBuilder.EMPTY
                        .withBackgroundColor(TextColorFactory.fromRGB(0, 0, 0, 50)))
                .build();

        for (int i = 0; i < RANDOM_CHAR_COUNT; i++) {
            overlay.setCharacterAt(
                    Position.of(
                            random.nextInt(TERMINAL_WIDTH),
                            random.nextInt(TERMINAL_HEIGHT - 2) + 2),
                    TextCharacterBuilder.newBuilder()
                            .character(RANDOM_CHARS[random.nextInt(charCount)])
                            .foregroundColor(ANSITextColor.values()[random.nextInt(ansiCount)])
                            .backgroundColor(TextColorFactory.TRANSPARENT)
                            .build());
        }
        screen.pushLayer(overlay);
        screen.display();
    }
}
