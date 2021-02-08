package dev.overtow.graphics.hud;

public enum TextAlign {
    LEFT(1 << 0),
    CENTER(1 << 1),
    RIGHT(1 << 2),
    TOP(1 << 3),
    MIDDLE(1 << 4),
    BOTTOM(1 << 5),
    BASELINE(1 << 6);

    private final int mask;

    TextAlign(int mask) {
        this.mask = mask;
    }

    public static int mergeMasks(TextAlign... textAligns) {
        int result = 0;
        for (TextAlign align : textAligns) {
            result |= align.mask;
        }
        return result;
    }
}
