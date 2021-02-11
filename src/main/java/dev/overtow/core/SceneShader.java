package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.*;
import dev.overtow.service.renderer.RendererImpl;
import dev.overtow.service.window.Window;
import org.joml.Matrix4f;
import org.lwjglb.engine.SceneLight;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class SceneShader extends ShaderProgram {

    public SceneShader() {
        uniformMap.put(Uniform.Name.PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.PROJECTION_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_VIEW_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_VIEW_MATRIX));
        uniformMap.put(Uniform.Name.TEXTURE_SAMPLER, new IntegerUniform(Uniform.Name.TEXTURE_SAMPLER));
        uniformMap.put(Uniform.Name.NORMAL_MAP, new IntegerUniform(Uniform.Name.NORMAL_MAP));
        uniformMap.put(Uniform.Name.MATERIAL, new MaterialUniform(Uniform.Name.MATERIAL));
        uniformMap.put(Uniform.Name.SPECULAR_POWER, new FloatUniform(Uniform.Name.SPECULAR_POWER));
        uniformMap.put(Uniform.Name.AMBIENT_LIGHT, new Vector3fUniform(Uniform.Name.AMBIENT_LIGHT));
        uniformMap.put(Uniform.Name.POINT_LIGHTS, new ArrayUniform<>(Uniform.Name.POINT_LIGHTS, RendererImpl.MAX_POINT_LIGHTS, PointLightUniform::new));
        uniformMap.put(Uniform.Name.SPOT_LIGHTS, new ArrayUniform<>(Uniform.Name.SPOT_LIGHTS, RendererImpl.MAX_SPOT_LIGHTS, SpotLightUniform::new));
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
