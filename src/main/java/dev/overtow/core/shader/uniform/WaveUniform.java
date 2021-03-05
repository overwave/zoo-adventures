package dev.overtow.core.shader.uniform;

import dev.overtow.core.Wave;
import org.joml.Vector2f;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;

public class WaveUniform implements ValueUniform<Wave> {

    private final String namePrefix;

    private int amplitude;
    private int length;
    private int speed;
    private int steepness;
    private int direction;

    public WaveUniform(String name) {
        this.namePrefix = name;
    }

    @Override
    public void setValue(Wave wave) {
        glUniform1f(amplitude, wave.getAmplitude());
        glUniform1f(length, wave.getLength());
        glUniform1f(speed, wave.getSpeed());
        glUniform1f(steepness, wave.getSteepness());
        Vector2f waveDirection = wave.getDirection();
        glUniform2f(this.direction, waveDirection.x(), waveDirection.y());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        amplitude = function.apply(namePrefix + ".amplitude");
        length = function.apply(namePrefix + ".length");
        speed = function.apply(namePrefix + ".speed");
        steepness = function.apply(namePrefix + ".steepness");
        direction = function.apply(namePrefix + ".direction");
    }
}
