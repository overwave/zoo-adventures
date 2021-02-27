package dev.overtow.core.shader;

import dev.overtow.core.shader.uniform.*;
import dev.overtow.glsl.shader.FragmentShader;
import dev.overtow.glsl.shader.VertexShader;
import dev.overtow.service.reader.Reader;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.graph.Material;
import org.lwjglb.engine.graph.lights.DirectionalLight;
import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    protected int programId;

    protected final Map<Uniform.Name, Uniform<?>> uniformMap = new HashMap<>();

    protected int compile(Class<? extends VertexShader> vsClassToken, Class<? extends FragmentShader> fsClassToken) {
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
            vertexShaderCode = reader.read("data/shader/target/" + vsClassToken.getSimpleName() + ".vert");
            fragmentShaderCode = reader.read("data/shader/target/" + fsClassToken.getSimpleName() + ".frag");
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
        ((IntegerUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, float value) {
        ((FloatUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Vector3f value) {
        ((Vector3fUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Vector4f value) {
        ((Vector4fUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Matrix4f value) {
        ((Matrix4fUniform) uniformMap.get(name)).setValue(value);
    }

    public void set(Uniform.Name name, Material value) {
        ((MaterialUniform) uniformMap.get(name)).setValue(value);
    }

    // TODO NOT TESTED
    public void set(Uniform.Name name, int index, PointLight value) {
        ((ArrayUniform<PointLight>) uniformMap.get(name)).setElement(value, index);
    }

    // TODO NOT TESTED
    public void set(Uniform.Name name, int index, SpotLight value) {
        ((ArrayUniform<SpotLight>) uniformMap.get(name)).setElement(value, index);
    }

    public void set(Uniform.Name name, DirectionalLight value) {
        ((DirectionalLightUniform) uniformMap.get(name)).setValue(value);
    }

    public void draw(Consumer<ShaderProgram> runnable) {
        glUseProgram(programId);
        runnable.accept(this);
        glUseProgram(0);
    }
}
