package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Dispenser {
    public static final int BOXES_AMOUNT = 3;

    private final List<BoxActor> actors;

    public Dispenser(List<BoxType> boxTypes, Vector2i from, Vector2i direction) {
        actors = new ArrayList<>(BOXES_AMOUNT);
        Vector2i cursor = new Vector2i(from);
        ThreadLocalRandom random =  ThreadLocalRandom.current();

        for (int i = 0; i < BOXES_AMOUNT; i++) {
            BoxType randomType = boxTypes.get(random.nextInt(boxTypes.size()));
            actors.add(new BoxActor(randomType, cursor));
            cursor.add(direction);
        }
    }

    public List<BoxActor> getBoxes() {
        return Collections.unmodifiableList(actors);
    }
}
