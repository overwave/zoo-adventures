package dev.overtow.math;

import org.joml.Math;

import java.util.Objects;

public class Vector3 {
    public static final Vector3 ONE = Vector3.of(1, 1, 1);
    public static final Vector3 ZERO = Vector3.of(0, 0, 0);

    private final float x;
    private final float y;
    private final float z;

    private Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3 of(float c) {
        return new Vector3(c, c, c);
    }

    public static Vector3 of(float x, float y, float z) {
        return new Vector3(x, y, z);
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

    public Vector2 getXZ() {
        return Vector2.of(x, z);
    }

    public Vector3 plus(Vector3 vec) {
        return of(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vector3 minus(Vector3 vec) {
        return of(x - vec.x, y - vec.y, z - vec.z);
    }

    public Vector3 divide(float c) {
        return of(x / c, y / c, z / c);
    }

    public Vector3 negate() {
        return of(-x, -y, -z);
    }

    public Vector3 normalize() {
        float scalar = Math.invsqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
        return of(x * scalar, y * scalar, z * scalar);
    }

    public float length() {
        return Math.sqrt(Math.fma(x, x, Math.fma(y, y, z * z)));
    }

    public Vector3 cross(Vector3 vec) {
        float rx = Math.fma(y, vec.z, -z * vec.y);
        float ry = Math.fma(z, vec.x, -x * vec.z);
        float rz = Math.fma(x, vec.y, -y * vec.x);
        return of(rx, ry, rz);
    }

    @Override
    public String toString() {
        return "[" + x + ", \t" + y + ", \t" + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Float.compare(vector3.x, x) == 0 && Float.compare(vector3.y, y) == 0 && Float.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
