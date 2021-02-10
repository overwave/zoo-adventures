package dev.overtow.core.shader.uniform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArrayUniform<T> implements Uniform<List<ValueUniform<T>>> {
    private final List<ValueUniform<T>> value;

    public ArrayUniform(Uniform.Name name, int size, Function<String, ValueUniform<T>> elementConstructor) {
        value = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String arrayPrefix = String.format("%s[%d]", name.get(), i);

            value.add(elementConstructor.apply(arrayPrefix));
        }
    }

    public void setElement(T value, int index) {
        if (value.equals(this.value.get(index))) {
            return;
        }
        this.value.get(index).setValue(value);
    }

    @Override
    public void locate(Function<String, Integer> function) {
        for (Uniform<T> uniform : value) {
            uniform.locate(function);
        }
    }
}
