package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BoxActor implements Actor {
    //    private final BoxMesh mesh;
    private final BoxType boxType;
    private final Vector3f position;
    private float scale;
    private final Quaternionf rotation;
    private Vector3f temporaryPositionOffset;
    private Quaternionf temporaryRotation;

    public BoxActor(BoxType boxType, Vector2i position) {
//        this.mesh = mesh;
        this.position = new Vector3f(position.x() - 4.5f, 0, position.y() - 4.5f);
        this.temporaryPositionOffset = new Vector3f(0);
        this.boxType = boxType;
        this.scale = 1;
        this.rotation = new Quaternionf();
        this.temporaryRotation = new Quaternionf();
    }

    public void setTemporaryTilt(Vector3f tempPositionOffset, Quaternionf tempRotation) {
        temporaryPositionOffset = new Vector3f(tempPositionOffset);
        temporaryRotation = tempRotation;
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(position).add(temporaryPositionOffset);
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    @Override
    public Vector3f getScale() {
        return new Vector3f(scale);
    }

    @Override
    public Quaternionf getRotation() {
        Quaternionf quaternion = new Quaternionf(rotation);
        return quaternion.mul(temporaryRotation);
    }

    @Override
    public Mesh.Id getMeshId() {
        return boxType.getMeshId();
    }

    @Override
    public Vector4f getBackgroundColor() {
        return boxType.getColor();
    }
}
