package dev.overtow.service.renderer;

import dev.overtow.service.window.Window;
import org.lwjglb.engine.Scene;
import org.lwjglb.engine.graph.Camera;

public interface Renderer {
    void cleanup();

    void render(Window windowKek, Camera camera, Scene scene);

    void init() throws Exception;
}
