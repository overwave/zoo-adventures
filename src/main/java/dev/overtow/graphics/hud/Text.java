package dev.overtow.graphics.hud;

import dev.overtow.core.Font;
import dev.overtow.service.memory.MemoryManager;
import dev.overtow.util.injection.Injector;
import org.joml.Vector2f;

import java.awt.Color;

import static org.lwjgl.nanovg.NanoVG.*;

public class Text implements HudElement {
    private String text;
    private float fontSize;
    private Font font;
    private float x;
    private float y;
    private int align;
    private Color color;
    private MemoryManager memoryManager;

    public Text(Vector2f position, String text, float fontSize, Font font, Color color, TextAlign... textAligns) {
        this.memoryManager = Injector.getInstance(MemoryManager.class);
        this.x = position.x();
        this.y = position.y();
        this.text = text;
        this.fontSize = fontSize;
        this.font = font;
        this.color = color;
        this.align = TextAlign.mergeMasks(textAligns);
    }

    @Override
    public void draw(long contextHandler) {
        nvgFontSize(contextHandler, fontSize);
        nvgFontFace(contextHandler, font.name());
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
