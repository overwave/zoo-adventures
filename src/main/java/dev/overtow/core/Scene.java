package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.graphics.hud.HudElement;
import dev.overtow.util.Utils;
import dev.overtow.util.misc.Tuple;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scene {
    private final List<Actor> generalActors;
    private final List<BoxActor> boxesOnField;
    private final WaterActor waterActors;
    private final DispenserSystem dispenserSystem;

    private final HudLayout hudLayout;
    private final Matrix4f light = new Matrix4f();
    private Vector3f lightPosition;

    public Scene(Level level) {
        generalActors = new ArrayList<>();
        boxesOnField = new ArrayList<>();

        Set<BoxType> distinctTypes = new HashSet<>();

        for (Cell cell : level.getCells()) {
            distinctTypes.add(cell.getType());
            boxesOnField.add(new BoxActor(cell.getType(), new Vector2i(cell.getX(), cell.getY())));
        }

        dispenserSystem = new DispenserSystem(new ArrayList<>(distinctTypes));
        dispenserSystem.update();

        generalActors.add(new PoolActor());
        waterActors = new WaterActor();

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
        double alpha = Utils.getTime() / 8;
        float x = (float) Math.sin(alpha);
        float z = (float) Math.cos(alpha);
        lightPosition.set(lightDistance * x, lightHeight + (float) Math.sin(alpha), lightDistance * z);
        light.setPerspective((float) Math.toRadians(90), 1.0f, 0.1f, 40.0f)
                .lookAt(lightPosition, lightLookAt, UP);

        hudLayout.update();

        float time = Utils.getTime();
        for (BoxActor actor : boxesOnField) {
            Tuple<Vector3f, Quaternionf> wavesShift = waterActors.getWavesShift(actor.getPosition(), new Vector2f(1), time);

            actor.setTemporaryTilt(wavesShift.getT(), wavesShift.getV());
        }
    }

    public List<HudElement> getHudElements() {
        return Collections.unmodifiableList(hudLayout.getHudElements());
    }

    public List<Actor> getUsualActors() {
        // TODO maybe create smth like a view-list?
        return Stream.of(generalActors, dispenserSystem.getBoxes(), boxesOnField)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<WaterActor> getWaterActors() {
        return List.of(waterActors);
    }
}
