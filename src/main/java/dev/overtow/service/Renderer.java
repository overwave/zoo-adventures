package dev.overtow.service;

import org.lwjglb.engine.Scene;
import org.lwjglb.engine.graph.Camera;

public interface Renderer {
    void cleanup();

    void render(Window windowKek, Camera camera, Scene scene);

    void init() throws Exception;
}
