package dev.overtow.core;

import dev.overtow.graphics.hud.HudElement;
import dev.overtow.service.hud.HudContext;
import dev.overtow.service.window.Window;
import dev.overtow.util.injection.Injector;

import java.util.List;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;

public class HudRenderer {

    private final HudContext hudContext;
    private final Window window;

    public HudRenderer() {
        this.window = Injector.getInstance(Window.class);
        this.hudContext = Injector.getInstance(HudContext.class);
    }

    public void render(List<HudElement> hudElements) {
        long context = hudContext.getContextHandler();

        nvgBeginFrame(context, window.getWidth(), window.getHeight(), 1);

        for (HudElement element : hudElements) {
            element.draw(context);
        }

        nvgEndFrame(context);
    }
}
