#version 330

const int NUM_CASCADES = 3;

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;
out vec4 mlightviewVertexPos[NUM_CASCADES];
out mat4 outModelViewMatrix;
out float outSelected;

uniform mat4 lightViewMatrix[NUM_CASCADES];
uniform mat4 orthoProjectionMatrix[NUM_CASCADES];
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;
uniform float selected;

void main() {
    outSelected = selected;
    vec4 initPos = vec4(position, 1.0);
    vec4 initNormal = vec4(vertexNormal, 0.0);

    mat4 modelViewMatrix =  viewMatrix * modelMatrix;
    vec4 mvPos = modelViewMatrix * initPos;
    gl_Position = projectionMatrix * mvPos;

    outTexCoord = texCoord;

    mvVertexNormal = normalize(modelViewMatrix * initNormal).xyz;
    mvVertexPos = mvPos.xyz;
    for (int i = 0; i < NUM_CASCADES; i++) {
        mlightviewVertexPos[i] = orthoProjectionMatrix[i] * lightViewMatrix[i] * modelMatrix * initPos;
    }
    outModelViewMatrix = modelViewMatrix;
}