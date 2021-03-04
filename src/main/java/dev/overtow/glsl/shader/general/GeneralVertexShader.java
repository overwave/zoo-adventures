package dev.overtow.glsl.shader.general;

import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.shader.VertexShader;
import dev.overtow.glsl.type.Mat4;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

import static dev.overtow.core.shader.uniform.Uniform.Name.*;
import static dev.overtow.glsl.GlslLibrary.*;

public class GeneralVertexShader implements VertexShader {

    @Uniform(VIEW_PROJECTION_MATRIX)
    private final Mat4 viewProjectionMatrix = mat4(0);
    @Uniform(LIGHT_VIEW_PROJECTION_MATRIX)
    private final Mat4 lightViewProjectionMatrix = mat4(0);
    @Uniform(BIAS_MATRIX)
    private final Mat4 biasMatrix = mat4(0);
    @Uniform(MODEL_MATRIX)
    private final Mat4 modelMatrix = mat4(0);

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

    @Override
    public void main() {
        Vec4 modelPosition = modelMatrix.multiply(vec4(position, 1));

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
