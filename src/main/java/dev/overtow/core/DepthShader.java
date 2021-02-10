package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Matrix4fUniform;
import dev.overtow.core.shader.uniform.Uniform;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.graph.ShadowMap;
import org.lwjglb.engine.graph.Transformation;
import org.lwjglb.engine.graph.lights.DirectionalLight;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class DepthShader extends ShaderProgram {

    private final SceneLight sceneLight;
    private final ShadowMap shadowMapKek;
    private final Transformation transformationKek;

    public DepthShader(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
        uniformMap.put(Uniform.Name.JOINTS_MATRIX, new Matrix4fUniform(Uniform.Name.JOINTS_MATRIX));
        uniformMap.put(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX, new Matrix4fUniform(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX));
        uniformMap.put(Uniform.Name.ORTHO_PROJECTION_MATRIX, new Matrix4fUniform(Uniform.Name.ORTHO_PROJECTION_MATRIX));

        programId = compile("data/shader/depth/");
        shadowMapKek = new ShadowMap();
        transformationKek = new Transformation();
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMapKek.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glUseProgram(programId);
    }

    @Override
    public void unbind() {
        glUseProgram(0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void draw() {
        DirectionalLight light = sceneLight.getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformationKek.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformationKek.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        orthoProjectionMatrix.setValue(orthoProjMatrix);

        renderMeshes(depthShader, null, lightViewMatrix);
    }
}
