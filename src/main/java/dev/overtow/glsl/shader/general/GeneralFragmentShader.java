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
    private static final float LIGHT_INTENSITY = 0.3f;

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
        /* Convert the linearly interpolated clip-space position to NDC */
        Vec4 lightNDCPosition = lightBiasedClipPosition.divide(lightBiasedClipPosition.w);

        /* Sample the depth from the depth texture */
        Vec4 depth = texture(depthTexture, lightNDCPosition.xy);

        /* Additionally, do standard lambertian/diffuse lighting */
        double dot = max(0.0, dot(normalize(lightPosition.minus(worldPosition)), worldNormal));


        Vec4 backColor = vec4(0.9019608, 1.0, 0.1764706, 1);
        double noise = 1.0 - rand(round(textureCoordinate.multiply(50))) / 10.0;
        Vec4 background = texture(textureSampler, textureCoordinate);
        background.rgb = mix(backColor.rgb.multiply(noise), background.rgb, background.a).multiply(0.7);
        fragColor = vec4(background.rgb, backColor.a);

        /* "in shadow" test... */
        if (depth.z < lightNDCPosition.z - DEPTH_OFFSET) {
            fragColor.rgb = fragColor.rgb.minus(vec3(LIGHT_INTENSITY).multiply(dot));
        } else {
            fragColor.rgb = fragColor.rgb.plus(vec3(LIGHT_INTENSITY).multiply(dot));
        }
    }

}
