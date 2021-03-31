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
        WAVES("waves"),
        RIPPLES("ripples"),
        TEXTURE_MOVING_DIRECTION("textureMovingDirection"),
        BACKGROUND_COLOR("backgroundColor"),
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
