package dev.overtow.core.shader.uniform;

public interface ValueUniform<T> extends Uniform<T> {
    void setValue(T value);
}
