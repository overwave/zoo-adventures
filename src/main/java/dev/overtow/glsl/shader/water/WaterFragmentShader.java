package dev.overtow.glsl.shader.water;

import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.shader.FragmentShader;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

import static dev.overtow.core.shader.uniform.Uniform.Name.LIGHT_POSITION;
import static dev.overtow.core.shader.uniform.Uniform.Name.TEXTURE_SAMPLER;
import static dev.overtow.glsl.GlslLibrary.*;

public class WaterFragmentShader implements FragmentShader {
    private final WaterVertexShader parentShader = new WaterVertexShader();

    @Uniform(TEXTURE_SAMPLER)
    private final Sampler2D textureSampler = new Sampler2D();
    @Uniform(LIGHT_POSITION)
    private final Vec3 lightPosition = vec3(0);

    @Input
    private final Vec3 worldPosition = parentShader.worldPosition;
    @Input
    private final Vec3 worldNormal = parentShader.worldNormal;
    @Input
    private final Vec2 textureCoordinate = parentShader.textureCoordinate;

    @Output
    private Vec4 fragColor;

    public void main() {
        /* do standard lambertian/diffuse lighting */
        double diffuse = max(0.0, dot(normalize(lightPosition.minus(worldPosition)), worldNormal));

        Vec4 background = texture(textureSampler, textureCoordinate);

        Vec3 shadowColor = vec3(-0.15, -0.15, 0);
        fragColor = vec4(background.rgb.plus(shadowColor.multiply(diffuse)), 0.6);
    }
}
