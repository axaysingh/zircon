package org.codetome.zircon.examples.interactive;

import org.codetome.zircon.api.Position;
import org.codetome.zircon.api.Size;
import org.codetome.zircon.api.TextCharacter;
import org.codetome.zircon.api.builder.DeviceConfigurationBuilder;
import org.codetome.zircon.api.builder.ScreenBuilder;
import org.codetome.zircon.api.builder.TerminalBuilder;
import org.codetome.zircon.api.builder.TextCharacterBuilder;
import org.codetome.zircon.api.color.TextColorFactory;
import org.codetome.zircon.api.input.InputType;
import org.codetome.zircon.api.input.KeyStroke;
import org.codetome.zircon.api.resource.CP437TilesetResource;
import org.codetome.zircon.api.screen.Screen;
import org.codetome.zircon.api.terminal.Terminal;
import org.codetome.zircon.examples.TerminalUtils;

import java.util.ArrayList;
import java.util.List;

import static org.codetome.zircon.api.color.ANSITextColor.BLACK;
import static org.codetome.zircon.api.color.ANSITextColor.RED;
import static org.codetome.zircon.api.input.InputType.Enter;

public class TypingExample {

    private static final int TERMINAL_WIDTH = 40;

    private static final List<InputType> EXIT_CONDITIONS = new ArrayList<>();
    private static final TextCharacter TEXT_CHAR_TEMPLATE = TextCharacterBuilder.newBuilder()
            .foregroundColor(TextColorFactory.fromString("#F7923A"))
            .backgroundColor(BLACK)
            .build();

    private static boolean headless = false;

    static {
        EXIT_CONDITIONS.add(InputType.Escape);
        EXIT_CONDITIONS.add(InputType.EOF);
    }

    public static void main(String[] args) {
        if(args.length > 0) {
            headless = true;
        }
        TerminalBuilder builder = TerminalUtils.fetchTerminalBuilder(args)
                .initialTerminalSize(Size.of(TERMINAL_WIDTH, 10))
                .font(CP437TilesetResource.ADU_DHABI_16X16.toFont())
                .deviceConfiguration(DeviceConfigurationBuilder.newBuilder()
                        .cursorBlinking(true)
                        .build());
        final Terminal terminal = builder.build();
        final Screen screen = ScreenBuilder.createScreenFor(terminal);

        startTypingSupportForScreen(screen);
//        startTypingSupportForTerminal(terminal);
    }

    private static void startTypingSupportForScreen(Screen screen) {
        screen.onInput((input) -> {
            final Position pos = screen.getCursorPosition();
            if (EXIT_CONDITIONS.contains(input.getInputType()) && !headless) {
                System.exit(0);
            } else if (input.inputTypeIs(Enter)) {
                screen.putCursorAt(pos.withRelativeY(1).withX(0));
                screen.refresh();
            } else {
                if (input.isKeyStroke()) {
                    final KeyStroke ks = input.asKeyStroke();
                    screen.setCharacterAt(pos, TEXT_CHAR_TEMPLATE.withCharacter(ks.getCharacter()));
                    screen.moveCursorForward();
                    screen.refresh();
                }
            }
        });
    }

    private static void startTypingSupportForTerminal(Terminal terminal) {
        terminal.onInput((input) -> {
            final Position pos = terminal.getCursorPosition();
            if (EXIT_CONDITIONS.contains(input.getInputType()) && !headless) {
                System.exit(0);
            } else if (input.inputTypeIs(Enter)) {
                terminal.putCursorAt(pos.withRelativeY(1).withX(0));
                terminal.flush();
            } else {
                if (input.isKeyStroke()) {
                    final KeyStroke ks = input.asKeyStroke();
                    terminal.setBackgroundColor(BLACK);
                    terminal.setForegroundColor(RED);
                    terminal.putCharacter(ks.getCharacter());
                    terminal.resetColorsAndModifiers();
                    terminal.flush();
                }
            }
        });
    }
}
