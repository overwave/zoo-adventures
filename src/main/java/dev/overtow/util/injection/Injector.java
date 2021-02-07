package dev.overtow.util.injection;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Injector {

    private static final Map<Class<?>, Class<?>> bindMap = new HashMap<>();
    private static final Map<Class<?>, Object> servicesMap = new HashMap<>();


    public static void init() {
        Reflections reflections = new Reflections("dev.overtow");
        Collection<Class<?>> annotated = reflections.getTypesAnnotatedWith(Bind.class);

        annotated.forEach(clazz -> {
            for (Class<?> interf : clazz.getInterfaces()) {
                bindMap.put(interf, clazz);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<? extends T> interf) {
        Object o = servicesMap.get(interf);
        if (o != null) {
            return (T) o;
        }

        return createInstance(interf);
    }

    private static <T> T createInstance(Class<? super T> interf) {
        Class<?> clazz = bindMap.get(interf);
        if (clazz == null) {
            throw new RuntimeException(String.format("Failed to bind interface %s", interf.getSimpleName()));
        }

        Constructor<?> constructor = getConstructor(clazz);

        return getT(interf, constructor);
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
    private static <T> T getT(Class<? super T> interf, Constructor<?> constructor) {
        try {
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            Object[] arguments = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                arguments[i] = getInstance(parameterTypes[i]);
            }

            Object service = constructor.newInstance(arguments);

            servicesMap.put(interf, service);
            return (T) service;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
