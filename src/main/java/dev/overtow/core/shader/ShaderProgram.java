package dev.overtow.core.shader;

import dev.overtow.core.shader.uniform.*;
import dev.overtow.service.reader.Reader;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjglb.engine.graph.Material;
import org.lwjglb.engine.graph.lights.DirectionalLight;
import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;

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

        for (Uniform<?> uniform : uniformMap.values()) {
            uniform.locate(uniformName -> glGetUniformLocation(programId, uniformName));
        }

        return programId;
    }

    public void set(Uniform.Name name, int value) {
        ((IntegerUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, float value) {
        ((FloatUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Vector3f value) {
        ((Vector3fUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Matrix4f value) {
        ((Matrix4fUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Material value) {
        ((MaterialUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, int index, PointLight value) {
        ((ArrayUniform<PointLight>) uniformMap.get(name)).setElement(value, index);
    }

    public void set(Uniform.Name name, int index, SpotLight value) {
        ((ArrayUniform<SpotLight>) uniformMap.get(name)).setElement(value, index);
    }

    public void set(Uniform.Name name, DirectionalLight value) {
        ((DirectionalLightUniform) uniformMap.get(name)).setValue(value);
    }

    public void draw(Runnable runnable) {
        glUseProgram(programId);
        runnable.run();
        glUseProgram(0);
    }
}
