package dev.overtow.core;

import dev.overtow.util.Utils;
import dev.overtow.util.misc.Tuple;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaterActor implements Actor {
    private final List<Wave> waves;
    private final Quaternionf rotation;
    private final List<Ripple> ripples;

    public WaterActor() {
        this.rotation = new Quaternionf();

        waves = List.of(
                new Wave(0, 0.008f, 3f, 0.0f, new Vector2f(9, 8).normalize()),

                new Wave(0, 0.02f, 0.5f, 0.0f, new Vector2f(2, 10).normalize()),

                new Wave(0, 0.05f, 1.5f, 0.2f, new Vector2f(9, 5).normalize()),
                new Wave(0, 0.05f, 1.5f, 0.2f, new Vector2f(5, 9).normalize())
//                new Wave(0.19f, 0.05f, 2f, 0.3f, new Vector2f(1, 1).normalize())
        );
        ripples = List.of(
                new Ripple(new Vector2f(-0.05f, -0.05f), new Vector2f(0, -1))
        );
    }

    public Tuple<Vector3f, Quaternionf> getWavesShift(Vector3f position, Vector2f size, float time) {
//        return getWavesShiftExact(position, time);
        Vector3f[] positions = new Vector3f[4];
        Quaternionf[] rotations = new Quaternionf[4];

        List<Tuple<Vector3f, Quaternionf>> tuples = Stream.of(
                new Vector3f(-size.x() / 2, 0, -size.y() / 2),
                new Vector3f(size.x() / 2, 0, -size.y() / 2),
                new Vector3f(-size.x() / 2, 0, size.y() / 2),
                new Vector3f(size.x() / 2, 0, size.y() / 2))
                .map(vector -> vector.add(position))
                .map(vector -> getWavesShiftExact(vector, time)).collect(Collectors.toList());
        for (int i = 0; i < tuples.size(); i++) {
            Tuple<Vector3f, Quaternionf> tuple = tuples.get(i);
            positions[i] = tuple.getT();
            rotations[i] = tuple.getV();
        }

        Vector3f averagePosition = new Vector3f();
        for (Vector3f vec : positions) {
            averagePosition.add(vec);
        }
        averagePosition.div(4);

        Quaternionf averageQuaternion = new Quaternionf();
        Quaternionf.nlerp(rotations, new float[]{0.25f, 0.25f, 0.25f, 0.25f}, averageQuaternion);
        return Tuple.of(averagePosition, averageQuaternion);
    }

    private Tuple<Vector3f, Quaternionf> getWavesShiftExact(Vector3f position, float time) {
        Vector4f reducedPosition = new Vector4f(position, 1);
        Matrix4f modelMatrix = new Matrix4f().translationRotateScale(
                getPosition(),
                getRotation(),
                getScale());
        Matrix4f normalMatrix = new Matrix4f(modelMatrix).normal();
        Matrix4f invertedModelMatrix = new Matrix4f(modelMatrix).invert();

        Vector4f positionOnWater = reducedPosition.mul(invertedModelMatrix);

        Vector3f resultOffset = new Vector3f(0);
        Vector3f defaultNormal = new Vector3f(0, 1, 0);
        Vector3f tangent = new Vector3f(1, 0, 0);
        Vector3f bitangent = new Vector3f(0, 0, 1);

        for (Wave wave : waves) {

            Vector2f offset = new Vector2f(positionOnWater.x(), positionOnWater.z()).mul(wave.getDirection());

            resultOffset.y += Math.sin(wave.getSpeed() * -time + (offset.x + offset.y) / wave.getLength()) * wave.getAmplitude();
            tangent.y += Math.cos(wave.getSpeed() * -time + (offset.x + offset.y) / wave.getLength()) * wave.getAmplitude() * wave.getDirection().x() / wave.getLength();
            bitangent.y += Math.cos(wave.getSpeed() * -time + (offset.x + offset.y) / wave.getLength()) * wave.getAmplitude() * wave.getDirection().y() / wave.getLength();
//            float k = 2.0f * 3.1416f / wave.getLength();
//            float kA = k * wave.getAmplitude();
//            Vector2f D = wave.getDirection().normalize();
//            Vector2f K = new Vector2f(D).mul(k);
//
//            float S = wave.getSpeed();
//            float w = S * k;
//            float wT = w * time;
//
//            float KPwT = K.dot(new Vector2f(positionOnWater.x(), positionOnWater.z())) - wT;
//            float S0 = (float) Math.sin(KPwT);
//            float C0 = (float) Math.cos(KPwT);
//
//            Vector2f dunno = new Vector2f(D).mul(wave.getSteepness() * wave.getAmplitude() * S0);
//            resultOffset = new Vector3f(
//                    resultOffset.x() - dunno.x(),
//                    resultOffset.y() + wave.getAmplitude() * C0,
//                    resultOffset.z() - dunno.y()
//            );

//            bitangent = new Vector3f(bitangent).add(new Vector3f(
//                    1 - wave.getSteepness() * D.x * D.x * kA * C0,
//                    D.x * kA * S0,
//                    -(wave.getSteepness() * D.x * D.y * kA * C0)
//            ).normalize());
//            tangent = new Vector3f(tangent).add(new Vector3f(
//                    -(wave.getSteepness() * D.x * D.y * kA * C0),
//                    D.y * kA * S0,
//                    1 - wave.getSteepness() * D.y * D.y * kA * C0
//            ).normalize());
        }
//        Vector3f resultNormal = new Vector3f(0,1,0);
        Vector3f resultNormal = bitangent.cross(tangent).normalize();
//        System.out.println(resultNormal);
        Vector4f mul = new Vector4f(resultNormal, 1).mul(normalMatrix);
        resultNormal = new Vector3f(mul.x, mul.y, mul.z).normalize();
        Quaternionf rotationQuaternion = new Quaternionf().rotationTo(defaultNormal, resultNormal);

        Vector4f temp = new Vector4f(resultOffset, 1).mul(modelMatrix);
        temp.add(new Vector4f(getPosition(), 0));
        return Tuple.of(new Vector3f(temp.x(), temp.y(), temp.z()), rotationQuaternion);
    }

    @Override
    public void update() {
//        waves.get(0).setDirection(new Vector2f((float) Math.cos(Utils.getTime()/3), (float) Math.sin(Utils.getTime()/3)));
        // NOP
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(0, -0.02f, 0);
    }

    @Override
    public Vector3f getScale() {
        return new Vector3f(10, 1, 10);
    }

    @Override
    public Quaternionf getRotation() {
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return Mesh.Id.WATER;
    }

    @Override
    public Vector4f getBackgroundColor() {
        return Utils.NO_BACKGROUND_COLOR;
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public List<Ripple> getRipples() {
        return ripples;
    }

    public Vector2f getWavesDirection() {
        return waves.stream()
                .map(wave -> wave.getDirection().mul(wave.getSpeed()))
                .reduce(Vector2f::add)
                .map(vec -> new Vector2f(vec.y(), -vec.x()))
                .orElse(new Vector2f());
    }
}
