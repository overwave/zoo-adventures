package dev.overtow.core.shader.uniform;

import java.util.function.Function;

public interface Uniform<T> {
    void locate(Function<String, Integer> function);

    enum Name {
        // scene & shadow vert
        LIGHT_VIEW_MATRIX("lightViewMatrix"),
        ORTHO_PROJECTION_MATRIX("orthoProjectionMatrix"),
        VIEW_MATRIX("viewMatrix"),
        PROJECTION_MATRIX("projectionMatrix"),
        MODEL_MATRIX("modelMatrix"),
        SELECTED("selected"),

        // scene frag
        TEXTURE_SAMPLER("textureSampler"),
        NORMAL_MAP("normalMap"),
        AMBIENT_LIGHT("ambientLight"),
        BACK_COLOR("backColor"),
        SPECULAR_POWER("specularPower"),
        MATERIAL("material"),
        POINT_LIGHTS("pointLights"),
        SPOT_LIGHTS("spotLights"),
        DIRECTIONAL_LIGHT("directionalLight"),
        SHADOW_MAP("shadowMap"),
        CASCADE_FAR_PLANES("cascadeFarPlanes");

        private final String name;

        Name(String name) {
            this.name = name;
        }

        public String get() {
            return name;
        }
    }
}
