package dev.overtow.core;

import java.awt.Color;

public class Material {

    public static final Color DEFAULT_COLOUR = Color.WHITE;

    private final Texture texture;
    private final Color backgroundColor;

    public Material(Texture texture) {
        this(texture, DEFAULT_COLOUR);
    }

    public Material(Texture texture, Color backgroundColor) {
        this.texture = texture;
        this.backgroundColor = backgroundColor;
    }

    public Texture getTexture() {
        return texture;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
//    public Texture getNormalMap() {
//        return normalMap;
//    }
}