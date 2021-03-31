package dev.overtow.glsl.type;

public final class Vec2 {
    public static final Vec2 STUB = new Vec2();

    public double x;
    public double y;
    public Vec2 xy;
    public Vec2 yx;

    private Vec2() {
    }

    public Vec2 multiply(double scalar) {
        return STUB;
    }

    public Vec2 multiply(Vec2 vec) {
        return STUB;
    }

    public Vec2 divide(double scalar) {
        return STUB;
    }

    public Vec2 divide(Vec2 vec2) {
        return STUB;
    }

    public Vec2 plus(Vec2 vec) {
        return STUB;
    }

    public Vec2 plus(double c) {
        return STUB;
    }

    public Vec2 minus(Vec2 vec) {
        return STUB;
    }
}
