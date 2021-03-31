package dev.overtow.core;

import dev.overtow.math.Matrix;
import dev.overtow.math.Quaternion;
import dev.overtow.math.Vector2;
import dev.overtow.math.Vector3;
import dev.overtow.math.Vector4;
import dev.overtow.util.Utils;
import dev.overtow.util.misc.Tuple;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WaterActor implements Actor {
    private final List<Wave> waves;
    private final Quaternion rotation;
    private final List<Ripple> ripples;

    public WaterActor() {
        rotation = Quaternion.of();

        waves = List.of(
                new Wave(0, 0.008f, 3f, 0.0f, Vector2.of(9, 8)),

                new Wave(0, 0.02f, 0.5f, 0.0f, Vector2.of(2, 10)),

                new Wave(0, 0.05f, 1.5f, 0.2f, Vector2.of(9, 5)),
                new Wave(0, 0.05f, 1.5f, 0.2f, Vector2.of(5, 9))
//                new Wave(0.19f, 0.05f, 2f, 0.3f, new Vector2f(1, 1))
        );
        ripples = List.of(
                new Ripple(Vector2.of(-0.05f, -0.05f), Vector2.of(1, 0))
        );
    }

    public Tuple<Vector3, Quaternion> getWavesShift(Vector3 position, Vector2 size, float time) {
        Vector3[] positions = new Vector3[4];
        Quaternion[] rotations = new Quaternion[4];

        List<Tuple<Vector3, Quaternion>> tuples = Stream.of(
                Vector3.of(-size.getX() / 2, 0, -size.getY() / 2),
                Vector3.of(size.getX() / 2, 0, -size.getY() / 2),
                Vector3.of(-size.getX() / 2, 0, size.getY() / 2),
                Vector3.of(size.getX() / 2, 0, size.getY() / 2))
                .map(vector -> vector.plus(position))
                .map(vector -> getWavesShiftExact(vector, time)).collect(Collectors.toList());
        for (int i = 0; i < tuples.size(); i++) {
            Tuple<Vector3, Quaternion> tuple = tuples.get(i);
            positions[i] = tuple.getT();
            rotations[i] = tuple.getV();
        }

        Vector3 averagePosition = Vector3.of();
        for (Vector3 vec : positions) {
            averagePosition = averagePosition.plus(vec);
        }
        averagePosition = averagePosition.divide(4);

        Quaternion averageQuaternion = Quaternion.ofAverageRotation(rotations);
        return Tuple.of(averagePosition, averageQuaternion);
    }

    private Tuple<Vector3, Quaternion> getWavesShiftExact(Vector3 position, float time) {
        Vector4 reducedPosition = Vector4.of(position, 1);
        Matrix modelMatrix = Matrix.ofTranslationRotationScale(
                getPosition(),
                getRotation(),
                getScale());
        Matrix normalMatrix = modelMatrix.normal();
        Matrix invertedModelMatrix = modelMatrix.invert();

        Vector4 positionOnWater = reducedPosition.multiply(invertedModelMatrix);

        float resultOffsetY = 0;
        float tangentY = 0;
        float bitangentY = 0;

        for (Wave wave : waves) {

            Vector2 offset = positionOnWater.getXZ().multiply(wave.getDirection());

            resultOffsetY += Math.sin(wave.getSpeed() * -time + (offset.getX() + offset.getY()) / wave.getLength()) * wave.getAmplitude();
            tangentY += Math.cos(wave.getSpeed() * -time + (offset.getX() + offset.getY()) / wave.getLength()) * wave.getAmplitude() * wave.getDirection().getX() / wave.getLength();
            bitangentY += Math.cos(wave.getSpeed() * -time + (offset.getX() + offset.getY()) / wave.getLength()) * wave.getAmplitude() * wave.getDirection().getY() / wave.getLength();
        }
        Vector3 resultNormal = Vector3.of(0, bitangentY, 1).cross(Vector3.of(1, tangentY, 0)).normalize();

        Vector4 mul = Vector4.of(resultNormal, 1).multiply(normalMatrix);
        resultNormal = mul.getXYZ().normalize();

        Vector3 defaultNormal = Vector3.of(0, 1, 0);
        Quaternion rotationQuaternion = Quaternion.ofRotationTo(defaultNormal, resultNormal);

        Vector4 temp = Vector4.of(0, resultOffsetY, 0, 1).multiply(modelMatrix);
        temp = temp.plus(Vector4.of(getPosition(), 0));
        return Tuple.of(temp.getXYZ(), rotationQuaternion);
    }

    @Override
    public void update() {
//        waves.get(0).setDirection(new Vector2f((float) Math.cos(Utils.getTime()/3), (float) Math.sin(Utils.getTime()/3)));
    }

    @Override
    public Vector3 getPosition() {
        return Vector3.of(0, -0.02f, 0);
    }

    @Override
    public Vector3 getScale() {
        return Vector3.of(10, 1, 10);
    }

    @Override
    public Quaternion getRotation() {
        return rotation;
    }

    @Override
    public Mesh.Id getMeshId() {
        return Mesh.Id.WATER;
    }

    @Override
    public Vector4 getBackgroundColor() {
        return Utils.NO_BACKGROUND_COLOR;
    }

    public List<Wave> getWaves() {
        return waves;
    }

    public List<Ripple> getRipples() {
        return ripples;
    }

    public Vector2 getWavesDirection() {
        return Vector2.of(1, 0);
//        return waves.stream()
//                .map(wave -> wave.getDirection().multiply(wave.getSpeed()))
//                .reduce(Vector2f::add)
//                .map(vec -> new Vector2f(vec.y(), -vec.x()))
//                .orElse(new Vector2f());
    }
}
