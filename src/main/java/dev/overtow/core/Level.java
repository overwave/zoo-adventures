package dev.overtow.core;

import java.util.Collections;
import java.util.List;

public class Level {
    private final List<Cell> cells;

    public Level(String levelContent) {
        String[] lines = levelContent.split("\n");
//        cells = Arrays.stream(lines).map(Cell::new).collect(Collectors.toList());
        cells = List.of(new Cell("4 4 CORN"));
    }

    public List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }
}
