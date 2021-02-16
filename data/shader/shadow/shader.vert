#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

uniform mat4 modelNonInstancedMatrix;
uniform mat4 lightViewMatrix;
uniform mat4 orthoProjectionMatrix;

void main() {
    gl_Position = orthoProjectionMatrix * lightViewMatrix * modelNonInstancedMatrix * vec4(position, 1.0);
}
