package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.MatrixUniform;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.glsl.shader.depth.DepthFragmentShader;
import dev.overtow.glsl.shader.depth.DepthVertexShader;

public class DepthShaderProgram extends ShaderProgram {
    public DepthShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new MatrixUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new MatrixUniform(Uniform.Name.MODEL_MATRIX));

        programId = compile(DepthVertexShader.class, DepthFragmentShader.class);
    }
}
