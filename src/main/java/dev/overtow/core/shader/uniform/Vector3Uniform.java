package dev.overtow.core.shader.uniform;

import dev.overtow.math.Vector3;

import static org.lwjgl.opengl.GL20.glUniform3f;

public class Vector3Uniform extends SingleValueUniform<Vector3> {

    public Vector3Uniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Vector3 value) {
        glUniform3f(id, value.getX(), value.getY(), value.getY());
    }
}
