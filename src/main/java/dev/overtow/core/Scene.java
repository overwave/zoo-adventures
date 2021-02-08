package dev.overtow.core;

import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.items.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private final Map<Mesh, List<GameItem>> meshMap;

    private final ArrayList<Actor> actors;

    private SceneLight sceneLight;

    public Scene() {
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
}
