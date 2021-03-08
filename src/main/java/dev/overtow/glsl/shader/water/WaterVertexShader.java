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
        double scale = 100;

        Vec3 resultPosition = inputPosition;
        Vec3 resultNormal = normal;

        for (int i = 0; i < WAVES_AMOUNT; i++) {
            Wave wave = waves[i];
            double reducedSteepness = wave.steepness / (wave.amplitude * wave.length);
            double xDelta = reducedSteepness * wave.amplitude * wave.direction.x * cos(dot(wave.direction.multiply(wave.length), inputPosition.xz) + wave.speed * time) / scale;
            double zDelta = reducedSteepness * wave.amplitude * wave.direction.y * cos(dot(wave.direction.multiply(wave.length), inputPosition.xz) + wave.speed * time) / scale;
            double yDelta = wave.amplitude * sin(dot(wave.direction.multiply(wave.length), inputPosition.xz) + wave.speed * time);

            resultPosition = resultPosition.plus(vec3(xDelta, yDelta, zDelta));
        }

        for (int i = 0; i < WAVES_AMOUNT; i++) {
            Wave wave = waves[i];
            double reducedSteepness = wave.steepness / (wave.amplitude * wave.length);

            Vec3 direction2 = vec3(wave.direction.x, 0, wave.direction.y);
            double normalDeltaX = wave.direction.x * wave.length * wave.amplitude * cos(wave.length * dot(direction2, resultPosition) + wave.speed * time) / scale;
            double normalDeltaZ = wave.direction.y * wave.length * wave.amplitude * cos(wave.length * dot(direction2, resultPosition) + wave.speed * time) / scale;
            double normalDeltaY = reducedSteepness * wave.length * wave.amplitude * sin(wave.length * dot(direction2, resultPosition) + wave.speed * time);
            resultNormal = resultNormal.minus(vec3(normalDeltaX, normalDeltaY, normalDeltaZ));
        }

        Vec4 modelPosition = modelMatrix.multiply(vec4(resultPosition, 1));

        worldPosition = modelPosition.xyz;
        worldNormal = modelMatrix.multiply(vec4(resultNormal, 1)).xyz;
        textureCoordinate = texture;

        /* Normally transform the vertex */
        gl_Position = viewProjectionMatrix.multiply(modelPosition);
    }
}
