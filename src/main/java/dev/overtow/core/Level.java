package dev.overtow.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Level {
    private final List<Cell> cells;

    public Level(String levelContent) {
        String[] lines = levelContent.split("\n");
        cells = Arrays.stream(lines).map(Cell::new).collect(Collectors.toList());
    }

    public List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }
}
