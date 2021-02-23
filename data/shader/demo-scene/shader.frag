#version 330

#define DEPTH_OFFSET 0.00005
#define LIGHT_INTENSITY 0.5
#define AMBIENT 0.5

uniform sampler2D textureSampler;
uniform sampler2D depthTexture;
uniform vec3 lightPosition;

in vec4 lightBiasedClipPosition;
in vec3 worldPosition;
in vec3 worldNormal;
in vec2 textureCoord;

out vec4 fragColor;

float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main(void) {
    /* Convert the linearly interpolated clip-space position to NDC */
    vec4 lightNDCPosition = lightBiasedClipPosition / lightBiasedClipPosition.w;

    /* Sample the depth from the depth texture */
    vec4 depth = texture(depthTexture, lightNDCPosition.xy);

    /* Additionally, do standard lambertian/diffuse lighting */
    float dot = max(0.0, dot(normalize(lightPosition - worldPosition), worldNormal));


    //    vec4 backColor = vec4(0.9019608f, 1.0f, 0.1764706f, 1);
    //    float noise = 1.0 - rand(round(textureCoord * 50)) / 10.0;
    //    vec4 background = texture(textureSampler, textureCoord);
    //    background.rgb = mix(noise * backColor.rgb, background.rgb, background.a);
    //    fragColor = background;
    fragColor = vec4(AMBIENT, AMBIENT, AMBIENT, 1.0);

    /* "in shadow" test... */
    if (depth.z < lightNDCPosition.z - DEPTH_OFFSET) {
        fragColor -= vec4(LIGHT_INTENSITY, LIGHT_INTENSITY, LIGHT_INTENSITY, 1.0) * dot / 2;
    } else {
        /* lit */
        fragColor += vec4(LIGHT_INTENSITY, LIGHT_INTENSITY, LIGHT_INTENSITY, 1.0) * dot / 2;
    }
}
