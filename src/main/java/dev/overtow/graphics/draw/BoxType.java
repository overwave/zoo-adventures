package dev.overtow.graphics.draw;

import java.awt.Color;

public enum BoxType {
    BANANA(new Color(230, 255, 45, 255)),
    HAT(new Color(127, 127, 255, 255)),
    PARROT(new Color(182, 158, 255, 255)),
    CRAB(new Color(255, 208, 204, 255)),
    STARFISH(new Color(137, 255, 190, 255)),
    CACTUS(new Color(255, 197, 7, 255)),
    FLAMINGO(new Color(165, 255, 91, 255)),
    SUN(new Color(66, 229, 255, 255));

    private final Color color;

    BoxType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
