package dev.overtow.core.shader.uniform;

import dev.overtow.glsl.type.struct.Wave;
import org.joml.Vector2f;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;

public class WaveUniform implements ValueUniform<Wave> {

    private final String namePrefix;

    int waveAmplitude;
    int waveLength;
    int waveSpeed;
    int steepness;
    int waveDirection;

    public WaveUniform(Name name) {
        this.namePrefix = name.get();
    }

    @Override
    public void setValue(Wave wave) {
        glUniform1f(waveAmplitude, wave.getWaveAmplitude());
        glUniform1f(waveLength, wave.getWaveLength());
        glUniform1f(waveSpeed, wave.getWaveSpeed());
        glUniform1f(steepness, wave.getSteepness());
        Vector2f waveDirection = wave.getWaveDirection();
        glUniform2f(this.waveDirection, waveDirection.x(), waveDirection.y());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        waveAmplitude = function.apply(namePrefix + ".waveAmplitude");
        waveLength = function.apply(namePrefix + ".waveLength");
        waveSpeed = function.apply(namePrefix + ".waveSpeed");
        steepness = function.apply(namePrefix + ".steepness");
        waveDirection = function.apply(namePrefix + ".waveDirection");
    }
}
