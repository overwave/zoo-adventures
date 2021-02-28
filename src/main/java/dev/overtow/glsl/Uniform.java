package dev.overtow.glsl;

import dev.overtow.core.shader.uniform.Uniform.Name;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Uniform {
    Name value();
}
