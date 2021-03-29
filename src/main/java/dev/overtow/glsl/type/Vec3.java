package dev.overtow.glsl.type;

public final class Vec3 {
    public static final Vec3 STUB = new Vec3();

    public double x;
    public double y;
    public double z;
    public Vec2 xz;
    public Vec2 zx;
    public Vec3 xzy;

    private Vec3() {
    }

    public Vec3 minus(Vec3 vec3) {
        return STUB;
    }

    public Vec3 multiply(double scalar) {
        return STUB;
    }

    public Vec3 plus(Vec3 vec3) {
        return STUB;
    }

    public Vec3 multiply(Vec3 vec3) {
        return STUB;
    }
}
