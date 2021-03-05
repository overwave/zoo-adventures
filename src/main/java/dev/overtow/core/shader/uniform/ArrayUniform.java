package dev.overtow.core.shader.uniform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArrayUniform<T> implements Uniform<List<ValueUniform<T>>> {
    private final List<ValueUniform<T>> childrenUniforms;

    public ArrayUniform(Uniform.Name name, int size, Function<String, ValueUniform<T>> elementConstructor) {
        childrenUniforms = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String arrayPrefix = String.format("%s[%d]", name.get(), i);

            childrenUniforms.add(elementConstructor.apply(arrayPrefix));
        }
    }

    public void setElements(List<T> values) {
        for (int i = 0; i < values.size(); i++) {
            this.childrenUniforms.get(i).setValue(values.get(i));
        }
    }

    @Override
    public void locate(Function<String, Integer> function) {
        for (Uniform<T> uniform : childrenUniforms) {
            uniform.locate(function);
        }
    }
}
