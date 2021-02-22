#version 330

uniform mat4 viewProjectionMatrix;
uniform mat4 lightViewProjectionMatrix;
uniform mat4 biasMatrix;
uniform mat4 modelMatrix;

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;

out vec4 lightBiasedClipPosition;
out vec3 worldPosition;
out vec3 worldNormal;

void main(void) {
    /* Pass the position and normal to the fragment shader
       (we do lighting computations in world coordinates) */
    worldPosition = (modelMatrix * vec4(position, 1)).xyz;
    worldNormal = normal;

    /* Compute vertex position as seen from
       the light and use linear interpolation when passing it
       to the fragment shader
    */
    lightBiasedClipPosition = biasMatrix * lightViewProjectionMatrix * modelMatrix * vec4(position, 1.0);

    /* Normally transform the vertex */
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(position, 1.0);
}
