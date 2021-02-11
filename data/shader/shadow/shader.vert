#version 330

layout (location=0) in vec3 position;

//uniform mat4 modelLightViewMatrix;
//uniform mat4 orthoProjectionMatrix;
uniform mat4 viewProjectionMatrix;

void main() {
    gl_Position = viewProjectionMatrix * vec4(position, 1.0);
//    gl_Position = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
}
