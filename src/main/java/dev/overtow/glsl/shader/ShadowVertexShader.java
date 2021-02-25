package dev.overtow.glsl.shader;

import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.type.Mat4;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

import static dev.overtow.core.shader.uniform.Uniform.Name.MODEL_MATRIX;
import static dev.overtow.core.shader.uniform.Uniform.Name.VIEW_PROJECTION_MATRIX;
import static dev.overtow.glsl.GlslLibrary.*;

public class ShadowVertexShader implements Shader {

    @Uniform(VIEW_PROJECTION_MATRIX)
    private final Mat4 viewProjectionMatrix = mat4(0);
    @Uniform(MODEL_MATRIX)
    private final Mat4 modelMatrix = mat4(0);

    @Input(location = 0)
    private final Vec3 position = vec3(0);

    @Output(shown = false)
    Vec4 gl_Position;

    @Override
    public void main() {
        gl_Position = viewProjectionMatrix.multiply(modelMatrix).multiply(vec4(position, 1.0));
    }
}
