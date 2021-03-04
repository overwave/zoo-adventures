package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.glsl.shader.depth.DepthFragmentShader;
import dev.overtow.glsl.shader.depth.DepthVertexShader;

public class DepthShaderProgram extends ShaderProgram {
    public DepthShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_MATRIX));

        programId = compile(DepthVertexShader.class, DepthFragmentShader.class);
    }
}
