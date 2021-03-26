package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DispenserSystem {
    private final Dispenser[] leftDispensers;
    private final Dispenser[] rightDispensers;
    private final Dispenser[] topDispensers;
    private final Dispenser[] bottomDispensers;
    private List<BoxActor> cachedBoxes;

    public DispenserSystem(Collection<BoxType> boxTypes) {
        leftDispensers = createDispensers(boxTypes, new Vector2i(-1, 9), new Vector2i(-1, 0));
        rightDispensers = createDispensers(boxTypes, new Vector2i(10, 0), new Vector2i(1, 0));
        topDispensers = createDispensers(boxTypes, new Vector2i(0, -1), new Vector2i(0, -1));
        bottomDispensers = createDispensers(boxTypes, new Vector2i(9, 10), new Vector2i(0, 1));
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

    private Dispenser[] createDispensers(Collection<BoxType> boxTypes, Vector2i from, Vector2i dispenserDirection) {
        Dispenser[] dispenserRow = new Dispenser[10];
        Vector2i cursor = new Vector2i(from);
        Vector2i rowDirection = rotate90deg(dispenserDirection);

        for (int i = 0; i < dispenserRow.length; i++) {
            dispenserRow[i] = new Dispenser(boxTypes, cursor, dispenserDirection);
            cursor.add(rowDirection);
        }
        return dispenserRow;
    }

    private Vector2i rotate90deg(Vector2i vec) {
        return new Vector2i(-vec.y(), vec.x());
    }
}
