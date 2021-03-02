package dev.overtow.core.shader.uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class FloatUniform extends SingleValueUniform<Float> {

    public FloatUniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Float value) {
        glUniform1f(id, value);
    }
}
