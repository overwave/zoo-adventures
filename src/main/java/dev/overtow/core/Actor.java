package dev.overtow.core;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface Actor {
    void update();

    Vector3f getPosition();

    Vector3f getScale();

    Quaternionf getRotation();

    Mesh.Id getMeshId();
}
