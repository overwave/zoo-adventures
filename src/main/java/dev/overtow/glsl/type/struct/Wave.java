package dev.overtow.glsl.type.struct;

import dev.overtow.glsl.GlslType;
import dev.overtow.glsl.type.Vec2;
import org.joml.Vector2f;

public class Wave {
    double waveAmplitude;
    double waveLength;
    double waveSpeed;
    double steepness;
    @GlslType(Vec2.class)
    Vector2f waveDirection;

    public Wave(double waveAmplitude, double waveLength, double waveSpeed, double steepness, Vector2f waveDirection) {
        this.waveAmplitude = waveAmplitude;
        this.waveLength = waveLength;
        this.waveSpeed = waveSpeed;
        this.steepness = steepness;
        this.waveDirection = waveDirection;
    }

    public Wave() {
    }
}
