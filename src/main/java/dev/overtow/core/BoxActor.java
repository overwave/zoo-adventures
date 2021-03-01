package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class BoxActor implements Actor {
    //    private final BoxMesh mesh;
    private final BoxType boxType;
    private final Vector3f position;
    private float scale;
    private final Quaternionf rotation;

    public BoxActor(/*BoxMesh mesh,*/ Vector2i position) {
//        this.mesh = mesh;
        this.position = new Vector3f(position.x(), 0, position.y());
        this.boxType = BoxType.BANANA;
        this.scale = 1;
        this.rotation = new Quaternionf();
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public Quaternionf getRotation() {
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return Mesh.Id.CUBE;
    }
}
