package dev.overtow.service.shader;

import org.lwjglb.engine.graph.lights.PointLight;

import java.util.function.Consumer;

public class SpotLightUniform extends Uniform<PointLight> {
    public SpotLightUniform(String name) {
        super(name);
    }

    @Override
    public void createUniform(Consumer<String> consumer) {
        consumer.accept(name + ".pl.colour");
        consumer.accept(name + ".pl.position");
        consumer.accept(name + ".pl.intensity");
        consumer.accept(name + ".pl.att.constant");
        consumer.accept(name + ".pl.att.linear");
        consumer.accept(name + ".pl.att.exponent");
        consumer.accept(name + ".conedir");
        consumer.accept(name + ".cutoff");
    }
}
