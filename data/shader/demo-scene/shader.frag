#version 330

#define DEPTH_OFFSET 0.00000
#define LIGHT_INTENSITY 0.9
#define AMBIENT 0.1

uniform sampler2D depthTexture;
uniform vec3 lightPosition;

in vec4 lightBiasedClipPosition;
in vec3 worldPosition;
in vec3 worldNormal;

out vec4 fragColor;

void main(void) {
    /* Convert the linearly interpolated clip-space position to NDC */
    vec4 lightNDCPosition = lightBiasedClipPosition / lightBiasedClipPosition.w;

    /* Sample the depth from the depth texture */
    vec4 depth = texture2D(depthTexture, lightNDCPosition.xy);

    /* Additionally, do standard lambertian/diffuse lighting */
    float dot = max(0.0, dot(normalize(lightPosition - worldPosition), worldNormal));

    fragColor = vec4(AMBIENT, AMBIENT, AMBIENT, 1.0);

    /* "in shadow" test... */
    if (depth.z >= lightNDCPosition.z - DEPTH_OFFSET) {
        /* lit */
        fragColor += vec4(LIGHT_INTENSITY, LIGHT_INTENSITY, LIGHT_INTENSITY, 1.0) * dot;
    }
}
