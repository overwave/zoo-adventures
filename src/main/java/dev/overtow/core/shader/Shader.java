package dev.overtow.core.shader;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int shaderId;

    enum ShaderType {
        VERTEX(GL_VERTEX_SHADER),
        FRAGMENT(GL_FRAGMENT_SHADER);

        private final int type;

        ShaderType(int type) {
            this.type = type;
        }

        public int get() {
            return type;
        }
    }

    public Shader(String shaderCode, ShaderType shaderType) {
        shaderId = glCreateShader(shaderType.get());
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling shader: " + glGetShaderInfoLog(shaderId, 1024));
        }
    }

    public void execute(Runnable runnable) {

    }

    public int getShaderId() {
        return shaderId;
    }
}
