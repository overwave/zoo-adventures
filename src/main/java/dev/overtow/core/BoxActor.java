package dev.overtow.core;

import dev.overtow.graphics.draw.BoxMesh;
import dev.overtow.graphics.draw.BoxType;
import org.joml.Vector2i;

public class BoxActor implements Actor {
    private final BoxMesh mesh;
    private final BoxType boxType;
    private final Vector2i position;

    public BoxActor(BoxMesh mesh, Vector2i position) {
        this.mesh = mesh;
        this.position = new Vector2i(position);
        this.boxType = BoxType.BANANA;
    }

    @Override
    public void update() {
        // NOP
    }

    @Override
    public void draw() {
        mesh.draw();
    }
}
