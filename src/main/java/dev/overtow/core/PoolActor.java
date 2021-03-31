package dev.overtow.core;

import dev.overtow.math.Quaternion;
import dev.overtow.math.Vector3;
import dev.overtow.math.Vector4;
import dev.overtow.util.Utils;

public class PoolActor implements Actor {
    private final Quaternion rotation;

    public PoolActor() {
        this.rotation =  Quaternion.of();
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3 getPosition() {
        return Vector3.ZERO;
    }

    @Override
    public Vector3 getScale() {
        return Vector3.ONE;
    }

    @Override
    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return Mesh.Id.POOL;
    }

    @Override
    public Vector4 getBackgroundColor() {
        return Utils.NO_BACKGROUND_COLOR;
    }
}
