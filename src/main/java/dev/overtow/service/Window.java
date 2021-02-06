package dev.overtow.service;

import org.joml.Matrix4f;

public interface Window {
    int getWidth();

    int getHeight();

    String getTitle();

    long getWindowHandle();

    boolean windowShouldClose();

    boolean isKeyPressed(int keyCode);

    void update();

    void restoreState();

    Matrix4f getProjectionMatrix();

    void setWindowTitle(String title);

    Matrix4f updateProjectionMatrix();
}
