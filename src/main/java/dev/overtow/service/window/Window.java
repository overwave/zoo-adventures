package dev.overtow.service.window;

import org.joml.Vector2f;

public interface Window {
    int getWidth();

    int getHeight();

    float getAspectRatio();

    long getWindowHandle();

    Vector2f getMousePosition();

    boolean windowShouldClose();

    boolean isKeyPressed(int keyCode);

    void pollEvents();

    void swapBuffers();
}
