package dev.overtow.graphics.draw;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.reflections.Reflections;

import java.util.Collection;

public class Injector extends AbstractModule {
    private static final com.google.inject.Injector INJECTOR = Guice.createInjector(new Injector());

    public static <T> T getInstance(Class<T> c) {
        return INJECTOR.getInstance(c);
    }

    @Override
    protected void configure() {
        Reflections reflections = new Reflections("dev.overtow");
        Collection<Class<?>> annotated = reflections.getTypesAnnotatedWith(Bind.class);

        annotated.forEach(this::annotate);
    }

    private <T> void annotate(Class<T> clazz) {
        for (Class<?> interf : clazz.getInterfaces()) {
            @SuppressWarnings("unchecked")
            Class<? super T> casted = (Class<? super T>) interf;

            bind(casted).to(clazz);
        }
    }
}
