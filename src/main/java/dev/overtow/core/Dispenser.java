package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;
import dev.overtow.math.Box;
import dev.overtow.math.Vector2;
import dev.overtow.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Dispenser {
    private static final int BOXES_AMOUNT = 3;

    private final List<BoxActor> actors;
    private final Box hitBox;

    public Dispenser(List<BoxType> boxTypes, Vector2 from, Vector2 direction) {
        actors = new ArrayList<>(BOXES_AMOUNT);
        Vector2 cursor = from;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<Box> hitBoxes = new ArrayList<>();
        for (int i = 0; i < BOXES_AMOUNT; i++) {
            BoxType randomType = boxTypes.get(random.nextInt(boxTypes.size()));
            BoxActor boxActor = new BoxActor(randomType, cursor);
            hitBoxes.add(boxActor.getHitBox());
            actors.add(boxActor);
            cursor = cursor.plus(direction);
        }
        hitBox = Box.of(hitBoxes);
    }

    public List<BoxActor> getBoxes() {
        return Collections.unmodifiableList(actors);
    }

    public void checkSelection(Vector3 cameraPosition, Vector3 mouseDirection) {
        boolean mouseHovered = hitBox.testRay(cameraPosition, mouseDirection).isPresent();

        if (mouseHovered) {
            System.out.println(this);
        }
        actors.get(0).setSelected(mouseHovered);
    }
}
