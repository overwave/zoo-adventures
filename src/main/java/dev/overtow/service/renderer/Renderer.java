package dev.overtow.service.renderer;

import dev.overtow.service.window.Window;
import dev.overtow.core.Scene;
import org.lwjglb.engine.graph.Camera;

public interface Renderer {
    void cleanup();

    void render(Window windowKek, Camera camera, Scene scene);

    void init() throws Exception;
}
