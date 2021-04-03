package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.math.Box;
import dev.overtow.math.Quaternion;
import dev.overtow.math.Vector2;
import dev.overtow.math.Vector3;
import dev.overtow.math.Vector4;

public class BoxActor implements Actor {
    private final BoxType boxType;
    private Vector3 position;
    private final Vector3 scale;
    private final Quaternion rotation;
    private boolean selected;
    private final Box hitBox;

    public BoxActor(BoxType boxType, Vector2 position) {
        this.position = Vector3.of(position.getX() - 4.5f, 0, position.getY() - 4.5f);
        this.boxType = boxType;
        this.scale = Vector3.ONE;
        this.rotation = Quaternion.of();
        this.hitBox = Box.of(this.position, this.position.plus(Vector3.ONE));
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3 getPosition() {
        return position;
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
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return boxType.getMeshId();
    }

    @Override
    public Vector4 getBackgroundColor() {
        return boxType.getColor();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    public Box getHitBox() {
        return hitBox;
    }
}
