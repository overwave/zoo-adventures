package dev.overtow.core.shader.uniform;

import java.util.function.Function;

public interface Uniform<T> {
    void locate(Function<String, Integer> function);

    enum Name {

        MODEL_LIGHT_VIEW_MATRIX("modelLightViewMatrix"),
        ORTHO_PROJECTION_MATRIX("orthoProjectionMatrix"),
        PROJECTION_MATRIX("projectionMatrix"),
        MODEL_VIEW_MATRIX("modelViewMatrix"),
        NORMAL_MAP("normalMap"),
        MATERIAL("material"),
        SPECULAR_POWER("specularPower"),
        AMBIENT_LIGHT("ambientLight"),
        POINT_LIGHTS("pointLights"),
        SPOT_LIGHTS("spotLights"),
        DIRECTIONAL_LIGHT("directionalLight"),
        SHADOW_MAP("shadowMap"),
        NUM_COLS("numCols"),
        NUM_ROWS("numRows"),
        SELECTED("selected"),
        BACK_COLOR("backColor"),


        TEXTURE_SAMPLER("textureSampler"),
        DEPTH_TEXTURE("depthTexture"),
        LIGHT_POSITION("lightPosition"),
        VIEW_PROJECTION_MATRIX("viewProjectionMatrix"),
        LIGHT_VIEW_PROJECTION_MATRIX("lightViewProjectionMatrix"),
        BIAS_MATRIX("biasMatrix"),
        MODEL_MATRIX("modelMatrix"),

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
