package dev.overtow.glsl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GlslType {
    Class<?> value();
}
