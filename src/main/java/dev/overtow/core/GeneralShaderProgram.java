package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.IntegerUniform;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.core.shader.uniform.Vector3fUniform;
import dev.overtow.glsl.shader.general.GeneralFragmentShader;
import dev.overtow.glsl.shader.general.GeneralVertexShader;

public class GeneralShaderProgram extends ShaderProgram {
    public GeneralShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.LIGHT_VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.NORMAL_MATRIX, new Matrix4fUniform(Uniform.Name.NORMAL_MATRIX));
        uniformMap.put(Uniform.Name.BIAS_MATRIX, new Matrix4fUniform(Uniform.Name.BIAS_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_POSITION, new Vector3fUniform(Uniform.Name.LIGHT_POSITION));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.DEPTH_TEXTURE, new IntegerUniform(Uniform.Name.DEPTH_TEXTURE));

        programId = compile(GeneralVertexShader.class, GeneralFragmentShader.class);
    }
}
