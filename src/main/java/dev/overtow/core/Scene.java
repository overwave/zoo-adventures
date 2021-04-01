package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.graphics.hud.HudElement;
import dev.overtow.math.Matrix;
import dev.overtow.math.Quaternion;
import dev.overtow.math.Vector2;
import dev.overtow.math.Vector3;
import dev.overtow.util.Utils;
import dev.overtow.util.misc.Tuple;

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
    private final WaterActor water;
    private final DispenserSystem dispenserSystem;

    private final HudLayout hudLayout;
    private Matrix viewProjection = Matrix.ofIdentity();
    private Vector3 lightPosition;

    public Scene(Level level) {
        generalActors = new ArrayList<>();
        boxesOnField = new ArrayList<>();

        Set<BoxType> distinctTypes = new HashSet<>();

        for (Cell cell : level.getCells()) {
            distinctTypes.add(cell.getType());
            boxesOnField.add(new BoxActor(cell.getType(), Vector2.of(cell.getX(), cell.getY())));
        }

        dispenserSystem = new DispenserSystem(new ArrayList<>(distinctTypes));
        dispenserSystem.update();

        generalActors.add(new PoolActor());
        water = new WaterActor();

        hudLayout = new HudLayout();

        lightPosition = Vector3.ZERO;
    }

    public Matrix getViewProjection() {
        return viewProjection;
    }

    public Vector3 getLightPosition() {
        return lightPosition;
    }

    public void update() {
        float lightHeight = 15;
        float lightDistance = 17;
        float time = Utils.getTime();
        float x = (float) Math.sin(time / 8);
        float z = (float) Math.cos(time / 8);
        lightPosition = Vector3.of(lightDistance * x, lightHeight + (float) Math.sin(time), lightDistance * z);
        Vector3 viewTarget = Vector3.of(0.5f, 0.0f, 0.5f);

        viewProjection = Matrix.ofProjectionLookAt(90, 1.0f, 0.1f, 40.0f, lightPosition, viewTarget);

        hudLayout.update();
        water.update();

        for (BoxActor actor : boxesOnField) {
            Tuple<Vector3, Quaternion> wavesShift = water.getWavesShift(actor.getPosition(), Vector2.of(1), time);

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

    public List<WaterActor> getWater() {
        return List.of(water);
    }
}
