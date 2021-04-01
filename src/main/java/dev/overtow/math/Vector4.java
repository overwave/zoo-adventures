package dev.overtow.math;

import org.joml.Math;

import java.util.Objects;

public class Vector4 {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    private Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Vector4 of() {
        return new Vector4(0, 0, 0, 1);
    }

    public static Vector4 of(float c) {
        return new Vector4(c, c, c, c);
    }

    public static Vector4 of(float x, float y, float z, float w) {
        return new Vector4(x, y, z, w);
    }

    public static Vector4 of(Vector3 vec, float w) {
        return new Vector4(vec.getX(), vec.getY(), vec.getZ(), w);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    public Vector3 getXYZ() {
        return Vector3.of(x, y, z);
    }

    public Vector2 getXZ() {
        return Vector2.of(x, z);
    }

    public Vector4 plus(Vector4 vec) {
        return of(x + vec.x, y + vec.y, z + vec.z, w + vec.w);
    }

    public Vector4 divide(float c) {
        return of(x / c, y / c, z / c, w / c);
    }

    public Vector4 multiply(Matrix matrix) {
        return matrix.getMultiplied(this);
    }

    public Vector4 normalize() {
        return divide(length());
    }

    public float length() {
        return Math.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
    }

    @Override
    public String toString() {
        return "[" + x + ", \t" + y + ", \t" + z + ", \t" + w + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector4 vector4 = (Vector4) o;
        return Float.compare(vector4.x, x) == 0 && Float.compare(vector4.y, y) == 0 && Float.compare(vector4.z, z) == 0 && Float.compare(vector4.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
