package dev.overtow.service;

import org.lwjgl.nanovg.NVGColor;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public interface MemoryManager {
    void doForColor(Color color, Consumer<NVGColor> consumer);

    ByteBuffer allocateBuffer();

    ByteBuffer readFromFile(String resource) throws IOException;
}
