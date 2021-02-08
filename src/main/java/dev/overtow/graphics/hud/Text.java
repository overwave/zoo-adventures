package dev.overtow.graphics.hud;

import dev.overtow.service.memory.MemoryManager;
import dev.overtow.util.injection.Injector;
import org.joml.Vector2f;

import java.awt.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class Text implements HudElement {
    private String text;
    private float fontSize;
    private String fontName;
    private float x;
    private float y;
    private int align;
    private float height;
    private Color color;
    private MemoryManager memoryManager;

    public Text(Vector2f position, String text, float fontSize, String fontName, Color color, TextAlign... textAligns) {
        this.memoryManager = Injector.getInstance(MemoryManager.class);
        this.x = position.x();
        this.y = position.y();
        this.text = text;
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.color = color;
        this.align = TextAlign.mergeMasks(textAligns);
    }

    @Override
    public void draw(long contextHandler) {
        nvgFontSize(contextHandler, fontSize);
        nvgFontFace(contextHandler, fontName);
        nvgTextAlign(contextHandler, align);
        memoryManager.doForColor(color, color -> nvgFillColor(contextHandler, color));
        nvgText(contextHandler, x, y, text);
    }

    @Override
    public boolean isHovered(Vector2f mousePosition) {
        return false;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
