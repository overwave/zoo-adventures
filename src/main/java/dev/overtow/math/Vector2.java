package dev.overtow.math;

import org.joml.Math;

import java.util.Objects;

public class Vector2 {
    private final float x;
    private final float y;

    private Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 of(float c) {
        return new Vector2(c, c);
    }

    public static Vector2 of(float x, float y) {
        return new Vector2(x, y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2 plus(Vector2 vec) {
        return of(x + vec.x, y + vec.y);
    }

    public Vector2 multiply(Vector2 vec) {
        return of(x * vec.x, y * vec.y);
    }

    public Vector2 multiply(float c) {
        return of(x * c, y * c);
    }

    public Vector2 normalize() {
        float invLength = Math.invsqrt(x * x + y * y);
        return of(x * invLength, y * invLength);
    }

    @Override
    public String toString() {
        return "[" + x + ", \t" + y + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Float.compare(vector2.x, x) == 0 && Float.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
