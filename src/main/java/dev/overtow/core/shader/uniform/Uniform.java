package dev.overtow.core.shader.uniform;

import java.util.function.Function;

public interface Uniform<T> {
    void locate(Function<String, Integer> function);

    enum Name {
        VIEW_PROJECTION_MATRIX("viewProjectionMatrix"),

        MODEL_LIGHT_VIEW_MATRIX("modelLightViewMatrix"),
        ORTHO_PROJECTION_MATRIX("orthoProjectionMatrix"),
        CASCADE_FAR_PLANES("cascadeFarPlanes"),
        MODEL_NON_INSTANCED_MATRIX("modelNonInstancedMatrix"),
        LIGHT_VIEW_MATRIX("lightViewMatrix"),
        PROJECTION_MATRIX("projectionMatrix"),
//        VIEW_MATRIX("viewMatrix"),
        TEXTURE_SAMPLER("textureSampler"),
        NORMAL_MAP("normalMap"),
        MATERIAL("material"),
        SPECULAR_POWER("specularPower"),
        AMBIENT_LIGHT("ambientLight"),
        POINT_LIGHTS("pointLights"),
        SPOT_LIGHTS("spotLights"),
        DIRECTIONAL_LIGHT("directionalLight"),
        SHADOW_MAP("shadowMap"),
        SELECTED("selected"),
        BACK_COLOR("backColor");

        private final String name;

        Name(String name) {
            this.name = name;
        }

        public String get() {
            return name;
        }
    }
}
