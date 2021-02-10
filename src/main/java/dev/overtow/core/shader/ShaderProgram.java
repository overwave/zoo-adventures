package dev.overtow.core.shader;

import dev.overtow.core.DepthShader;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.service.reader.Reader;
import dev.overtow.util.injection.Injector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    protected int programId;

    protected final Map<Uniform.Name, Uniform<?>> uniformMap = new HashMap<>();

    protected int compile(String folderPath) {
        if (uniformMap.isEmpty()) {
            throw new IllegalStateException();
        }

        int programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create shader program");
        }

        Reader reader = Injector.getInstance(Reader.class);

        String vertexShaderCode;
        String fragmentShaderCode;
        try {
            vertexShaderCode = reader.read(folderPath + "shader.vert");
            fragmentShaderCode = reader.read(folderPath + "shader.frag");
        } catch (IOException e) {
            throw new RuntimeException("failed to load shader code", e);
        }

        Shader vertexShader = new Shader(vertexShaderCode, Shader.ShaderType.VERTEX);
        Shader fragmentShader = new Shader(fragmentShaderCode, Shader.ShaderType.FRAGMENT);

        glAttachShader(programId, vertexShader.getShaderId());
        glAttachShader(programId, fragmentShader.getShaderId());

        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        glDetachShader(programId, vertexShader.getShaderId());
        glDetachShader(programId, fragmentShader.getShaderId());

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        for (Uniform<?> uniform : uniforms) {
            uniform.locate(uniformName -> glGetUniformLocation(programId, uniformName));
        }

        return programId;
    }

    public abstract void bind();

    public abstract void unbind();

    public abstract void draw();
}