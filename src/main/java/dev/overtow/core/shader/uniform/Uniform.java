package dev.overtow.core.shader.uniform;

import java.util.function.Function;

public interface Uniform<T> {
    void locate(Function<String, Integer> function);
}
