package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DispenserSystem {
    private final Dispenser[] leftDispensers;
    private final Dispenser[] rightDispensers;
    private final Dispenser[] topDispensers;
    private final Dispenser[] bottomDispensers;
    private List<BoxActor> cachedBoxes;

    public DispenserSystem(List<BoxType> boxTypes) {
        leftDispensers = createDispensers(boxTypes, Vector2.of(-1, 9), Vector2.of(-1, 0));
        rightDispensers = createDispensers(boxTypes, Vector2.of(10, 0), Vector2.of(1, 0));
        topDispensers = createDispensers(boxTypes, Vector2.of(0, -1), Vector2.of(0, -1));
        bottomDispensers = createDispensers(boxTypes, Vector2.of(9, 10), Vector2.of(0, 1));
    }

    public void update() {
        // todo make it "getBoxFrom x, y"?
        cachedBoxes = new ArrayList<>();
        addBoxesFrom(leftDispensers);
        addBoxesFrom(rightDispensers);
        addBoxesFrom(topDispensers);
        addBoxesFrom(bottomDispensers);
    }

    public List<BoxActor> getBoxes() {
        return Collections.unmodifiableList(cachedBoxes);
    }

    private void addBoxesFrom(Dispenser[] dispensers) {
        for (Dispenser dispenser : dispensers) {
            cachedBoxes.addAll(dispenser.getBoxes());
        }
    }

    private Dispenser[] createDispensers(List<BoxType> boxTypes, Vector2 from, Vector2 dispenserDirection) {
        Dispenser[] dispenserRow = new Dispenser[10];
        Vector2 cursor = from;
        Vector2 rowDirection = rotate90deg(dispenserDirection);

        for (int i = 0; i < dispenserRow.length; i++) {
            dispenserRow[i] = new Dispenser(boxTypes, cursor, dispenserDirection);
            cursor = cursor.plus(rowDirection);
        }
        return dispenserRow;
    }

    private Vector2 rotate90deg(Vector2 vec) {
        return Vector2.of(-vec.getY(), vec.getX());
    }
}
