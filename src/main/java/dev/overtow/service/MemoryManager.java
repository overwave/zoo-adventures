package dev.overtow.service;

import org.lwjgl.nanovg.NVGColor;

import java.awt.*;
import java.util.function.Consumer;

public interface MemoryManager {
    void doForColor(Color color, Consumer<NVGColor> consumer);
}
