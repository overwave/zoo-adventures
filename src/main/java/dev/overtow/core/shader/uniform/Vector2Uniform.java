package dev.overtow.core.shader.uniform;

import dev.overtow.math.Vector2;

import static org.lwjgl.opengl.GL20.glUniform2f;

public class Vector2Uniform extends SingleValueUniform<Vector2> {

    public Vector2Uniform(Name name) {
        super(name);
    }

    @Override
    public void setValue(Vector2 value) {
        glUniform2f(id, value.getX(), value.getY());
    }
}
