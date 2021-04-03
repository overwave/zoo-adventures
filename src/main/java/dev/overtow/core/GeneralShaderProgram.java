package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.IntegerUniform;
import dev.overtow.core.shader.uniform.MatrixUniform;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.core.shader.uniform.Vector3Uniform;
import dev.overtow.core.shader.uniform.Vector4Uniform;
import dev.overtow.glsl.shader.general.GeneralFragmentShader;
import dev.overtow.glsl.shader.general.GeneralVertexShader;

public class GeneralShaderProgram extends ShaderProgram {
    public GeneralShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new MatrixUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_VIEW_PROJECTION_MATRIX, new MatrixUniform(Uniform.Name.LIGHT_VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.NORMAL_MATRIX, new MatrixUniform(Uniform.Name.NORMAL_MATRIX));
        uniformMap.put(Uniform.Name.BIAS_MATRIX, new MatrixUniform(Uniform.Name.BIAS_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new MatrixUniform(Uniform.Name.MODEL_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_POSITION, new Vector3Uniform(Uniform.Name.LIGHT_POSITION));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.DEPTH_TEXTURE, new IntegerUniform(Uniform.Name.DEPTH_TEXTURE));
        uniformMap.put(Uniform.Name.BACKGROUND_COLOR, new Vector4Uniform(Uniform.Name.BACKGROUND_COLOR));
        uniformMap.put(Uniform.Name.SHADOWS_ANTIALIASING_LEVEL, new Vector3Uniform(Uniform.Name.SHADOWS_ANTIALIASING_LEVEL));

        programId = compile(GeneralVertexShader.class, GeneralFragmentShader.class);
    }
}
