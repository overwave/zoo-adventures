package dev.overtow.core.shader.uniform;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Matrix4fUniform extends SingleValueUniform<Matrix4f> {

    public Matrix4fUniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Matrix4f value) {
//        if (value.equals(this.value)) {
//            return;
//        }
//        this.value = value;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(id, false, value.get(stack.mallocFloat(16)));
        }
    }
}
