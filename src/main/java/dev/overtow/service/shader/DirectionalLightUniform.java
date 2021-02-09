package dev.overtow.service.shader;

import org.lwjglb.engine.graph.lights.DirectionalLight;

import java.util.function.Consumer;

public class DirectionalLightUniform extends Uniform<DirectionalLight> {
    public DirectionalLightUniform(String name) {
        super(name);
    }

    @Override
    public void createUniform(Consumer<String> consumer) {
        consumer.accept(name + ".colour");
        consumer.accept(name + ".direction");
        consumer.accept(name + ".intensity");
    }
}
