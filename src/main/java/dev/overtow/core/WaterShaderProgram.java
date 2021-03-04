package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.FloatUniform;
import dev.overtow.core.shader.uniform.IntegerUniform;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.core.shader.uniform.Vector3fUniform;
import dev.overtow.glsl.shader.water.WaterFragmentShader;
import dev.overtow.glsl.shader.water.WaterVertexShader;

public class WaterShaderProgram extends ShaderProgram {
    public WaterShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.LIGHT_VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.BIAS_MATRIX, new Matrix4fUniform(Uniform.Name.BIAS_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_POSITION, new Vector3fUniform(Uniform.Name.LIGHT_POSITION));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.DEPTH_TEXTURE, new IntegerUniform(Uniform.Name.DEPTH_TEXTURE));
        uniformMap.put(Uniform.Name.TIME, new FloatUniform(Uniform.Name.TIME));

        programId = compile(WaterVertexShader.class, WaterFragmentShader.class);
    }
}
