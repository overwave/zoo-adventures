package dev.overtow.core;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WaterActor implements Actor {
    private final Quaternionf rotation;

    public WaterActor() {
        this.rotation = new Quaternionf();
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(0, 0.2f, 0);
    }

    @Override
    public float getScale() {
        return 2;
    }

    @Override
    public Quaternionf getRotation() {
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return Mesh.Id.WATER;
    }
}
