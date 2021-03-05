package dev.overtow.core;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PoolActor implements Actor {
    private final Quaternionf rotation;

    public PoolActor() {
        this.rotation = new Quaternionf();
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(0);
    }

    @Override
    public Vector3f getScale() {
        return new Vector3f(1);
    }

    @Override
    public Quaternionf getRotation() {
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return Mesh.Id.POOL;
    }
}
