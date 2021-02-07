package dev.overtow.service;

import dev.overtow.util.injection.Bind;
import org.joml.Vector2f;
import org.lwjglb.engine.Utils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Bind
public class HudImpl implements Hud {

    private static final String FONT_NAME = "BOLD";
    public static final Color UPPER_RIBBON_COLOR = new Color(0x23, 0xa1, 0xf1, 200);
    public static final Color LOWER_RIBBON_COLOR = new Color(0xc1, 0xe3, 0xf9, 200);
    public static final Color CIRCLE_COLOR = new Color(0xc1, 0xe3, 0xf9, 200);
    public static final Color TEXT_HOVERED_COLOR = new Color(0x00, 0x00, 0x00, 255);
    public static final Color TEXT_COLOR = new Color(0x23, 0xa1, 0xf1, 255);
    public static final Color CLOCK_TEXT_COLOR = new Color(0xe6, 0xea, 0xed, 255);

    private final long contextHandler;

    private final MemoryManager memoryManager;
    private final Window window;

    private ByteBuffer fontBuffer;

    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private int counter;

    public HudImpl(MemoryManager memoryManager, Window window) throws Exception {
        this.memoryManager = memoryManager;
        this.window = window;

        this.contextHandler = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.contextHandler == NULL) {
            throw new Exception("Could not init nanoVg");
        }

        fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
        int font = nvgCreateFontMem(contextHandler, FONT_NAME, fontBuffer, 0);
        if (font == -1) {
            throw new Exception("Could not add font");
        }
        counter = 0;
    }

    public void render() {
        nvgBeginFrame(contextHandler, window.getWidth(), window.getHeight(), 1);

        // Upper ribbon
        nvgBeginPath(contextHandler);
        nvgRect(contextHandler, 0, window.getHeight() - 100, window.getWidth(), 50);
        memoryManager.doForColor(UPPER_RIBBON_COLOR, color -> nvgFillColor(contextHandler, color));
        nvgFill(contextHandler);

        // Lower ribbon
        nvgBeginPath(contextHandler);
        nvgRect(contextHandler, 0, window.getHeight() - 50, window.getWidth(), 10);
        memoryManager.doForColor(LOWER_RIBBON_COLOR, color -> nvgFillColor(contextHandler, color));
        nvgFill(contextHandler);

        Vector2f position = window.getMousePosition();
        int xcenter = 50;
        int ycenter = window.getHeight() - 75;
        int radius = 20;
        boolean hover = Math.pow(position.x() - xcenter, 2) + Math.pow(position.y() - ycenter, 2) < Math.pow(radius, 2);

        // Circle
        nvgBeginPath(contextHandler);
        nvgCircle(contextHandler, xcenter, ycenter, radius);
        memoryManager.doForColor(CIRCLE_COLOR, color -> nvgFillColor(contextHandler, color));
        nvgFill(contextHandler);

        // Clicks Text
        nvgFontSize(contextHandler, 25.0f);
        nvgFontFace(contextHandler, FONT_NAME);
        nvgTextAlign(contextHandler, NVG_ALIGN_CENTER | NVG_ALIGN_TOP);
        if (hover) {
            memoryManager.doForColor(TEXT_HOVERED_COLOR, color -> nvgFillColor(contextHandler, color));
        } else {
            memoryManager.doForColor(TEXT_COLOR, color -> nvgFillColor(contextHandler, color));
        }
        nvgText(contextHandler, 50, window.getHeight() - 87, String.format("%02dasdфыв結婚記念日", counter));

        // Render hour text
        nvgFontSize(contextHandler, 40.0f);
        nvgFontFace(contextHandler, FONT_NAME);
        nvgTextAlign(contextHandler, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        memoryManager.doForColor(CLOCK_TEXT_COLOR, color -> nvgFillColor(contextHandler, color));
        nvgText(contextHandler, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));

        nvgEndFrame(contextHandler);

        // Restore state
        window.restoreState();
    }

    public void incCounter() {
        counter++;
        if (counter > 99) {
            counter = 0;
        }
    }

    public void cleanup() {
        nvgDelete(contextHandler);
    }
}
