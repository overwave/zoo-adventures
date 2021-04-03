package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.graphics.hud.HudElement;
import dev.overtow.math.Matrix;
import dev.overtow.math.Vector2;
import dev.overtow.math.Vector3;
import dev.overtow.math.Vector4;
import dev.overtow.util.Utils;

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
    private final DispenserSystem dispenserSystem;
    private BoxActor cursor;

    private final Vector3 cameraPosition;
    private final Vector3 cameraRotation;

    private final HudLayout hudLayout;
    private Matrix lightViewProjectionMatrix = Matrix.ofIdentity();
    private Matrix viewProjectionMatrix = Matrix.ofIdentity();
    private Vector3 lightPosition;

    public Scene(Level level) {
        generalActors = new ArrayList<>();
        boxesOnField = new ArrayList<>();

        Set<BoxType> distinctTypes = new HashSet<>();

        for (Cell cell : level.getCells()) {
            distinctTypes.add(cell.getType());
            boxesOnField.add(new BoxActor(cell.getType(), Vector2.of(cell.getX(), cell.getY())));
        }
        cursor = new BoxActor(BoxType.CORN, Vector2.ZERO);

        dispenserSystem = new DispenserSystem(new ArrayList<>(distinctTypes));
        dispenserSystem.update();

        generalActors.add(new PoolActor());

        hudLayout = new HudLayout();

        lightPosition = Vector3.ZERO;

//        cameraPosition = Vector3.of(0f, 5, 10);
//        cameraRotation = Vector3.of(15, 0, 0);
        cameraPosition = Vector3.of(0, 21, 0);
        cameraRotation = Vector3.of(90, 0, 0);
    }

    public Matrix getLightViewProjectionMatrix() {
        return lightViewProjectionMatrix;
    }

    public Matrix getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    public Vector3 getLightPosition() {
        return lightPosition;
    }

    public void update(Vector2 mousePosition) {
        lightPosition = calculateLightPosition();
        lightViewProjectionMatrix = calculateLightProjectionMatrix();
        viewProjectionMatrix = createViewProjectionMatrix();

        Vector2 reducedMousePosition = reduceMousePosition(mousePosition);
        cursor.setPosition(Vector3.of(reducedMousePosition.getX(), 0, reducedMousePosition.getY()));

        hudLayout.update();
    }

    private Vector2 reduceMousePosition(Vector2 mousePosition) {
        Vector4 rangeConvertedPosition = Vector4.of(2 * mousePosition.getX() - 1, 1 - 2 * mousePosition.getY(), 0, 1);
        Vector4 multiply = rangeConvertedPosition.multiply(viewProjectionMatrix.invert());
        return multiply.getXZ().multiply(20);    // magic constant
    }

    private Vector3 calculateLightPosition() {
        float lightHeight = 15;
        float lightDistance = 17;
        float time = Utils.getTime();
        float x = (float) Math.sin(time / 8);
        float z = (float) Math.cos(time / 8);
        return Vector3.of(lightDistance * x, lightHeight + (float) Math.sin(time), lightDistance * z);
    }

    private Matrix calculateLightProjectionMatrix() {
        Vector3 viewTarget = Vector3.of(0.5f, 0.0f, 0.5f);
        return Matrix.ofProjectionLookAt(90, 1.0f, 0.1f, 40.0f, lightPosition, viewTarget);
    }

    private Matrix createViewProjectionMatrix() {
        return Matrix.ofProjectionRotateTranslate(45, 1600.f / 900, 0.1f, 50, cameraRotation, cameraPosition.negate());

//        Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
//        viewProjectionMatrix.setPerspective((float) Math.toRadians(45), 1.0f, 0.1f, 40.0f)
//                .lookAt(scene.getLightPosition(), new Vector3f(0), UP);
    }

    public List<HudElement> getHudElements() {
        return Collections.unmodifiableList(hudLayout.getHudElements());
    }

    public List<Actor> getUsualActors() {
        // TODO maybe create smth like a view-list?
        return Stream.of(generalActors, dispenserSystem.getBoxes(), boxesOnField, List.of(cursor))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
