package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Dispenser {
    public static final int BOXES_AMOUNT = 3;

    private final List<BoxActor> actors;

    public Dispenser(List<BoxType> boxTypes, Vector2 from, Vector2 direction) {
        actors = new ArrayList<>(BOXES_AMOUNT);
        Vector2 cursor = from;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < BOXES_AMOUNT; i++) {
            BoxType randomType = boxTypes.get(random.nextInt(boxTypes.size()));
            actors.add(new BoxActor(randomType, cursor));
            cursor = cursor.plus(direction);
        }
    }

    public List<BoxActor> getBoxes() {
        return Collections.unmodifiableList(actors);
    }
}
