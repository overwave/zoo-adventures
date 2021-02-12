package dev.overtow.util.injection;

import dev.overtow.util.misc.Tuple;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Injector {

    private static final Map<Tuple<Class<?>, String>, Class<?>> bindMap = new HashMap<>();
    private static final Map<Tuple<Class<?>, String>, Object> servicesMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("dev.overtow");
        Collection<Class<?>> annotated = reflections.getTypesAnnotatedWith(Bind.class);

        annotated.forEach(clazz -> {
            for (Class<?> interf : clazz.getInterfaces()) {
                bindMap.put(new Tuple<>(interf, clazz.getAnnotation(Bind.class).value()), clazz);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<? extends T> interf, String tag) {
        Tuple<Class<?>, String> tuple = new Tuple<>(interf, tag);

        Object o = servicesMap.get(tuple);
        if (o != null) {
            return (T) o;
        }

        return createInstance(tuple);
    }

    public static <T> T getInstance(Class<? extends T> interf) {
        return getInstance(interf, "");
    }

    private static <T> T createInstance(Tuple<Class<?>, String> tuple) {
        Class<?> clazz = bindMap.get(tuple);
        if (clazz == null) {
            throw new RuntimeException(String.format("Failed to bind interface %s", tuple.getT().getSimpleName()));
        }

        Constructor<?> constructor = getConstructor(clazz);

        return getT(tuple, constructor);
    }

    private static Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        Constructor<?> constructor;
        if (constructors.length == 1) {
            constructor = constructors[0];
        } else {
            throw new RuntimeException("Not realized yet");
//            for (Constructor<?> c : constructors) {
//                if (c.isAnnotationPresent())
//            }
        }
        return constructor;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getT(Tuple<Class<?>, String> tuple, Constructor<?> constructor) {
        try {
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            Object[] arguments = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                arguments[i] = getInstance(parameterTypes[i]);
            }

            Object service = constructor.newInstance(arguments);

            servicesMap.put(tuple, service);
            return (T) service;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
