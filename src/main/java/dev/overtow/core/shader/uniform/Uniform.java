package dev.overtow.core.shader.uniform;

import java.util.function.Function;

public interface Uniform<T> {
    void locate(Function<String, Integer> function);

    enum Name {
        TEXTURE_SAMPLER("textureSampler"),
        DEPTH_TEXTURE("depthTexture"),
        LIGHT_POSITION("lightPosition"),
        VIEW_PROJECTION_MATRIX("viewProjectionMatrix"),
        NORMAL_MATRIX("normalMatrix"),
        LIGHT_VIEW_PROJECTION_MATRIX("lightViewProjectionMatrix"),
        BIAS_MATRIX("biasMatrix"),
        MODEL_MATRIX("modelMatrix"),
        TIME("time"),
        BACKGROUND_COLOR("backgroundColor"),
        SHADOWS_ANTIALIASING_LEVEL("shadowsAntialiasingLevel"),
        ITEM_SELECTED("itemSelected"),
        ;

        private final String name;

        Name(String name) {
            this.name = name;
        }

        public String get() {
            return name;
        }
    }
}
