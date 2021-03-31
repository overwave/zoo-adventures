package dev.overtow.math;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class Quaternion {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    private Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Quaternion of() {
        return new Quaternion(0, 0, 0, 1);
    }

    public static Quaternion of(float x, float y, float z, float w) {
        return new Quaternion(x, y, z, w);
    }

    public static Quaternion ofRotationTo(Vector3 from, Vector3 to) {
        Quaternionf result = new Quaternionf().rotationTo(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
        return of(result.x(), result.y(), result.z(), result.w());
    }

    public static Quaternion ofAverageRotation(Quaternion[] quaternions) {
        Quaternionf result = new Quaternionf();

        Quaternionfc[] tempQuaternions = new Quaternionfc[quaternions.length];
        float[] weights = new float[quaternions.length];
        for (int i = 0; i < quaternions.length; i++) {
            Quaternion quat = quaternions[i];
            tempQuaternions[i] = new Quaternionf(quat.x, quat.y, quat.z, quat.w);
            weights[i] = 1.f / quaternions.length;
        }

        Quaternionf.nlerp(tempQuaternions, weights, result);
        return of(result.x(), result.y(), result.z(), result.w());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    public Quaternion multiply(Quaternion quaternion) {
        Quaternionf result = new Quaternionf(x, y, z, w).mul(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
        return of(result.x(), result.y(), result.z(), result.w());
    }
}
