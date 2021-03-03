package dev.overtow.service.memory;

import org.lwjgl.nanovg.NVGColor;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public interface MemoryManager {
    void doForColor(Color color, Consumer<NVGColor> consumer);

    ByteBuffer readFromFile(String resource) throws IOException;
}
