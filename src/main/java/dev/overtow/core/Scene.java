package dev.overtow.core;

import dev.overtow.service.renderer.RendererImpl;
import dev.overtow.service.shader.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.items.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final Shader sceneShader;
    private final Shader depthShader;

    private final Map<Mesh, List<GameItem>> meshMap;

    private final ArrayList<Actor> actors;

    private SceneLight sceneLight;

    // scene uniforms
    private final Uniform<Matrix4f> projectionMatrix = new Uniform<>("projectionMatrix");
    private final Uniform<Matrix4f> modelViewMatrix = new Uniform<>("modelViewMatrix");
    private final Uniform<Integer> textureSampler = new Uniform<>("textureSampler");
    private final Uniform<Integer> normalMap = new Uniform<>("normalMap");
    private final MaterialUniform material = new MaterialUniform("material");
    private final Uniform<Float> specularPower = new Uniform<>("specularPower");
    private final Uniform<Vector3f> ambientLight = new Uniform<>("ambientLight");
    private final ArrayUniform<PointLightUniform> pointLights =
            new ArrayUniform<>("pointLights", RendererImpl.MAX_POINT_LIGHTS, PointLightUniform::new);
    private final ArrayUniform<SpotLightUniform> spotLights =
            new ArrayUniform<>("spotLights", RendererImpl.MAX_SPOT_LIGHTS, SpotLightUniform::new);
    private final DirectionalLightUniform directionalLight = new DirectionalLightUniform("directionalLight");
    private final Uniform<Integer> shadowMap = new Uniform<>("shadowMap");
    private final Uniform<Matrix4f> orthoProjectionMatrix = new Uniform<>("orthoProjectionMatrix");
    private final Uniform<Matrix4f> modelLightViewMatrix = new Uniform<>("modelLightViewMatrix");
    private final Uniform<Matrix4f> jointsMatrix = new Uniform<>("jointsMatrix");
    private final Uniform<Integer> numCols = new Uniform<>("numCols");
    private final Uniform<Integer> numRows = new Uniform<>("numRows");
    private final Uniform<Float> selected = new Uniform<>("selected");


    public Scene(ShaderCompiler shaderCompiler) {
        sceneShader = shaderCompiler.compile("shaders/scene/",
                projectionMatrix, modelViewMatrix, textureSampler, normalMap, material, specularPower, ambientLight,
                pointLights, spotLights, directionalLight, shadowMap, orthoProjectionMatrix, modelLightViewMatrix,
                jointsMatrix, numCols, numRows, selected);
        depthShader = shaderCompiler.compile("shaders/depth/",
                jointsMatrix, modelLightViewMatrix, orthoProjectionMatrix);

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
