package dev.overtow.service.window;

import dev.overtow.math.Vector2;

public interface Window {
    int getWidth();

    int getHeight();

    float getAspectRatio();

    long getWindowHandle();

    Vector2 getMousePosition();

    boolean windowShouldClose();

    boolean isKeyPressed(int keyCode);

    void pollEvents();

    void swapBuffers();

    Vector2 getSize();

    void setTitle(String title);
}
