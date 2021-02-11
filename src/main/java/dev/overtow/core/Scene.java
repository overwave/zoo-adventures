package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.service.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.graph.*;
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
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Scene {
    private final ShaderProgram sceneShader;
    private final ShaderProgram depthShader;

    private final Map<Mesh, List<GameItem>> meshMap;
    private final ArrayList<Actor> actors;

    private final Window window;
    private final Camera camera;
    private SceneLight sceneLight;
    private final Transformation transformation;
    private ShadowMap shadowMap;
    private final float specularPower;


    public Scene(Window window, Camera camera, SceneLight sceneLight) {
        depthShader = new DepthShader();
        sceneShader = new SceneShader();

        actors = new ArrayList<>();
        meshMap = new HashMap<>();

        transformation = new Transformation();
        specularPower = 10f;
        this.window = window;
        this.camera = camera;
        this.sceneLight = sceneLight;
        this.shadowMap = new ShadowMap();
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
    }

    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);


        // depthShader
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        depthShader.draw(() -> {
//            DirectionalLight light = sceneLight.getDirectionalLight();
//            Vector3f lightDirection = light.getDirection();
//
//            float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
//            float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
//            float lightAngleZ = 0;
//            Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
//            DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
//            Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);
//
//            depthShader.set(Uniform.Name.ORTHO_PROJECTION_MATRIX, orthoProjMatrix);
//
//            renderMeshesDepth(depthShader, null);
        });
        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        window.updateProjectionMatrix();


        glViewport(0, 0, window.getWidth(), window.getHeight());
        sceneShader.draw(() -> {
            Matrix4f projectionMatrix = window.getProjectionMatrix();
            sceneShader.set(Uniform.Name.PROJECTION_MATRIX, projectionMatrix);
            Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
            sceneShader.set(Uniform.Name.ORTHO_PROJECTION_MATRIX, orthoProjMatrix);
            Matrix4f lightViewMatrix = transformation.getLightViewMatrix();
            Matrix4f viewMatrix = camera.getViewMatrix();

            renderLights(sceneShader, viewMatrix, sceneLight);

            sceneShader.set(Uniform.Name.TEXTURE_SAMPLER, 0);
            sceneShader.set(Uniform.Name.NORMAL_MAP, 1);
            sceneShader.set(Uniform.Name.SHADOW_MAP, 2);

            renderMeshes(sceneShader, viewMatrix, lightViewMatrix);
        });
    }

    private void renderMeshesDepth(ShaderProgram shader, Matrix4f viewMatrix) {
        // Render each mesh with the associated game Items
//        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : meshMap.keySet()) {
            if (viewMatrix != null) {
                shader.set(Uniform.Name.MATERIAL, mesh.getMaterial());
                shader.set(Uniform.Name.BACK_COLOR, new Vector4f(0.9019608f, 1.0f, 0.1764706f, 1));
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            }
        }
    }

    private void renderMeshes(ShaderProgram shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        // Render each mesh with the associated game Items
//        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
//        Map<Mesh, List<GameItem>> mapMeshes = meshMap;
        for (Mesh mesh : meshMap.keySet()) {
            if (viewMatrix != null) {
                shader.set(Uniform.Name.MATERIAL, mesh.getMaterial());
                shader.set(Uniform.Name.BACK_COLOR, new Vector4f(0.9019608f, 1.0f, 0.1764706f, 1));
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            }

            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                shader.set(Uniform.Name.NUM_COLS, text.getNumCols());
                shader.set(Uniform.Name.NUM_ROWS, text.getNumRows());
            }

            mesh.renderList(meshMap.get(mesh), (GameItem gameItem) -> {
                        shader.set(Uniform.Name.SELECTED, gameItem.isSelected() ? 1.0f : 0.0f);
                        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                        if (viewMatrix != null) {
                            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                            shader.set(Uniform.Name.MODEL_VIEW_MATRIX, modelViewMatrix);
                        }
                        Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
                        shader.set(Uniform.Name.MODEL_LIGHT_VIEW_MATRIX, modelLightViewMatrix);
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
