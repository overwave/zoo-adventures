package dev.overtow.core.shader.uniform;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.glUniform3f;

public class Vector3fUniform extends SingleValueUniform<Vector3f> {

    public Vector3fUniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Vector3f value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = new Vector3f(value);
        glUniform3f(id, value.x, value.y, value.z);
    }
}
