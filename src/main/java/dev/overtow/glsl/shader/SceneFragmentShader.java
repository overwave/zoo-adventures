package dev.overtow.glsl.shader;

import dev.overtow.glsl.Fragment;
import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

import static dev.overtow.glsl.GlslLibrary.*;

@Fragment
public class SceneFragmentShader {
    public static final float DEPTH_OFFSET = 0.00005f;
    public static final float LIGHT_INTENSITY = 0.3f;
    public static final float AMBIENT = 0.3f;

    @Uniform
    private final Sampler2D textureSampler = new Sampler2D();
    @Uniform
    private final Sampler2D depthTexture = new Sampler2D();
    @Uniform
    private final Vec3 lightPosition = vec3(0);

    @Input
    private final Vec4 lightBiasedClipPosition = vec4(0);
    @Input
    private final Vec3 worldPosition = vec3(0);
    @Input
    private final Vec3 worldNormal = vec3(0);
    @Input
    private final Vec2 textureCoord = vec2(0);

    @Output
    private Vec4 fragColor;

    double rand(Vec2 co) {
        return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
    }

    void main() {
        /* Convert the linearly interpolated clip-space position to NDC */
        Vec4 lightNDCPosition = lightBiasedClipPosition.divide(lightBiasedClipPosition.w);

        /* Sample the depth from the depth texture */
        Vec4 depth = texture(depthTexture, lightNDCPosition.xy);

        /* Additionally, do standard lambertian/diffuse lighting */
        double dot = max(0.0, dot(normalize(lightPosition.minus(worldPosition)), worldNormal));


        Vec4 backColor = vec4(0.9019608, 1.0, 0.1764706, 1);
        double noise = 1.0 - rand(round(textureCoord.multiply(50))) / 10.0;
        Vec4 background = texture(textureSampler, textureCoord);
        background.rgb = mix(backColor.rgb.multiply(noise), background.rgb, background.a).multiply(0.7);
        fragColor = background;
//        fragColor = vec4(AMBIENT, AMBIENT, AMBIENT, 1.0);

        /* "in shadow" test... */
        if (depth.z < lightNDCPosition.z - DEPTH_OFFSET) {
            fragColor = fragColor.minus(vec4(LIGHT_INTENSITY, LIGHT_INTENSITY, LIGHT_INTENSITY, 1.0).multiply(dot));
        } else {
            fragColor = fragColor.plus(vec4(LIGHT_INTENSITY, LIGHT_INTENSITY, LIGHT_INTENSITY, 1.0).multiply(dot));
        }
    }

}
