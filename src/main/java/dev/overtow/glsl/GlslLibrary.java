package dev.overtow.glsl;

import dev.overtow.glsl.type.Mat4;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

public interface GlslLibrary {

    static double sin(double v) {
        return 0;
    }

    static double cos(double v) {
        return 0;
    }

    static double clamp(double x, double a, double b) {
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

    static Vec3 normalize(Vec3 vec) {
        return Vec3.STUB;
    }

    static Vec2 normalize(Vec2 vec) {
        return Vec2.STUB;
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

    static float float_(double c) {
        return 0;
    }

    static Vec3 vec3(double c) {
        return Vec3.STUB;
    }

    static Vec3 vec3(Vec3 v) {
        return Vec3.STUB;
    }

    static Vec3 vec3(double x, double y, double z) {
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

    static double sqrt(double x) {
        return 0;
    }

    static Vec3 cross(Vec3 a, Vec3 b) {
        return Vec3.STUB;
    }
}
