package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.service.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.Texture;
import org.lwjglb.engine.graph.Transformation;
import org.lwjglb.engine.graph.lights.DirectionalLight;
import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;
import org.lwjglb.engine.items.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public class Scene {
    private final ShaderProgram sceneShader;
    private final ShaderProgram depthShader;

    private final Map<Mesh, List<GameItem>> meshMap;
    private final ArrayList<Actor> actors;

    private final Window window;
    private final Camera camera;
    private SceneLight sceneLight;
    private final Transformation transformation;
    private final float specularPower;

    // shadow stuff
    private ShadowBuffer shadowBuffer;
    public static final int SHADOW_CASCADES_NUMBER = 3;

    public static final float[] CASCADE_SPLITS = new float[]{Window.Z_FAR / 20.0f, Window.Z_FAR / 10.0f, Window.Z_FAR};
    private List<ShadowCascade> shadowCascades;


    public Scene(Window window, Camera camera, SceneLight sceneLight) {
        depthShader = new ShadowShader();
        sceneShader = new SceneShader();

        actors = new ArrayList<>();
        meshMap = new HashMap<>();

        transformation = new Transformation();
        specularPower = 10f;
        this.window = window;
        this.camera = camera;
        this.sceneLight = sceneLight;

        // shadow stuff
        shadowCascades = new ArrayList<>();
        shadowBuffer = new ShadowBuffer();
        float zNear = Window.Z_NEAR;
        for (int i = 0; i < SHADOW_CASCADES_NUMBER; i++) {
            ShadowCascade shadowCascade = new ShadowCascade(zNear, CASCADE_SPLITS[i]);
            shadowCascades.add(shadowCascade);
            zNear = CASCADE_SPLITS[i];
        }
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public void setGameItems(List<GameItem> gameItems) {
        // Create a map of meshes to speed up rendering
        int numGameItems = gameItems != null ? gameItems.size() : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems.get(i);
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                List<GameItem> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
                list.add(gameItem);
            }
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void update() {
        actors.forEach(Actor::update);

        // shadow shit
//        SceneLight sceneLight = scene.getSceneLight();
        DirectionalLight directionalLight = sceneLight != null ? sceneLight.getDirectionalLight() : null;
        for (int i = 0; i < SHADOW_CASCADES_NUMBER; i++) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            shadowCascade.update(window, camera.getViewMatrix(), directionalLight);
        }
    }

    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        // TODO add frustum filtering!

        glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
        glViewport(0, 0, ShadowBuffer.SHADOW_MAP_WIDTH, ShadowBuffer.SHADOW_MAP_HEIGHT);

        depthShader.draw(shader -> {
            for (int i = 0; i < SHADOW_CASCADES_NUMBER; i++) {
                ShadowCascade shadowCascade = shadowCascades.get(i);

                shader.set(Uniform.Name.ORTHO_PROJECTION_MATRIX, shadowCascade.getOrthoProjMatrix());
                shader.set(Uniform.Name.LIGHT_VIEW_MATRIX, shadowCascade.getLightViewMatrix());

                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
                glClear(GL_DEPTH_BUFFER_BIT);

                renderNonInstancedMeshes(shader, transformation);
            }
        });
        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        window.updateProjectionMatrix();


        glViewport(0, 0, window.getWidth(), window.getHeight());
        sceneShader.draw(shader -> {
            Matrix4f viewMatrix = camera.getViewMatrix();
            Matrix4f projectionMatrix = window.getProjectionMatrix();
//            shader.set(Uniform.Name.VIEW_MATRIX, viewMatrix);
            shader.set(Uniform.Name.PROJECTION_MATRIX, projectionMatrix);

            for (int i = 0; i < SHADOW_CASCADES_NUMBER; i++) {
                ShadowCascade shadowCascade = shadowCascades.get(i);
                shader.set(Uniform.Name.ORTHO_PROJECTION_MATRIX, i, shadowCascade.getOrthoProjMatrix());
                shader.set(Uniform.Name.CASCADE_FAR_PLANES, i, CASCADE_SPLITS[i]);
                shader.set(Uniform.Name.LIGHT_VIEW_MATRIX, i, shadowCascade.getLightViewMatrix());
            }

            renderLights(shader, viewMatrix, sceneLight);

            shader.set(Uniform.Name.TEXTURE_SAMPLER, 0);
            shader.set(Uniform.Name.NORMAL_MAP, 1);
            int offset = 2;
            for (int i = 0; i < SHADOW_CASCADES_NUMBER; i++) {
                shader.set(Uniform.Name.SHADOW_MAP, i, offset + i);
            }
            renderMeshes(shader/*, viewMatrix, lightViewMatrix*/);
        });
    }

    private void renderNonInstancedMeshes(ShaderProgram shader, Transformation transformation) {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.renderList(meshMap.get(mesh), gameItem -> {
                        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                        shader.set(Uniform.Name.MODEL_NON_INSTANCED_MATRIX, modelMatrix);
                    }
            );
        }
    }

    private void renderMeshes(ShaderProgram shader) {
        for (Mesh mesh : meshMap.keySet()) {
            shader.set(Uniform.Name.MATERIAL, mesh.getMaterial());
            shader.set(Uniform.Name.BACK_COLOR, new Vector4f(0.9019608f, 1.0f, 0.1764706f, 1));

            for (int i = 0; i < SHADOW_CASCADES_NUMBER; i++) {
                glActiveTexture(GL_TEXTURE2 + i);
                glBindTexture(GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i]);
            }

            mesh.renderList(meshMap.get(mesh), gameItem -> {
                        shader.set(Uniform.Name.SELECTED, gameItem.isSelected() ? 1.0f : 0.0f);
                        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                        shader.set(Uniform.Name.MODEL_NON_INSTANCED_MATRIX, modelMatrix);
                    }
            );
        }
    }

    private void renderLights(ShaderProgram shader, Matrix4f viewMatrix, SceneLight sceneLight) {
        shader.set(Uniform.Name.AMBIENT_LIGHT, sceneLight.getAmbientLight());
        shader.set(Uniform.Name.SPECULAR_POWER, specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            shader.set(Uniform.Name.POINT_LIGHTS, i, currPointLight);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            shader.set(Uniform.Name.SPOT_LIGHTS, i, currSpotLight);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(new Vector3f(6, 10, 4), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shader.set(Uniform.Name.DIRECTIONAL_LIGHT, currDirLight);
    }
}
