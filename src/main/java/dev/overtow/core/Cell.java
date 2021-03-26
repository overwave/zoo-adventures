package dev.overtow.core;

import dev.overtow.graphics.draw.BoxType;

public class Cell {
    private final int x;
    private final int y;
    private final BoxType type;

    public Cell(String description) {
        String[] tokens = description.split(" ");

        x = Integer.parseInt(tokens[0]);
        y = Integer.parseInt(tokens[1]);
        type = BoxType.valueOf(tokens[2].toUpperCase());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BoxType getType() {
        return type;
    }
}
