package dev.overtow.core.shader.uniform;

import org.lwjglb.engine.graph.lights.DirectionalLight;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;

public class DirectionalLightUniform implements ValueUniform<DirectionalLight> {

    private final String namePrefix;
    private DirectionalLight value;

    private int colourId;
    private int directionId;
    private int intensityId;

    public DirectionalLightUniform(String name) {
        namePrefix = name;
    }

    public void setValue(DirectionalLight value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;

        glUniform3f(colourId, value.getColor().x(), value.getColor().y(), value.getColor().z());
        glUniform3f(directionId, value.getDirection().x(), value.getDirection().y(), value.getDirection().z());
        glUniform1f(intensityId, value.getIntensity());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        colourId = function.apply(namePrefix + ".colour");
        directionId = function.apply(namePrefix + ".direction");
        intensityId = function.apply(namePrefix + ".intensity");
    }
}
