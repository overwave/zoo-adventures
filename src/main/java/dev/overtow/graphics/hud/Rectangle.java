package dev.overtow.graphics.hud;

import dev.overtow.service.memory.MemoryManager;
import dev.overtow.util.injection.Injector;
import org.joml.Vector2f;
import org.joml.primitives.Intersectionf;

import java.awt.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class Rectangle implements HudElement {
    private float x;
    private float y;
    private float width;
    private float height;
    private Color color;
    private MemoryManager memoryManager;

    public Rectangle(Vector2f position, Vector2f size, Color color) {
        this.memoryManager = Injector.getInstance(MemoryManager.class);
        this.x = position.x();
        this.y = position.y();
        this.width = size.x();
        this.height = size.y();
        this.color = color;
    }

    @Override
    public void draw(long contextHandler) {
        nvgBeginPath(contextHandler);
        nvgRect(contextHandler, x, y, width, height);
        memoryManager.doForColor(color, color -> nvgFillColor(contextHandler, color));
        nvgFill(contextHandler);
    }

    @Override
    public boolean isHovered(Vector2f mousePosition) {
        return Intersectionf.testPointAar(
                mousePosition.x(), mousePosition.y(),
                x, y,
                x + width, y + height);
    }
}
