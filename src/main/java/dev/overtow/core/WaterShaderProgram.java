package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.*;
import dev.overtow.glsl.shader.water.WaterFragmentShader;
import dev.overtow.glsl.shader.water.WaterVertexShader;

import static dev.overtow.glsl.shader.water.WaterVertexShader.WAVES_AMOUNT;

public class WaterShaderProgram extends ShaderProgram {
    public WaterShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.NORMAL_MATRIX, new Matrix4fUniform(Uniform.Name.NORMAL_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_POSITION, new Vector3fUniform(Uniform.Name.LIGHT_POSITION));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.TIME, new FloatUniform(Uniform.Name.TIME));
        uniformMap.put(Uniform.Name.WAVES, new ArrayUniform<>(Uniform.Name.WAVES, WAVES_AMOUNT, WaveUniform::new));
        uniformMap.put(Uniform.Name.TEXTURE_MOVING_DIRECTION, new Vector2fUniform(Uniform.Name.TEXTURE_MOVING_DIRECTION));

        programId = compile(WaterVertexShader.class, WaterFragmentShader.class);
    }
}
