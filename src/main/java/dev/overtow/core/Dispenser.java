package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Dispenser {
    public static final int BOXES_AMOUNT = 3;

    private final List<BoxActor> actors;

    public Dispenser(Collection<BoxType> boxTypes, Vector2i from, Vector2i direction) {
        actors = new ArrayList<>(BOXES_AMOUNT);
        Vector2i cursor = new Vector2i(from);

        for (int i = 0; i < BOXES_AMOUNT; i++) {
            actors.add(new BoxActor(cursor));
            cursor.add(direction);
        }
    }

    public List<BoxActor> getBoxes() {
        return Collections.unmodifiableList(actors);
    }
}
