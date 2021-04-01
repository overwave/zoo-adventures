package dev.overtow.math;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.util.Objects;

public class Matrix {

    private final float m00;
    private final float m01;
    private final float m02;
    private final float m03;
    private final float m10;
    private final float m11;
    private final float m12;
    private final float m13;
    private final float m20;
    private final float m21;
    private final float m22;
    private final float m23;
    private final float m30;
    private final float m31;
    private final float m32;
    private final float m33;

    private Matrix(float m00, float m01, float m02, float m03,
                   float m10, float m11, float m12, float m13,
                   float m20, float m21, float m22, float m23,
                   float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    private Matrix(Matrix4f matrix) {
        m00 = matrix.m00();
        m01 = matrix.m01();
        m02 = matrix.m02();
        m03 = matrix.m03();
        m10 = matrix.m10();
        m11 = matrix.m11();
        m12 = matrix.m12();
        m13 = matrix.m13();
        m20 = matrix.m20();
        m21 = matrix.m21();
        m22 = matrix.m22();
        m23 = matrix.m23();
        m30 = matrix.m30();
        m31 = matrix.m31();
        m32 = matrix.m32();
        m33 = matrix.m33();
    }

    public static Matrix of(float m00, float m01, float m02, float m03,
                            float m10, float m11, float m12, float m13,
                            float m20, float m21, float m22, float m23,
                            float m30, float m31, float m32, float m33) {
        return new Matrix(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public static Matrix ofTranslationRotationScale(Vector3 position, Quaternion rotation, Vector3 scale) {
        Matrix4f matrix = new Matrix4f().translationRotateScale(
                new Vector3f(position.getX(), position.getY(), position.getZ()),
                new Quaternionf(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW()),
                new Vector3f(scale.getX(), scale.getY(), scale.getZ())
        );
        return new Matrix(matrix);
    }

    public static Matrix ofIdentity() {
        return of(1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public static Matrix ofProjectionLookAt(float angle, float aspect, float zNear, float zFar, Vector3 from, Vector3 to) {
        Matrix4f matrix = new Matrix4f()
                .setPerspective((float) Math.toRadians(angle), aspect, zNear, zFar)
                .lookAt(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), 0, 1, 0);
        return new Matrix(matrix);
    }

    public static Matrix ofProjectionRotateTranslate(float angle, float aspect, float zNear, float zFar, Vector3 rotation, Vector3 translation) {
        Matrix4f matrix = new Matrix4f()
                .setPerspective((float) Math.toRadians(angle), aspect, zNear, zFar)
                .rotateXYZ((float) Math.toRadians(rotation.getX()), (float) Math.toRadians(rotation.getY()), (float) Math.toRadians(rotation.getY()))
                .translate(translation.getX(), translation.getY(), translation.getZ());
        return new Matrix(matrix);
    }

    public static Matrix ofModel(Vector3 position, Quaternion rotation, Vector3 scale) {
        Matrix4f matrix = new Matrix4f().translationRotateScale(
                position.getX(), position.getY(), position.getZ(),
                rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW(),
                scale.getX(), scale.getY(), scale.getZ());
        return new Matrix(matrix);
    }

    public Matrix invert() {
        Matrix4f matrix = asMatrix4f().invert();
        return new Matrix(matrix);
    }

    public Matrix normal() {
        Matrix4f matrix = asMatrix4f().normal();
        return new Matrix(matrix);
    }

    Vector4 getMultiplied(Vector4 vec) {
        Vector4f result = new Vector4f(vec.getX(), vec.getY(), vec.getZ(), vec.getW()).mul(asMatrix4f());
        return Vector4.of(result.x(), result.y(), result.z(), result.w());
    }

    private Matrix4f asMatrix4f() {
        return new Matrix4f(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public FloatBuffer allocate(FloatBuffer floatBuffer) {
        return asMatrix4f().get(floatBuffer);
    }

    @Override
    public String toString() {
        return "[" + m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n" +
               " " + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n" +
               " " + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n" +
               " " + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return Float.compare(matrix.m00, m00) == 0 && Float.compare(matrix.m01, m01) == 0 &&
               Float.compare(matrix.m02, m02) == 0 && Float.compare(matrix.m03, m03) == 0 &&
               Float.compare(matrix.m10, m10) == 0 && Float.compare(matrix.m11, m11) == 0 &&
               Float.compare(matrix.m12, m12) == 0 && Float.compare(matrix.m13, m13) == 0 &&
               Float.compare(matrix.m20, m20) == 0 && Float.compare(matrix.m21, m21) == 0 &&
               Float.compare(matrix.m22, m22) == 0 && Float.compare(matrix.m23, m23) == 0 &&
               Float.compare(matrix.m30, m30) == 0 && Float.compare(matrix.m31, m31) == 0 &&
               Float.compare(matrix.m32, m32) == 0 && Float.compare(matrix.m33, m33) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }
}
