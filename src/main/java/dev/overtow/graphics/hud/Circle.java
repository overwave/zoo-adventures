package dev.overtow.graphics.hud;

import dev.overtow.service.memory.MemoryManager;
import dev.overtow.util.injection.Injector;
import org.joml.Vector2f;
import org.joml.primitives.Intersectionf;

import java.awt.*;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;

public class Circle implements HudElement {
    private float x;
    private float y;
    private float radius;
    private float radiusSquared;
    private Color color;
    private MemoryManager memoryManager;

    public Circle(Vector2f position, float radius, Color color) {
        this.memoryManager = Injector.getInstance(MemoryManager.class);
        this.x = position.x();
        this.y = position.y();
        this.radius = radius;
        this.radiusSquared = radius * radius;
        this.color = color;
    }

    @Override
    public void draw(long contextHandler) {
        nvgBeginPath(contextHandler);
        nvgCircle(contextHandler, x, y, radius);
        memoryManager.doForColor(color, color -> nvgFillColor(contextHandler, color));
        nvgFill(contextHandler);
    }

    @Override
    public boolean isHovered(Vector2f mousePosition) {
        return Intersectionf.testPointCircle(
                mousePosition.x(), mousePosition.y(),
                x, y,
                radiusSquared);
    }
}
