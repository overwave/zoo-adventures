package dev.overtow.service.shader;

import java.util.function.Consumer;

public class Uniform<T>  {
    protected final String name;
    protected T value;

    public Uniform(String name) {
        this.name = name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void createUniform(Consumer<String> consumer) {
        consumer.accept(name);
    }
}
