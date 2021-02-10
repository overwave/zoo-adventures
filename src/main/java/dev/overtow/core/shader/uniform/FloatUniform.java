package dev.overtow.core.shader.uniform;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class FloatUniform extends SingleValueUniform<Float> {

    public FloatUniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Float value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;
        glUniform1f(id, value);
    }
}
