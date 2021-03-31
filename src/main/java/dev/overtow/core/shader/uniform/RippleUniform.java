package dev.overtow.core.shader.uniform;

import dev.overtow.core.Ripple;
import org.joml.Vector2f;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform2f;

public class RippleUniform implements ValueUniform<Ripple> {

    private final String namePrefix;

    private int center;
    private int direction;

    public RippleUniform(String name) {
        this.namePrefix = name;
    }

    @Override
    public void setValue(Ripple ripple) {
        Vector2f center = ripple.getCenter();
        Vector2f direction = ripple.getDirection();

        glUniform2f(this.center, center.x(), center.y());
        glUniform2f(this.direction, direction.x(), direction.y());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        center = function.apply(namePrefix + ".center");
        direction = function.apply(namePrefix + ".direction");
    }
}
