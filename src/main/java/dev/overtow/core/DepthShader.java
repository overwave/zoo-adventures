package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;

public class DepthShader extends ShaderProgram {

    public DepthShader() {
        uniformMap.put(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX));
        uniformMap.put(Uniform.Name.ORTHO_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.ORTHO_PROJECTION_MATRIX));

        programId = compile("data/shader/depth/");
    }
}
