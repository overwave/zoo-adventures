#version 330

uniform mat4 viewProjectionMatrix;
uniform mat4 modelMatrix;

layout (location=0) in vec3 position;

void main(void) {
    gl_Position = viewProjectionMatrix * modelMatrix * vec4(position, 1.0);
}
