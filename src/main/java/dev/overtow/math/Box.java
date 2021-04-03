package dev.overtow.math;

import org.joml.Vector2f;
import org.joml.primitives.Intersectionf;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Box {
    private final Vector3 from;
    private final Vector3 to;

    private Box(Vector3 from, Vector3 to) {
        this.from = from;
        this.to = to;
    }

    public static Box of(Vector3 from, Vector3 to) {
        return new Box(from, to);
    }

    public static Box of(List<Box> boxes) {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        for (Box box : boxes) {
            minX = Math.min(minX, box.from.getX());
            minY = Math.min(minY, box.from.getY());
            minZ = Math.min(minZ, box.from.getZ());
            maxX = Math.max(maxX, box.to.getX());
            maxY = Math.max(maxY, box.to.getY());
            maxZ = Math.max(maxZ, box.to.getZ());
        }
        return of(Vector3.of(minX, minY, minZ), Vector3.of(maxX, maxY, maxZ));
    }

    @Override
    public String toString() {
        return from + " / " + to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Objects.equals(from, box.from) && Objects.equals(to, box.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    public Optional<Float> testRay(Vector3 center, Vector3 direction) {
        Vector2f result = new Vector2f();

        boolean collision = Intersectionf.intersectRayAab(
                center.getX(), center.getY(), center.getZ(),
                direction.getX(), direction.getY(), direction.getZ(),
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ(),
                result);

        return collision ? Optional.of(result.x()) : Optional.empty();
    }
}
