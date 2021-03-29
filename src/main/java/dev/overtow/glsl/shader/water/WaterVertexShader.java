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
import dev.overtow.glsl.type.struct.Wave;

import static dev.overtow.core.shader.uniform.Uniform.Name.*;
import static dev.overtow.glsl.GlslLibrary.*;

public class WaterVertexShader implements VertexShader {

    public static final int WAVES_AMOUNT = 4;

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
//        Vec3 bitangent = vec3(0);
//        Vec3 tangent = vec3(0);

        for (int i = 0; i < WAVES_AMOUNT; i++) {
            Wave wave = waves[i];

//            double k = 2.0 * 3.1416 / wave.length; // wave length
//            double kA = k * wave.amplitude;
//            Vec2 D = normalize(wave.direction); // normalized direction
//            Vec2 K = D.multiply(k); // wave vector and magnitude (direction)


            // peak/crest steepness high means steeper, but too much
            // can cause the wave to become inside out at the top
            //  float Q = steepness; //max(steepness, 0.1);

//            double S = wave.speed;
//            double w = S * k; // Phase/frequency
//            double wT = w * time;
//
//             Calculate once instead of 4 times
//            double KPwT = dot(K, inputPosition.xz) - wT;
//            double S0 = sin(KPwT);
//            double C0 = cos(KPwT);
            Vec2 offset = wave.direction.multiply(inputPosition.xz);

            resultPosition.y += sin(wave.speed * -time + (offset.x + offset.y) / wave.length) * wave.amplitude;
//            resultPosition.xz = resultPosition.xz.minus(D.multiply(wave.steepness * wave.amplitude * S0));
//            resultPosition.y += wave.amplitude * C0;

//            bitangent = bitangent.plus(normalize(vec3(
//                    1 - (wave.steepness * D.x * D.x * kA * C0),
//                    D.x * kA * S0,
//                    -(wave.steepness * D.x * D.y * kA * C0)
//            )));
//            tangent = tangent.plus(normalize(vec3(
//                    -(wave.steepness * D.x * D.y * kA * C0),
//                    D.y * kA * S0,
//                    1 - (wave.steepness * D.y * D.y * kA * C0)
//            )));
        }
//        Vec3 resultNormal = normalize(cross(tangent, bitangent));
        Vec3 resultNormal = normalize(inputPosition.minus(resultPosition).multiply(vec3(1, -1, 1)));

        Vec4 modelPosition = modelMatrix.multiply(vec4(resultPosition, 1));
        worldPosition = modelPosition.xyz;
        worldNormal = normalize(normalMatrix.multiply(vec4(resultNormal, 1)).xyz);
        textureCoordinate = texture;

        /* Normally transform the vertex */
        gl_Position = viewProjectionMatrix.multiply(modelPosition);
    }
}
