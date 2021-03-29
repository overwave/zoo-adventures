package dev.overtow.glsl.shader.water;

import dev.overtow.glsl.Input;
import dev.overtow.glsl.Output;
import dev.overtow.glsl.Uniform;
import dev.overtow.glsl.shader.FragmentShader;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec4;

import static dev.overtow.core.shader.uniform.Uniform.Name.TEXTURE_MOVING_DIRECTION;
import static dev.overtow.core.shader.uniform.Uniform.Name.TEXTURE_SAMPLER;
import static dev.overtow.core.shader.uniform.Uniform.Name.TIME;
import static dev.overtow.glsl.GlslLibrary.float_;
import static dev.overtow.glsl.GlslLibrary.fract;
import static dev.overtow.glsl.GlslLibrary.texture;
import static dev.overtow.glsl.GlslLibrary.vec2;

public class WaterFragmentShader implements FragmentShader {
    public static final double WATER_TRANSPARENCY = 0.55;
    private final WaterVertexShader parentShader = new WaterVertexShader();

    @Uniform(TEXTURE_SAMPLER)
    private final Sampler2D textureSampler = new Sampler2D();
    @Uniform(TIME)
    private final float time = float_(0);
    @Uniform(TEXTURE_MOVING_DIRECTION)
    private final Vec2 textureMovingDirection = vec2(0);

    @Input
    private final Vec2 textureCoordinate = parentShader.textureCoordinate;

    @Output
    private Vec4 fragColor;

    public void main() {
        Vec2 textureDirection = textureMovingDirection.multiply(time).multiply(0.001);
        Vec2 coordinate = fract(textureCoordinate.plus(textureDirection).multiply(0.7));
        Vec4 background = texture(textureSampler, coordinate);
        background.a = WATER_TRANSPARENCY;

        fragColor = background;
    }
}
