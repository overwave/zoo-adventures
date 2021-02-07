package dev.overtow.service;

import dev.overtow.util.injection.Bind;
import org.lwjgl.nanovg.NVGColor;

import java.awt.*;
import java.util.function.Consumer;

@Bind
public class MemoryManagerImpl implements MemoryManager {
    private NVGColor nativeColor;

    public MemoryManagerImpl() {
        nativeColor = NVGColor.create();
    }

    @Override
    public void doForColor(Color color, Consumer<NVGColor> consumer) {
        nativeColor.r(color.getRed() / 255.0f);
        nativeColor.g(color.getGreen() / 255.0f);
        nativeColor.b(color.getBlue() / 255.0f);
        nativeColor.a(color.getAlpha() / 255.0f);

        consumer.accept(nativeColor);
    }
}
