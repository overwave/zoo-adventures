package dev.overtow.core.shader.uniform;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class Vector4fUniform extends SingleValueUniform<Vector4f> {

    public Vector4fUniform(Name name) {
        super(name);
    }

    @Override
    public void setValue(Vector4f value) {
        glUniform4f(id, value.x(), value.y(), value.z(), value.w());
    }
}
