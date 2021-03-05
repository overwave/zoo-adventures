package dev.overtow.core;

import org.joml.Vector2f;

public class Wave {
    private final float amplitude;
    private final float length;
    private final float speed;
    private final float steepness;
    private final Vector2f direction;

    public Wave(float amplitude, float length, float speed, float steepness, Vector2f direction) {
        this.amplitude = amplitude;
        this.length = length;
        this.speed = speed;
        this.steepness = steepness;
        this.direction = direction;
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

    public Vector2f getDirection() {
        return direction;
    }
}
