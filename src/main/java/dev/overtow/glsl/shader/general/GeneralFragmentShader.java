package dev.overtow.glsl.shader.general;

import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.shader.FragmentShader;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

import static dev.overtow.core.shader.uniform.Uniform.Name.*;
import static dev.overtow.glsl.GlslLibrary.*;

public class GeneralFragmentShader implements FragmentShader {
    private static final float DEPTH_OFFSET = 0.00005f;
    private static final float LIGHT_INTENSITY = 0.4f;

    private final GeneralVertexShader parentShader = new GeneralVertexShader();

    @Uniform(TEXTURE_SAMPLER)
    private final Sampler2D textureSampler = new Sampler2D();
    @Uniform(DEPTH_TEXTURE)
    private final Sampler2D depthTexture = new Sampler2D();
    @Uniform(LIGHT_POSITION)
    private final Vec3 lightPosition = vec3(0);

    @Input
    private final Vec4 lightBiasedClipPosition = parentShader.lightBiasedClipPosition;
    @Input
    private final Vec3 worldPosition = parentShader.worldPosition;
    @Input
    private final Vec3 worldNormal = parentShader.worldNormal;
    @Input
    private final Vec2 textureCoordinate = parentShader.textureCoordinate;

    @Output
    private Vec4 fragColor;

    double rand(Vec2 co) {
        return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
    }

    public void main() {
        Vec4 lightNDCPosition = lightBiasedClipPosition.divide(lightBiasedClipPosition.w);

        Vec4 depth = texture(depthTexture, lightNDCPosition.xy);

        /* standard lambertian/diffuse lighting */
        double dot = max(0.0, dot(normalize(lightPosition.minus(worldPosition)), worldNormal));

        Vec4 backColor = vec4(249/255f, 161/255f, 149/255f, 1);
        Vec4 background = texture(textureSampler, textureCoordinate);

        background.rgb = mix(backColor.rgb, background.rgb, background.a);
        fragColor = vec4(background.rgb.multiply(0.6), backColor.a);

        /* "in shadow" test... */
        if (depth.z >= lightNDCPosition.z - DEPTH_OFFSET) {
            /* lit */
            fragColor = fragColor.plus(vec4(LIGHT_INTENSITY, LIGHT_INTENSITY, LIGHT_INTENSITY, 1.0).multiply(dot));
        }
    }
}
