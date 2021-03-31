package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.math.Quaternion;
import dev.overtow.math.Vector3;
import dev.overtow.math.Vector4;
import org.joml.Vector2i;

public class BoxActor implements Actor {
    private final BoxType boxType;
    private Vector3 position;
    private final Vector3 scale;
    private final Quaternion rotation;
    private Vector3 temporaryPositionOffset;
    private Quaternion temporaryRotation;

    public BoxActor(BoxType boxType, Vector2i position) {
        this.position = Vector3.of(position.x() - 4.5f, 0, position.y() - 4.5f);
        this.temporaryPositionOffset = Vector3.of();
        this.boxType = boxType;
        this.scale = Vector3.of(1);
        this.rotation = Quaternion.of();
        this.temporaryRotation = Quaternion.of();
    }

    public void setTemporaryTilt(Vector3 tempPositionOffset, Quaternion tempRotation) {
        temporaryPositionOffset = tempPositionOffset;
        temporaryRotation = tempRotation;
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3 getPosition() {
        return position.plus(temporaryPositionOffset);
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    @Override
    public Vector3 getScale() {
        return scale;
    }

    @Override
    public Quaternion getRotation() {
        return rotation.multiply(temporaryRotation);
    }

    @Override
    public Mesh.Id getMeshId() {
        return boxType.getMeshId();
    }

    @Override
    public Vector4 getBackgroundColor() {
        return boxType.getColor();
    }
}
