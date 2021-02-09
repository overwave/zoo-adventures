package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.*;
import dev.overtow.service.renderer.RendererImpl;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;
import org.lwjglb.engine.items.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final ShaderProgram sceneShader;
    private final ShaderProgram depthShader;

    private final Map<Mesh, List<GameItem>> meshMap;

    private final ArrayList<Actor> actors;

    private SceneLight sceneLight;

    // scene uniforms
    private final Matrix4fUniform projectionMatrix = new Matrix4fUniform("projectionMatrix");
    private final Matrix4fUniform modelViewMatrix = new Matrix4fUniform("modelViewMatrix");
    private final IntegerUniform textureSampler = new IntegerUniform("textureSampler");
    private final IntegerUniform normalMap = new IntegerUniform("normalMap");
    private final MaterialUniform material = new MaterialUniform("material");
    private final FloatUniform specularPower = new FloatUniform("specularPower");
    private final Vector3fUniform ambientLight = new Vector3fUniform("ambientLight");
    private final ArrayUniform<PointLight> pointLights = new ArrayUniform<>("pointLights", RendererImpl.MAX_POINT_LIGHTS, PointLightUniform::new);
    private final ArrayUniform<SpotLight> spotLights = new ArrayUniform<>("spotLights", RendererImpl.MAX_SPOT_LIGHTS, SpotLightUniform::new);
    private final DirectionalLightUniform directionalLight = new DirectionalLightUniform("directionalLight");
    private final IntegerUniform shadowMap = new IntegerUniform("shadowMap");
    private final Matrix4fUniform orthoProjectionMatrix = new Matrix4fUniform("orthoProjectionMatrix");
    private final Matrix4fUniform modelLightViewMatrix = new Matrix4fUniform("modelLightViewMatrix");
    private final Matrix4fUniform jointsMatrix = new Matrix4fUniform("jointsMatrix");
    private final IntegerUniform numCols = new IntegerUniform("numCols");
    private final IntegerUniform numRows = new IntegerUniform("numRows");
    private final FloatUniform selected = new FloatUniform("selected");
    private final Vector3fUniform backColor = new Vector3fUniform("backColor");

    // depth uniforms
    private final Matrix4fUniform jointsMatrixDepth = new Matrix4fUniform("jointsMatrix");
    private final Matrix4fUniform modelLightViewMatrixDepth = new Matrix4fUniform("modelLightViewMatrix");
    private final Matrix4fUniform orthoProjectionMatrixDepth = new Matrix4fUniform("orthoProjectionMatrix");


    public Scene() {
        sceneShader = new ShaderProgram("data/shader/scene/",
                projectionMatrix, modelViewMatrix, textureSampler, normalMap, material, specularPower, ambientLight,
                pointLights, spotLights, directionalLight, shadowMap, orthoProjectionMatrix, modelLightViewMatrix,
                jointsMatrix, numCols, numRows, selected, backColor);
        depthShader = new ShaderProgram("data/shader/depth/",
                jointsMatrixDepth, modelLightViewMatrixDepth, orthoProjectionMatrixDepth);

        actors = new ArrayList<>();

        meshMap = new HashMap<>();
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public void setGameItems(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems[i];
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

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public void update() {
        actors.forEach(Actor::update);
    }

    public void draw() {
        sceneShader.execute(() -> {

        });
//        shader.bind();
//
//        Matrix4f projectionMatrix = windowKek.getProjectionMatrix();
//        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
//        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
//        sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
//        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();
//        Matrix4f viewMatrix = camera.getViewMatrix();
//
//        SceneLight sceneLight = scene.getSceneLight();
//        renderLights(viewMatrix, sceneLight);
//
//        sceneShaderProgram.setUniform("textureSampler", 0);
//        sceneShaderProgram.setUniform("normalMap", 1);
//        sceneShaderProgram.setUniform("shadowMap", 2);
//
//        renderMeshes(scene, sceneShaderProgram, viewMatrix, lightViewMatrix);
//
//        sceneShaderProgram.unbind();
    }
}
