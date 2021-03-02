package dev.overtow.core;

import dev.overtow.graphics.hud.Circle;
import dev.overtow.graphics.hud.Rectangle;
import dev.overtow.graphics.hud.Text;
import dev.overtow.graphics.hud.TextAlign;
import dev.overtow.service.memory.MemoryManager;
import dev.overtow.core.Window;
import dev.overtow.util.injection.Bind;
import org.joml.Vector2f;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Hud {

    private static final String FONT_NAME = "BOLD";
    public static final Color UPPER_RIBBON_COLOR = new Color(0x23, 0xa1, 0xf1, 200);
    public static final Color LOWER_RIBBON_COLOR = new Color(0xc1, 0xe3, 0xf9, 200);
    public static final Color CIRCLE_COLOR = new Color(0xc1, 0xe3, 0xf9, 200);
    public static final Color TEXT_HOVERED_COLOR = new Color(0x00, 0x00, 0x00, 255);
    public static final Color TEXT_COLOR = new Color(0x23, 0xa1, 0xf1, 255);
    public static final Color CLOCK_TEXT_COLOR = new Color(0xe6, 0xea, 0xed, 255);

    // data should be freed by byte buffer creator
    private static final int DONT_FREE_DATA = 0;

    private final long contextHandler;

    private final Window window;

    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private int counter;
    private final int fontHandler;
    private final ByteBuffer fontBuffer;

    private final Rectangle upperRibbon;
    private final Rectangle lowerRibbon;
    private final Circle circle;
    private final Text clicksNumberText;
    private final Text clockText;

    public Hud(MemoryManager memoryManager, Window window) {
        this.window = window;

        this.contextHandler = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.contextHandler == NULL) {
            throw new RuntimeException("Could not init nanoVg");
        }

        try {
            fontBuffer = memoryManager.readFromFile("data/fonts/OpenSans-Bold.ttf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fontHandler = nvgCreateFontMem(contextHandler, FONT_NAME, fontBuffer, DONT_FREE_DATA);
        if (fontHandler == -1) {
            throw new RuntimeException("Could not add font");
        }
        counter = 0;
        upperRibbon = new Rectangle(
                new Vector2f(0, window.getHeight() - 100),
                new Vector2f(window.getWidth(), 50),
                UPPER_RIBBON_COLOR);
        lowerRibbon = new Rectangle(
                new Vector2f(0, window.getHeight() - 50),
                new Vector2f(window.getWidth(), 10),
                LOWER_RIBBON_COLOR);
        circle = new Circle(
                new Vector2f(50, window.getHeight() - 75),
                20,
                CIRCLE_COLOR);
        clicksNumberText = new Text(
                new Vector2f(50, window.getHeight() - 87),
                "???",
                25, FONT_NAME, TEXT_COLOR, TextAlign.CENTER, TextAlign.TOP);
        clockText = new Text(
                new Vector2f(window.getWidth() - 150, window.getHeight() - 95),
                "???",
                40, FONT_NAME, CLOCK_TEXT_COLOR, TextAlign.LEFT, TextAlign.TOP);
    }

    public void render() {
        nvgBeginFrame(contextHandler, window.getWidth(), window.getHeight(), 1);

        upperRibbon.draw(contextHandler);
        lowerRibbon.draw(contextHandler);
        circle.draw(contextHandler);

        if (circle.isHovered(window.getMousePosition())) {
            clicksNumberText.setColor(TEXT_HOVERED_COLOR);
        } else {
            clicksNumberText.setColor(TEXT_COLOR);
        }
        clicksNumberText.setText(String.format("%02dasdфыв", counter));
        clicksNumberText.draw(contextHandler);

        clockText.setText(dateFormat.format(new Date()));
        clockText.draw(contextHandler);

        nvgEndFrame(contextHandler);
        window.restoreState();
    }

    public void incCounter() {
        counter = (counter + 1) % 100;
    }

    public void cleanup() {
        nvgDelete(contextHandler);
    }
}
