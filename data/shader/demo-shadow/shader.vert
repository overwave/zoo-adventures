#version 330

uniform mat4 viewProjectionMatrix;

layout (location=0) in vec3 position;

void main(void) {
    gl_Position = viewProjectionMatrix * vec4(position, 1.0);
}
