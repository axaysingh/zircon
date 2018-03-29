package org.codetome.zircon.examples;

import org.codetome.zircon.api.Position;
import org.codetome.zircon.api.Size;
import org.codetome.zircon.api.builder.AnimationBuilder;
import org.codetome.zircon.api.builder.ScreenBuilder;
import org.codetome.zircon.api.component.Panel;
import org.codetome.zircon.api.component.builder.LabelBuilder;
import org.codetome.zircon.api.component.builder.PanelBuilder;
import org.codetome.zircon.api.animation.Animation;
import org.codetome.zircon.api.animation.AnimationHandler;
import org.codetome.zircon.api.animation.AnimationResource;
import org.codetome.zircon.api.screen.Screen;
import org.codetome.zircon.api.terminal.Terminal;

import static org.codetome.zircon.api.resource.CP437TilesetResource.*;

public class AnimationExample {

    private static final Size TERMINAL_SIZE = Size.of(50, 30);
    private static final Position LEFT_POS = Position.of(8, 5);
    private static final Position RIGHT_POS = Position.of(29, 5);

    public static void main(String[] args) {
        final Terminal terminal = TerminalUtils.fetchTerminalBuilder(args)
                .font(WANDERLUST_16X16.toFont())
                .initialTerminalSize(TERMINAL_SIZE)
                .build();
        final Screen screen = ScreenBuilder.createScreenFor(terminal);
        screen.setCursorVisibility(false);

        final Panel panel = PanelBuilder.newBuilder()
                .wrapWithBox()
                .title("Animation example")
                .size(TERMINAL_SIZE)
                .build();

        panel.addComponent(LabelBuilder.newBuilder()
                .text("Looped:")
                .position(LEFT_POS.withRelativeY(-3).withRelativeX(-1))
                .build());
        panel.addComponent(LabelBuilder.newBuilder()
                .text("Non-looped:")
                .position(RIGHT_POS.withRelativeY(-3).withRelativeX(-1))
                .build());
        screen.addComponent(panel);

        screen.display();


        AnimationBuilder first = AnimationResource.loadAnimationFromStream(AnimationExample.class.getResourceAsStream("/animations/skull.zap"));
        AnimationBuilder second = first.createCopy();
        first.loopCount(0);
        second.loopCount(1);
        for (int i = 0; i < first.getLength(); i++) {
            first.addPosition(LEFT_POS);
            second.addPosition(RIGHT_POS);
        }
        Animation leftAnim = first.build();
        Animation rightAnim = second.build();

        final AnimationHandler animationHandler = new AnimationHandler(screen);
        animationHandler.addAnimation(leftAnim);
        animationHandler.addAnimation(rightAnim);

    }

}
