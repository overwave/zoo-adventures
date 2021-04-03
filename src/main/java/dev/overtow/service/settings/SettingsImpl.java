package dev.overtow.service.settings;

import dev.overtow.service.config.Config;
import dev.overtow.util.injection.Bind;

@Bind
public class SettingsImpl implements Settings {

    private final int shadowsAntialiasingLevel;
    private final int msaaLevel;

    public SettingsImpl(Config config) {
        msaaLevel = config.getInteger("msaa.level");
        shadowsAntialiasingLevel = config.getInteger("shadows.antialiasing.level");
    }

    @Override
    public int getShadowsAntialiasingLevel() {
        return shadowsAntialiasingLevel;
    }

    @Override
    public int getMsaaLevel() {
        return msaaLevel;
    }
}
