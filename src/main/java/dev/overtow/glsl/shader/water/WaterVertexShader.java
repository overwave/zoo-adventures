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

    public static final int WAVES_AMOUNT = 3;

    @Uniform(VIEW_PROJECTION_MATRIX)
    private final Mat4 viewProjectionMatrix = mat4(0);
    @Uniform(LIGHT_VIEW_PROJECTION_MATRIX)
    private final Mat4 lightViewProjectionMatrix = mat4(0);
    @Uniform(BIAS_MATRIX)
    private final Mat4 biasMatrix = mat4(0);
    @Uniform(MODEL_MATRIX)
    private final Mat4 modelMatrix = mat4(0);
    @Uniform(TIME)
    private final float time = float_(0);
    @Array(WAVES_AMOUNT)
    @Uniform(WAVES)
    private final Wave[] waves = new Wave[]{};

    @Input(location = 0)
    private final Vec3 position = vec3(0);
    @Input(location = 1)
    private final Vec2 texture = vec2(0);
    @Input(location = 2)
    private final Vec3 normal = vec3(0);

    @Output
    Vec4 lightBiasedClipPosition;
    @Output
    Vec3 worldPosition;
    @Output
    Vec3 worldNormal;
    @Output
    Vec2 textureCoordinate;
    @Output(shown = false)
    Vec4 gl_Position;

//    Vec3 gerstnerWave(Vec3 position) {
//        Vec3 gridPoint = position;
//        Vec3 tangent = vec3(0);
//        Vec3 binormal = vec3(0);
//        Vec3 p = gridPoint;
//
//
//
//        for (int i = 0; i < WAVES_AMOUNT; i++) {
//            double omega = 2 / waves[i].length;
//            double steepnessNormalized = clamp(waves[i].steepness, 0, 1 / (omega * waves[i].amplitude));
//            double phi = waves[i].speed * (2 / waves[i].length);
//
//            double dottedPosition = dot(waves[i].direction, position.xz.multiply(1000));
//            double positionShift = cos(omega * dottedPosition + phi * time);
//
//            position.xz = position.xz.plus(waves[i].direction.multiply(positionShift * steepnessNormalized * waves[i].amplitude));
//            position.y += waves[i].amplitude * sin(dottedPosition * omega + time * phi);
//        }
//
//
//        p += GerstnerWave(_WaveA, gridPoint, tangent, binormal);
//        p += GerstnerWave(_WaveB, gridPoint, tangent, binormal);
//        p += GerstnerWave(_WaveC, gridPoint, tangent, binormal);
//        Vec3 normal = normalize(cross(binormal, tangent));
//        vertexData.vertex.xyz = p;
//        vertexData.normal = normal;
//
////        for (int i = 0; i < WAVES_AMOUNT; i++) {
////            double omega = 2 / waves[i].length;
////            double steepnessNormalized = clamp(waves[i].steepness, 0, 1 / (omega * waves[i].amplitude));
////            double phi = waves[i].speed * (2 / waves[i].length);
////
////            double dottedPosition = dot(waves[i].direction, position.xz.multiply(1000));
////            double positionShift = cos(omega * dottedPosition + phi * time);
////
////            position.xz = position.xz.plus(waves[i].direction.multiply(positionShift * steepnessNormalized * waves[i].amplitude));
////            position.y += waves[i].amplitude * sin(dottedPosition * omega + time * phi);
////        }
////
////        return position;
//    }

    @Override
    public void main() {
        Vec3 p = position;
        Vec3 tangent = vec3(0);
        Vec3 binormal = vec3(0);

        for (int i = 0; i < WAVES_AMOUNT; i++) {
            double steepness = waves[i].steepness;
            double wavelength = waves[i].length / 10;
            double k = waves[i].amplitude / wavelength;
            double c = sqrt(waves[i].speed / k);
            Vec2 d = waves[i].direction;
            double f = k * (dot(d, position.xz) - c * time / 5);
            double a = steepness / k;

            double v = steepness * sin(f);

            tangent = tangent.plus(vec3(
                    -d.x * d.x * v,
                    d.x * (steepness * cos(f)),
                    -d.x * d.y * v
            ));
            binormal = binormal.plus(vec3(
                    -d.x * d.y * v,
                    d.y * (steepness * cos(f)),
                    -d.y * d.y * v
            ));
            p = p.plus(vec3(
                    d.x * (a * cos(f)),
                    a * sin(f),
                    d.y * (a * cos(f))
            ));
        }

        Vec4 modelPosition = modelMatrix.multiply(vec4(p, 1));

        worldPosition = modelPosition.xyz;
        worldNormal = normalize(cross(binormal, tangent));
//        worldNormal = normal;
        textureCoordinate = texture;

        /* Compute vertex position as seen from
           the light and use linear interpolation when passing it
           to the fragment shader
        */
        lightBiasedClipPosition = biasMatrix.multiply(lightViewProjectionMatrix).multiply(modelPosition);

        /* Normally transform the vertex */
        gl_Position = viewProjectionMatrix.multiply(modelPosition);
    }
}
