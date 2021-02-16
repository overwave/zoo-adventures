package dev.overtow.service.window;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public interface Window {
    float FOV = (float) Math.toRadians(60.0f);
    float Z_NEAR = 0.01f;
    float Z_FAR = 1000.f;

    int getWidth();

    int getHeight();

    String getTitle();

    long getWindowHandle();

    Vector2f getMousePosition();

    boolean windowShouldClose();

    boolean isKeyPressed(int keyCode);

    void update();

    void restoreState();

    Matrix4f getProjectionMatrix();

    void setWindowTitle(String title);

    Matrix4f updateProjectionMatrix();

    void cleanup();
}
