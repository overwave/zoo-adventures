package dev.overtow.core;

import dev.overtow.math.Vector2;

public class Wave {
    private final float amplitude;
    private final float length;
    private final float speed;
    private final float steepness;
    private Vector2 direction;

    public Wave(float amplitude, float length, float speed, float steepness, Vector2 direction) {
        this.amplitude = amplitude;
        this.length = length;
        this.speed = speed;
        this.steepness = steepness;
        this.direction = direction.normalize();
    }

    public float getAmplitude() {
        return amplitude;
    }

    public float getLength() {
        return length;
    }

    public float getSpeed() {
        return speed;
    }

    public float getSteepness() {
        return steepness;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public Vector2 getDirection() {
        return direction;
    }
}
