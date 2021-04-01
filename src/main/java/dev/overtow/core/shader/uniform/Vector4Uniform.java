package dev.overtow.core.shader.uniform;

import dev.overtow.math.Vector4;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class Vector4Uniform extends SingleValueUniform<Vector4> {

    public Vector4Uniform(Name name) {
        super(name);
    }

    @Override
    public void setValue(Vector4 value) {
        glUniform4f(id, value.getX(), value.getY(), value.getZ(), value.getW());
    }
}
