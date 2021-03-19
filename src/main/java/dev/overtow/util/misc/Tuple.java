package dev.overtow.util.misc;

import java.util.Objects;

public class Tuple<T, U> {
    private final T t;
    private final U v;

    public static <T, U> Tuple<T, U> of(T t, U u) {
        return new Tuple<>(t, u);
    }

    public Tuple(T t, U v) {
        this.t = Objects.requireNonNull(t);
        this.v = Objects.requireNonNull(v);
    }

    public T getT() {
        return t;
    }

    public U getV() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return t.equals(tuple.t) && v.equals(tuple.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t, v);
    }
}
