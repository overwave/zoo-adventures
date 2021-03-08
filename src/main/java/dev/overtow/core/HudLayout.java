package dev.overtow.core;

import dev.overtow.graphics.hud.Circle;
import dev.overtow.graphics.hud.HudElement;
import dev.overtow.graphics.hud.Rectangle;
import dev.overtow.graphics.hud.Text;
import dev.overtow.graphics.hud.TextAlign;
import dev.overtow.service.window.Window;
import dev.overtow.util.injection.Injector;
import org.joml.Vector2f;

import java.awt.Color;
import java.util.List;

public class HudLayout {

    public static final Color UPPER_RIBBON_COLOR = new Color(70, 220, 44, 226);
    public static final Color LOWER_RIBBON_COLOR = new Color(0xc1, 0xe3, 0xf9, 200);
    public static final Color CIRCLE_COLOR = new Color(0xc1, 0xe3, 0xf9, 200);
    public static final Color TEXT_HOVERED_COLOR = new Color(0x00, 0x00, 0x00, 255);
    public static final Color TEXT_COLOR = new Color(0x23, 0xa1, 0xf1, 255);
    public static final Color SCORE_TEXT_COLOR = new Color(35, 50, 80, 255);

//    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final Window window;

    private final Rectangle upperRibbon;
    //    private final Rectangle lowerRibbon;
    private final Circle circle;
//    private final Text clicksNumberText;
    private final Text scoreText;

    public HudLayout() {
        this.window = Injector.getInstance(Window.class);

        upperRibbon = new Rectangle(
                new Vector2f(0, window.getHeight() / 2f - 100),
                new Vector2f(250, 90),
                UPPER_RIBBON_COLOR);
//        lowerRibbon = new Rectangle(
//                new Vector2f(0, window.getHeight() - 50),
//                new Vector2f(window.getWidth(), 10),
//                LOWER_RIBBON_COLOR);
        circle = new Circle(
                new Vector2f(50, window.getHeight() - 75),
                20,
                CIRCLE_COLOR);
//        clicksNumberText = new Text(
//                new Vector2f(50, window.getHeight() - 87),
//                "???",
//                25, Font.OPEN_SANS_BOLD, TEXT_COLOR, TextAlign.CENTER, TextAlign.TOP);
        scoreText = new Text(
                new Vector2f(50, window.getHeight() / 2f - 80),
                "???",
                40, Font.OPEN_SANS_REGULAR, SCORE_TEXT_COLOR, TextAlign.LEFT, TextAlign.TOP);
    }

    public void update() {
        scoreText.setText("Счёт: " + System.currentTimeMillis() / 1000 % 1000);
    }

    public List<HudElement> getHudElements() {
        return List.of(upperRibbon/*, lowerRibbon*/, circle/*, clicksNumberText*/, scoreText);
    }
}
