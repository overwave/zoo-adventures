package dev.overtow.core;

import dev.overtow.math.Vector2;

public class Ripple {
    private final Vector2 center;
    private final Vector2 direction;

    public Ripple(Vector2 center, Vector2 direction) {
        this.center = center;
        this.direction = direction;
    }

    public Vector2 getCenter() {
        return center;
    }

    public Vector2 getDirection() {
        return direction;
    }
}
