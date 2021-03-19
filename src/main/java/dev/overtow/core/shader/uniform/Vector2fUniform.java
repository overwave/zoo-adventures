package dev.overtow.core.shader.uniform;

import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.glUniform2f;

public class Vector2fUniform extends SingleValueUniform<Vector2f> {

    public Vector2fUniform(Name name) {
        super(name);
    }

    @Override
    public void setValue(Vector2f value) {
        glUniform2f(id, value.x(), value.y());
    }
}
