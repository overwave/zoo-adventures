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
    @Uniform(BACKGROUND_COLOR)
    private final Vec4 backgroundColor = vec4(0);
    @Uniform(SHADOWS_ANTIALIASING_LEVEL)
    private final Vec3 shadowsAntialiasingLevel = vec3(0);
    @Uniform(ITEM_SELECTED)
    private final float itemSelected = float_(0);

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

    public void main() {
        /* standard lambertian/diffuse lighting */
        double dot = max(0.0, dot(normalize(lightPosition.minus(worldPosition)), worldNormal));

        Vec4 backColor = backgroundColor;
        Vec4 background = texture(textureSampler, textureCoordinate);

        background.rgb = mix(backColor.rgb, background.rgb, background.a);
        fragColor = vec4(background.rgb.multiply(0.6), backColor.a);

        Vec4 lightNDCPosition = lightBiasedClipPosition.divide(lightBiasedClipPosition.w);
        double shadowFactor = 0.0;
        Vec2 increment = vec2(1.0).divide(textureSize(depthTexture, 0));

        for (double row = shadowsAntialiasingLevel.x; row < shadowsAntialiasingLevel.y; row++) {
            for (double col = shadowsAntialiasingLevel.x; col < shadowsAntialiasingLevel.y; col++) {
                Vec2 depthTextureOffset = vec2(row, col).multiply(increment);
                double depth = texture(depthTexture, lightNDCPosition.xy.plus(depthTextureOffset)).z;

                shadowFactor += lightNDCPosition.z - DEPTH_OFFSET > depth ? 0.0 : 1.0;
            }
        }

        Vec3 light = vec3(LIGHT_INTENSITY);
        fragColor = fragColor.plus(vec4(light.multiply(shadowFactor / shadowsAntialiasingLevel.z), 0).multiply(dot));
        fragColor.r += 0.2 * itemSelected;
    }
}
