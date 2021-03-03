package dev.overtow.core;

import dev.overtow.service.hud.HudContext;
import dev.overtow.service.memory.MemoryManager;
import dev.overtow.util.injection.Injector;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

public enum Font {

    OPEN_SANS_REGULAR("data/fonts/OpenSans-Regular.ttf"),
    OPEN_SANS_BOLD("data/fonts/OpenSans-Bold.ttf"),
    ;

    // data should be freed by byte buffer creator
    private static final int DONT_FREE_DATA = 0;

    private final int fontHandler;
    private final ByteBuffer fontBuffer; // CANNOT BE FREED WHILE FONT IS USED


    Font(String fontPath) {
        MemoryManager memoryManager = Injector.getInstance(MemoryManager.class);
        HudContext hudContext = Injector.getInstance(HudContext.class);

        try {
            fontBuffer = memoryManager.readFromFile(fontPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fontHandler = nvgCreateFontMem(hudContext.getContextHandler(), this.name(), fontBuffer, DONT_FREE_DATA);
        if (fontHandler == -1) {
            throw new RuntimeException("Could not create font");
        }
    }
}