package dev.overtow.core.shader.uniform;

import dev.overtow.math.Matrix;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class MatrixUniform extends SingleValueUniform<Matrix> {

    public MatrixUniform(Uniform.Name name) {
        super(name);
    }

    @Override
    public void setValue(Matrix value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(id, false, value.allocate(stack.mallocFloat(16)));
        }
    }
}
