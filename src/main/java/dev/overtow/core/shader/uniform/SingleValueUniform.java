package dev.overtow.core.shader.uniform;

import java.util.function.Function;

public abstract class SingleValueUniform<T> implements ValueUniform<T> {
    protected final String name;
    protected int id;
    protected T value;

    public SingleValueUniform(Uniform.Name name) {
        this.name = name.get();
        this.id = -1;
    }

    @Override
    public void locate(Function<String, Integer> function) {
        id = function.apply(name);
    }
}
