package dev.overtow.core;

import dev.overtow.graphics.hud.HudElement;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scene {
    private final ArrayList<Actor> actors;
    private final HudLayout hudLayout;
    //    private final BoxActor lightBox;
    private final Matrix4f light = new Matrix4f();
    private Vector3f lightPosition;

    public Scene() {
        actors = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            actors.add(new BoxActor(new Vector2i(-7 + i / 10, -4 + i % 10)));   // left
            actors.add(new BoxActor(new Vector2i(-4 + i % 10, -7 + i / 10)));   // top
            actors.add(new BoxActor(new Vector2i(6 + i / 10, -4 + i % 10)));    // right
            actors.add(new BoxActor(new Vector2i(-4 + i % 10, 6 + i / 10)));   // bottom
        }
//        lightBox = new BoxActor(new Vector2i(0, 0));
//        actors.add(lightBox);
        actors.add(new PoolActor());
        actors.add(new WaterActor());

        hudLayout = new HudLayout();

        lightPosition = new Vector3f();
    }

    public Matrix4f getLight() {
        return light;
    }

    public Vector3f getLightPosition() {
        return lightPosition;
    }

    public void update() {
        float lightHeight = 15;
        float lightDistance = 17;
        lightPosition = new Vector3f(6.0f, lightHeight, 6.0f);
        Vector3f lightLookAt = new Vector3f(0.5f, 0.0f, 0.5f);
        Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
        double alpha = System.currentTimeMillis() / 1000.0 * 0.5;
        float x = (float) Math.sin(alpha);
        float z = (float) Math.cos(alpha);
        lightPosition.set(lightDistance * x, lightHeight + (float) Math.sin(alpha), lightDistance * z);
//        lightBox.setPosition(lightPosition);
        light.setPerspective((float) Math.toRadians(90), 1.0f, 0.1f, 40.0f)
                .lookAt(lightPosition, lightLookAt, UP);

        hudLayout.update();
    }

    public List<Actor> getActors() {
        return Collections.unmodifiableList(actors);
    }

    public List<HudElement> getHudElements() {
        return Collections.unmodifiableList(hudLayout.getHudElements());
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }
}
