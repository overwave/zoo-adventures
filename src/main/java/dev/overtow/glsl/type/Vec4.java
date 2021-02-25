package dev.overtow.glsl.type;

public final class Vec4 {
    public static final Vec4 STUB = new Vec4();

    public double x;
    public double y;
    public double z;
    public double w;
    public Vec2 xy;
    public Vec3 rgb;
    public double a;
    public Vec3 xyz;

    private Vec4() {
    }

    public Vec4 divide(double scalar) {
        return Vec4.STUB;
    }

    public Vec4 minus(Vec4 vec) {
        return Vec4.STUB;
    }

    public Vec4 plus(Vec4 vec) {
        return Vec4.STUB;
    }

    public Vec4 multiply(double scalar) {
        return Vec4.STUB;
    }
}
