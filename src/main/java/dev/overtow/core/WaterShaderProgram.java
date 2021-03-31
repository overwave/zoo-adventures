package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.*;
import dev.overtow.glsl.shader.water.WaterFragmentShader;
import dev.overtow.glsl.shader.water.WaterVertexShader;

import static dev.overtow.glsl.shader.water.WaterVertexShader.RIPPLES_AMOUNT;
import static dev.overtow.glsl.shader.water.WaterVertexShader.WAVES_AMOUNT;

public class WaterShaderProgram extends ShaderProgram {
    public WaterShaderProgram() {
        uniformMap.put(Uniform.Name.VIEW_PROJECTION_MATRIX, new MatrixUniform(Uniform.Name.VIEW_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.NORMAL_MATRIX, new MatrixUniform(Uniform.Name.NORMAL_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_MATRIX, new MatrixUniform(Uniform.Name.MODEL_MATRIX));
        uniformMap.put(Uniform.Name.LIGHT_POSITION, new Vector3Uniform(Uniform.Name.LIGHT_POSITION));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.TIME, new FloatUniform(Uniform.Name.TIME));
        uniformMap.put(Uniform.Name.WAVES, new ArrayUniform<>(Uniform.Name.WAVES, WAVES_AMOUNT, WaveUniform::new));
        uniformMap.put(Uniform.Name.RIPPLES, new ArrayUniform<>(Uniform.Name.RIPPLES, RIPPLES_AMOUNT, RippleUniform::new));
        uniformMap.put(Uniform.Name.TEXTURE_MOVING_DIRECTION, new Vector2Uniform(Uniform.Name.TEXTURE_MOVING_DIRECTION));

        programId = compile(WaterVertexShader.class, WaterFragmentShader.class);
    }
}
