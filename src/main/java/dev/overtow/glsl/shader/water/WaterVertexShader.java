package dev.overtow.glsl.shader.water;

import dev.overtow.glsl.Array;
import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.shader.VertexShader;
import dev.overtow.glsl.type.Mat4;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;
import dev.overtow.glsl.type.struct.Ripple;
import dev.overtow.glsl.type.struct.Wave;

import static dev.overtow.core.shader.uniform.Uniform.Name.*;
import static dev.overtow.glsl.GlslLibrary.*;

public class WaterVertexShader implements VertexShader {

    public static final int WAVES_AMOUNT = 4;
    public static final int RIPPLES_AMOUNT = 1;

    private static final double RIPPLE_SPEED = 3;
    private static final double RIPPLE_WAVELENGTH = 0.006f;
    private static final double RIPPLE_AMPLITUDE = 0.08;
    private static final double SQRT_2 = 1.41421356237;

    @Uniform(VIEW_PROJECTION_MATRIX)
    private final Mat4 viewProjectionMatrix = mat4(0);
    @Uniform(NORMAL_MATRIX)
    private final Mat4 normalMatrix = mat4(0);
    @Uniform(MODEL_MATRIX)
    private final Mat4 modelMatrix = mat4(0);
    @Uniform(TIME)
    private final float time = float_(0);
    @Array(WAVES_AMOUNT)
    @Uniform(WAVES)
    private final Wave[] waves = new Wave[]{};
    @Array(RIPPLES_AMOUNT)
    @Uniform(RIPPLES)
    private final Ripple[] ripples = new Ripple[]{};

    @Input(location = 0)
    private final Vec3 inputPosition = vec3(0);
    @Input(location = 1)
    private final Vec2 texture = vec2(0);
    @Input(location = 2)
    private final Vec3 normal = vec3(0);

    @Output
    Vec3 worldPosition;
    @Output
    Vec3 worldNormal;
    @Output
    Vec2 textureCoordinate;
    @Output(shown = false)
    Vec4 gl_Position;

    @Override
    public void main() {
        Vec3 resultPosition = inputPosition;

        for (int i = 0; i < WAVES_AMOUNT; i++) {
            Wave wave = waves[i];

            Vec2 reducedPosition = wave.direction.multiply(inputPosition.xz);
            resultPosition.y += sin(wave.speed * -time + (reducedPosition.x + reducedPosition.y) / wave.length) * wave.amplitude;
        }
        for (int i = 0; i < RIPPLES_AMOUNT; i++) {
            Ripple ripple = ripples[i];
            Vec2 relativePosition = inputPosition.xz.minus(ripple.center);
            double distanceFromRippleCenter = length(relativePosition);
            Vec2 vectorToRippleDirection = ripple.direction.minus(normalize(relativePosition));
            double vectorLength = length(vectorToRippleDirection);

            if (distanceFromRippleCenter > 0.07 && distanceFromRippleCenter < 0.1 && vectorLength < SQRT_2) {
                resultPosition.y += sin(RIPPLE_SPEED * time + distanceFromRippleCenter / RIPPLE_WAVELENGTH) * RIPPLE_AMPLITUDE;
            }


            Vec2 pointOnDirection = ripple.center.plus(ripple.direction);
            Vec2 perpendicular = vec2(pointOnDirection.y - ripple.center.y, ripple.center.x - pointOnDirection.x);
            double L = (ripple.center.x * pointOnDirection.y - pointOnDirection.x * ripple.center.y +
                        ripple.center.y * relativePosition.x - pointOnDirection.y * relativePosition.x +
                        pointOnDirection.x * relativePosition.y - ripple.center.x * relativePosition.y) /
                       (perpendicular.x * (pointOnDirection.y - ripple.center.y) + perpendicular.y * (ripple.center.x - pointOnDirection.x));
            Vec2 H = vec2(relativePosition.x + perpendicular.x * L, relativePosition.y + perpendicular.y * L);  // высота

            double lengthH = length(H);
            double finsDistance = distance(ripple.direction.multiply(-lengthH), relativePosition);

            if (lengthH < 0.3 && finsDistance > 0.07 && finsDistance < 0.1) {
                resultPosition.y += sin(RIPPLE_SPEED * time + (relativePosition.x + relativePosition.y) / RIPPLE_WAVELENGTH) * RIPPLE_AMPLITUDE;
            }
//            double directionToRippleCenterDistance = length(ripple.direction.yx.multiply(relativePosition).plus(ripple.center)) / length(ripple.direction);

//            if (directionToRippleCenterDistance > 0.07 && directionToRippleCenterDistance < 0.1) {
//                Vec2 reducedPosition = ripple.direction.multiply(relativePosition);
//
//                resultPosition.y += sin(RIPPLE_SPEED * time + (reducedPosition.x + reducedPosition.y) / RIPPLE_WAVELENGTH) * RIPPLE_AMPLITUDE;
//            }
        }

        Vec3 resultNormal = normalize(inputPosition.minus(resultPosition).multiply(vec3(1, -1, 1)));

        Vec4 modelPosition = modelMatrix.multiply(vec4(resultPosition, 1));
        worldPosition = modelPosition.xyz;
        worldNormal = normalize(normalMatrix.multiply(vec4(resultNormal, 1)).xyz);
        textureCoordinate = texture;

        gl_Position = viewProjectionMatrix.multiply(modelPosition);
    }
}
