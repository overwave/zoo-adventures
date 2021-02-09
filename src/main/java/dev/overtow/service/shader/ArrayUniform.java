package dev.overtow.service.shader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArrayUniform<T extends Uniform<?>> extends Uniform<List<T>> {
    public ArrayUniform(String name, int size, Function<String, T> elementConstructor) {
        super(name);

        value= new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String arrayPrefix = String.format("%s[%d]", name, i);

            value.add(elementConstructor.apply(arrayPrefix));
        }
    }

    @Override
    public void createUniform(Consumer<String> consumer) {
        for (T t : value) {
            t.createUniform(consumer);
        }
    }
}
