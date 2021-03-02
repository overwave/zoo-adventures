package dev.overtow.core.shader;

import dev.overtow.core.shader.uniform.*;
import dev.overtow.glsl.Converter;
import dev.overtow.glsl.shader.FragmentShader;
import dev.overtow.glsl.shader.VertexShader;
import dev.overtow.service.reader.Reader;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    protected int programId;

    protected final Map<Uniform.Name, Uniform<?>> uniformMap = new HashMap<>();

    protected int compile(Class<? extends VertexShader> vsClassToken, Class<? extends FragmentShader> fsClassToken) {
        List<Uniform.Name> uninitializedUniforms = Converter.getUsedUniforms(List.of(vsClassToken, fsClassToken));
        uninitializedUniforms.removeAll(uniformMap.keySet());
        if (uninitializedUniforms.size() != 0) {
            throw new IllegalStateException("Uniforms are not initialized: " + uninitializedUniforms);
        }

        int programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create shader program");
        }

        Reader reader = Injector.getInstance(Reader.class);

        String vertexShaderCode;
        String fragmentShaderCode;
        try {
            vertexShaderCode = reader.read(Converter.convert(vsClassToken));
            fragmentShaderCode = reader.read(Converter.convert(fsClassToken));
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

        glUseProgram(programId);
        for (Uniform<?> uniform : uniformMap.values()) {
            uniform.locate(uniformName -> glGetUniformLocation(programId, uniformName));
        }
        glUseProgram(0);

        return programId;
    }

    public void set(Uniform.Name name, int value) {
        checkedGet(name, IntegerUniform.class).setValue(value);
    }

    public void set(Uniform.Name name, float value) {
        checkedGet(name, FloatUniform.class).setValue(value);
    }

    public void set(Uniform.Name name, Vector3f value) {
        checkedGet(name, Vector3fUniform.class).setValue(value);
    }

    public void set(Uniform.Name name, Vector4f value) {
        checkedGet(name, Vector4fUniform.class).setValue(value);
    }

    public void set(Uniform.Name name, Matrix4f value) {
        checkedGet(name, Matrix4fUniform.class).setValue(value);
    }

    private <T extends Uniform<?>> T checkedGet(Uniform.Name name, Class<T> uniformType) {
        Uniform<?> value = uniformMap.get(name);
        if (value == null) {
            throw new IllegalStateException("Uniform " + name + " was not created!");
        }
        return uniformType.cast(value);
    }

    public void executeWithProgram(Consumer<ShaderProgram> runnable) {
        glUseProgram(programId);
        runnable.accept(this);
        glUseProgram(0);
    }
}
