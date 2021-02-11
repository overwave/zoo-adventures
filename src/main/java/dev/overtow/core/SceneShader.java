package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.*;

public class SceneShader extends ShaderProgram {

    private static final int MAX_POINT_LIGHTS = 5;  // also hardcoded in shader :(
    private static final int MAX_SPOT_LIGHTS = 5;

    public SceneShader() {
        uniformMap.put(Uniform.Name.PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_VIEW_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_VIEW_MATRIX));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.NORMAL_MAP, new IntegerUniform(Uniform.Name.NORMAL_MAP));
        uniformMap.put(Uniform.Name.MATERIAL, new MaterialUniform(Uniform.Name.MATERIAL));
        uniformMap.put(Uniform.Name.SPECULAR_POWER, new FloatUniform(Uniform.Name.SPECULAR_POWER));
        uniformMap.put(Uniform.Name.AMBIENT_LIGHT, new Vector3fUniform(Uniform.Name.AMBIENT_LIGHT));
        uniformMap.put(Uniform.Name.POINT_LIGHTS, new ArrayUniform<>(Uniform.Name.POINT_LIGHTS, MAX_POINT_LIGHTS, PointLightUniform::new));
        uniformMap.put(Uniform.Name.SPOT_LIGHTS, new ArrayUniform<>(Uniform.Name.SPOT_LIGHTS, MAX_SPOT_LIGHTS, SpotLightUniform::new));
        uniformMap.put(Uniform.Name.DIRECTIONAL_LIGHT, new DirectionalLightUniform(Uniform.Name.DIRECTIONAL_LIGHT));
        uniformMap.put(Uniform.Name.SHADOW_MAP, new IntegerUniform(Uniform.Name.SHADOW_MAP));
        uniformMap.put(Uniform.Name.ORTHO_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.ORTHO_PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX));
        uniformMap.put(Uniform.Name.NUM_COLS, new IntegerUniform(Uniform.Name.NUM_COLS));
        uniformMap.put(Uniform.Name.NUM_ROWS, new IntegerUniform(Uniform.Name.NUM_ROWS));
        uniformMap.put(Uniform.Name.SELECTED, new FloatUniform(Uniform.Name.SELECTED));
        uniformMap.put(Uniform.Name.BACK_COLOR, new Vector3fUniform(Uniform.Name.BACK_COLOR));

        programId = compile("data/shader/scene/");
    }
}
