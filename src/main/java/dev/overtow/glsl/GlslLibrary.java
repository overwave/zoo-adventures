package dev.overtow.glsl;

import dev.overtow.glsl.type.Mat4;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

public interface GlslLibrary {

    static double sin(double v) {
        throw new UnsupportedOperationException();
    }

    static Vec2 sin(Vec2 v) {
        throw new UnsupportedOperationException();
    }

    static double cos(double v) {
        throw new UnsupportedOperationException();
    }

    static double clamp(double x, double a, double b) {
        throw new UnsupportedOperationException();
    }

    static double dot(Vec2 a, Vec2 b) {
        throw new UnsupportedOperationException();
    }

    static double dot(Vec3 a, Vec3 b) {
        throw new UnsupportedOperationException();
    }

    static double fract(double v) {
        throw new UnsupportedOperationException();
    }

    static Vec2 fract(Vec2 v) {
        throw new UnsupportedOperationException();
    }

    static double length(Vec2 v) {
        throw new UnsupportedOperationException();
    }

    static Vec2 floor(Vec2 v) {
        throw new UnsupportedOperationException();
    }

    static Vec2 round(Vec2 v) {
        throw new UnsupportedOperationException();
    }

    static Vec3 normalize(Vec3 vec) {
        throw new UnsupportedOperationException();
    }

    static Vec4 normalize(Vec4 vec) {
        throw new UnsupportedOperationException();
    }

    static Vec2 normalize(Vec2 vec) {
        throw new UnsupportedOperationException();
    }

    static Vec4 texture(Sampler2D sampler, Vec2 coordinate) {
        throw new UnsupportedOperationException();
    }

    static Vec2 vec2(double x, double y) {
        throw new UnsupportedOperationException();
    }

    static Vec2 vec2(double c) {
        throw new UnsupportedOperationException();
    }

    static Mat4 mat4(double c) {
        throw new UnsupportedOperationException();
    }

    static float float_(double c) {
        throw new UnsupportedOperationException();
    }

    static Vec3 vec3(double c) {
        throw new UnsupportedOperationException();
    }

    static Vec3 vec3(Vec3 v) {
        throw new UnsupportedOperationException();
    }

    static Vec3 vec3(double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    static Vec4 vec4(double c) {
        throw new UnsupportedOperationException();
    }

    static Vec4 vec4(double x, double y, double z, double w) {
        throw new UnsupportedOperationException();
    }

    static Vec4 vec4(Vec3 xyz, double w) {
        throw new UnsupportedOperationException();
    }

    static double max(double a, double... other) {
        throw new UnsupportedOperationException();
    }

    static Vec3 mix(Vec3 a, Vec3 b, double x) {
        throw new UnsupportedOperationException();
    }

    static double sqrt(double x) {
        throw new UnsupportedOperationException();
    }

    static double abs(double x) {
        throw new UnsupportedOperationException();
    }

    static Vec3 cross(Vec3 a, Vec3 b) {
        throw new UnsupportedOperationException();
    }
}
