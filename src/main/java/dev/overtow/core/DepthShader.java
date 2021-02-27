package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.glsl.shader.DepthFragmentShader;
import dev.overtow.glsl.shader.DepthVertexShader;

public class DepthShader extends ShaderProgram {

    public DepthShader() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));

        programId = compile(DepthVertexShader.class, DepthFragmentShader.class);
    }
}
