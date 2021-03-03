package dev.overtow.service.hud;

import dev.overtow.util.injection.Bind;
import dev.overtow.util.injection.Destroyable;

import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Bind
public class NvgContext implements HudContext, Destroyable {
    private final long contextHandler;

    public NvgContext() {
        this.contextHandler = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.contextHandler == NULL) {
            throw new RuntimeException("Could not init nanoVg");
        }
    }

    @Override
    public long getContextHandler() {
        return contextHandler;
    }

    @Override
    public void destroy() {
        nvgDelete(contextHandler);
    }
}
