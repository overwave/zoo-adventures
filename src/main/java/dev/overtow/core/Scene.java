package dev.overtow.core;

import dev.overtow.graphics.hud.HudElement;
import dev.overtow.util.Utils;
import dev.overtow.util.misc.Tuple;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scene {
    //    private final List<Actor> actors;
    private final List<Actor> generalActors;
    private final List<BoxActor> boxesOnField;
    private final List<BoxActor> boxesInDispensers;
    private final WaterActor waterActors;
//    private final WaterActor water;

    private final HudLayout hudLayout;
    //    private final BoxActor lightBox;
    private final Matrix4f light = new Matrix4f();
    private Vector3f lightPosition;

    public Scene() {
        generalActors = new ArrayList<>();
        boxesInDispensers = new ArrayList<>();
        boxesOnField = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            boxesInDispensers.add(new BoxActor(new Vector2i(-7 + i / 10, -4 + i % 10)));   // left
            boxesInDispensers.add(new BoxActor(new Vector2i(-4 + i % 10, -7 + i / 10)));   // top
            boxesInDispensers.add(new BoxActor(new Vector2i(6 + i / 10, -4 + i % 10)));    // right
            boxesInDispensers.add(new BoxActor(new Vector2i(-4 + i % 10, 6 + i / 10)));   // bottom
        }

        boxesOnField.add(new BoxActor(new Vector2i(-2, 3)));
        boxesOnField.add(new BoxActor(new Vector2i(1, -4)));
//        lightBox = new BoxActor(new Vector2i(0, 0));
//        actors.add(lightBox);
        generalActors.add(new PoolActor());
        waterActors = new WaterActor();

//        actors.addAll(usualActors);
//        actors.addAll(waterActors);

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
        double alpha = Utils.getTime() / 3;
        float x = (float) Math.sin(alpha);
        float z = (float) Math.cos(alpha);
        lightPosition.set(lightDistance * x, lightHeight + (float) Math.sin(alpha), lightDistance * z);
        light.setPerspective((float) Math.toRadians(90), 1.0f, 0.1f, 40.0f)
                .lookAt(lightPosition, lightLookAt, UP);

        hudLayout.update();

        float time = Utils.getTime();
        for (BoxActor actor : boxesOnField) {
            Tuple<Vector3f, Quaternionf> wavesShift = waterActors.getWavesShift(actor.getPosition(), time);

            actor.setTemporaryTilt(wavesShift.getT(), wavesShift.getV());
        }
    }

    public List<HudElement> getHudElements() {
        return Collections.unmodifiableList(hudLayout.getHudElements());
    }

    public List<Actor> getUsualActors() {
        // TODO maybe create smth like a view-list?
        return Stream.of(generalActors, boxesInDispensers, boxesOnField)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<WaterActor> getWaterActors() {
        return List.of(waterActors);
    }
}
