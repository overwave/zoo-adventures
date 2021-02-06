package dev.overtow.service;

import dev.overtow.util.injection.Bind;
import org.lwjglb.engine.WindowKek;

@Bind
public class WindowImpl implements Window {
    private final WindowKek windowHandle;

    private String title;
    private int height;
    private int width;

    public WindowImpl(Config config) {
        title = config.getString("window.title");
        width = config.getInteger("window.width");
        height = config.getInteger("window.height");
        WindowKek.WindowOptions opts = new WindowKek.WindowOptions();

        this.windowHandle = new WindowKek(title, width, height);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
