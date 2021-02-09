package dev.overtow.service.shader;

import org.lwjglb.engine.graph.lights.PointLight;

import java.util.function.Consumer;

public class PointLightUniform extends Uniform<PointLight> {
    public PointLightUniform(String name) {
        super(name);
    }

    @Override
    public void createUniform(Consumer<String> consumer) {
        consumer.accept(name + ".colour");
        consumer.accept(name + ".position");
        consumer.accept(name + ".intensity");
        consumer.accept(name + ".att.constant");
        consumer.accept(name + ".att.linear");
        consumer.accept(name + ".att.exponent");
    }
}
