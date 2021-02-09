package dev.overtow.service.shader;

import org.lwjglb.engine.graph.Material;

import java.util.function.Consumer;

public class MaterialUniform extends Uniform<Material> {
    public MaterialUniform(String name) {
        super(name);
    }

    @Override
    public void createUniform(Consumer<String> consumer) {
        consumer.accept(name + ".ambient");
        consumer.accept(name + ".diffuse");
        consumer.accept(name + ".specular");
        consumer.accept(name + ".backColor");
        consumer.accept(name + ".hasTexture");
        consumer.accept(name + ".hasNormalMap");
        consumer.accept(name + ".reflectance");
    }
}
