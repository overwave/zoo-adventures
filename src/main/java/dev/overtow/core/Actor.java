package dev.overtow.core;

import dev.overtow.math.Quaternion;
import dev.overtow.math.Vector3;
import dev.overtow.math.Vector4;

public interface Actor {
    void update();

    Vector3 getPosition();

    Vector3 getScale();

    Quaternion getRotation();

    Mesh.Id getMeshId();

    Vector4 getBackgroundColor();
}
