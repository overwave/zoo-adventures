package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;

public class ShadowShader extends ShaderProgram {

    public ShadowShader() {
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_VIEW_MATRIX, new Matrix4fUniform(Uniform.Name.LIGHT_VIEW_MATRIX));
        uniformMap.put(Uniform.Name.ORTHO_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.ORTHO_PROJECTION_MATRIX));

        programId = compile("data/shader/shadow/");
    }
}
