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

    public static final int WAVES_AMOUNT = 1;

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

    Vec3 gerstnerWave(Vec3 position) {
        for (int i = 0; i < WAVES_AMOUNT; i++) {
            double omega = 2 / waves[i].length;
            double steepnessNormalized = clamp(waves[i].steepness, 0, 1 / (omega * waves[i].amplitude));
            double phi = waves[i].speed * (2 / waves[i].length);

            double dottedPosition = dot(waves[i].direction, position.xz.multiply(100));
            double positionShift = cos(omega * dottedPosition + phi * time);

            position.xz = position.xz.minus(waves[i].direction.multiply(positionShift * steepnessNormalized * waves[i].amplitude));
            position.y += waves[i].amplitude * sin(dottedPosition * omega + time * phi);
        }

        return position;
    }

    @Override
    public void main() {
        Vec4 modelPosition = modelMatrix.multiply(vec4(gerstnerWave(position), 1));

        worldPosition = modelPosition.xyz;
        worldNormal = normal;
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
