package dev.overtow.core;

import org.joml.Vector2f;

public class Ripple {
    private final Vector2f center;
    private final Vector2f direction;

    public Ripple(Vector2f center, Vector2f direction) {
        this.center = center;
        this.direction = direction;
    }

    public Vector2f getCenter() {
        return new Vector2f(center);
    }

    public Vector2f getDirection() {
        return new Vector2f(direction);
    }
}
