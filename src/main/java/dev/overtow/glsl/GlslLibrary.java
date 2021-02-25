package dev.overtow.glsl;

import dev.overtow.glsl.type.*;

public interface GlslLibrary {

    static double sin(double v) {
        return 0;
    }

    static double dot(Vec2 a, Vec2 b) {
        return 0;
    }

    static double dot(Vec3 a, Vec3 b) {
        return 0;
    }

    static double fract(double v) {
        return 0;
    }

    static Vec2 round(Vec2 v) {
        return Vec2.STUB;
    }

    static Vec3 normalize(Vec3 vec3) {
        return Vec3.STUB;
    }

    static Vec4 texture(Sampler2D sampler, Vec2 coordinate) {
        return Vec4.STUB;
    }

    static Vec2 vec2(double x, double y) {
        return Vec2.STUB;
    }

    static Vec2 vec2(double c) {
        return Vec2.STUB;
    }

    static Mat4 mat4(double c) {
        return Mat4.STUB;
    }

    static Vec3 vec3(double c) {
        return Vec3.STUB;
    }

    static Vec4 vec4(double c) {
        return Vec4.STUB;
    }

    static Vec4 vec4(double x, double y, double z, double w) {
        return Vec4.STUB;
    }

    static Vec4 vec4(Vec3 xyz, double w) {
        return Vec4.STUB;
    }

    static double max(double a, double... other) {
        return 0;
    }

    static Vec3 mix(Vec3 a, Vec3 b, double x) {
        return Vec3.STUB;
    }
}
