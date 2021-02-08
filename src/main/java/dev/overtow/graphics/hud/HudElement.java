package dev.overtow.graphics.hud;

import org.joml.Vector2f;

public interface HudElement {
    void draw(long contextHandler);

    boolean isHovered(Vector2f mousePosition);
}
