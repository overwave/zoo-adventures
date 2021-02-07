package dev.overtow.service;

import dev.overtow.util.injection.Bind;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Bind
public class MemoryManagerImpl implements MemoryManager {

    private final List<ByteBuffer> buffers;
    private final NVGColor nativeColor;

    public MemoryManagerImpl() {
        nativeColor = NVGColor.create();
        buffers = new ArrayList<>();
    }

    @Override
    public void doForColor(Color color, Consumer<NVGColor> consumer) {
        nativeColor.r(color.getRed() / 255.0f);
        nativeColor.g(color.getGreen() / 255.0f);
        nativeColor.b(color.getBlue() / 255.0f);
        nativeColor.a(color.getAlpha() / 255.0f);

        consumer.accept(nativeColor);
    }

    @Override
    public ByteBuffer allocateBuffer() {
        return null;
    }

    @Override
    public ByteBuffer readFromFile(String fileName) throws IOException {
        try (InputStream inputStream = new FileInputStream(fileName)) {
            byte[] bytes =  inputStream.readAllBytes();
            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length + 1);
            buffer.put(bytes);
            buffer.flip();
            return buffer;
        }
    }
}
