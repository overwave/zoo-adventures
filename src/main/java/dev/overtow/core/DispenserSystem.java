package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.math.Vector2;
import dev.overtow.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DispenserSystem {
    private static final int DISPENSERS_IN_A_ROW = 10;

    private final List<Dispenser> dispensers;
    private List<BoxActor> cachedBoxes;

    public DispenserSystem(List<BoxType> boxTypes) {
        dispensers = new ArrayList<>(40);
        dispensers.addAll(createDispensers(boxTypes, Vector2.of(-1, 9), Vector2.of(-1, 0)));
        dispensers.addAll(createDispensers(boxTypes, Vector2.of(10, 0), Vector2.of(1, 0)));
        dispensers.addAll(createDispensers(boxTypes, Vector2.of(0, -1), Vector2.of(0, -1)));
        dispensers.addAll(createDispensers(boxTypes, Vector2.of(9, 10), Vector2.of(0, 1)));

        cachedBoxes = new ArrayList<>();
    }

    public void update(Vector3 cameraPosition, Vector3 mouseDirection) {
        cachedBoxes.clear();
        for (Dispenser dispenser : dispensers) {
            dispenser.checkSelection(cameraPosition, mouseDirection);
            cachedBoxes.addAll(dispenser.getBoxes());
        }
    }

    public List<BoxActor> getBoxes() {
        return Collections.unmodifiableList(cachedBoxes);
    }

    private List<Dispenser> createDispensers(List<BoxType> boxTypes, Vector2 from, Vector2 dispenserDirection) {
        List<Dispenser> dispenserRow = new ArrayList<>(10);
        Vector2 cursor = from;
        Vector2 rowDirection = dispenserDirection.rotate90deg();

        for (int i = 0; i < DISPENSERS_IN_A_ROW; i++) {
            dispenserRow.add(new Dispenser(boxTypes, cursor, dispenserDirection));
            cursor = cursor.plus(rowDirection);
        }
        return dispenserRow;
    }
}
