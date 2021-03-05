package dev.overtow.glsl.type.struct;

import dev.overtow.glsl.GlslType;
import dev.overtow.glsl.type.Vec2;
import org.joml.Vector2f;

public class Wave {
    private final float waveAmplitude;
    private final float waveLength;
    private final float waveSpeed;
    private final float steepness;
    @GlslType(Vec2.class)
    private final Vector2f waveDirection;

    public Wave(float waveAmplitude, float waveLength, float waveSpeed, float steepness, Vector2f waveDirection) {
        this.waveAmplitude = waveAmplitude;
        this.waveLength = waveLength;
        this.waveSpeed = waveSpeed;
        this.steepness = steepness;
        this.waveDirection = waveDirection;
    }

    public Wave() {
        throw new UnsupportedOperationException();
    }

    public float getWaveAmplitude() {
        return waveAmplitude;
    }

    public float getWaveLength() {
        return waveLength;
    }

    public float getWaveSpeed() {
        return waveSpeed;
    }

    public float getSteepness() {
        return steepness;
    }

    public Vector2f getWaveDirection() {
        return waveDirection;
    }
}
