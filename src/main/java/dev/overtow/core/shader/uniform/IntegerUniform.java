package dev.overtow.core.shader.uniform;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class IntegerUniform extends SingleValueUniform<Integer> {

    public IntegerUniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Integer value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;
        glUniform1i(id, value);
    }
}
