#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=13) in vec2 texOffset;
layout (location=14) in float selectedInstanced;

out vec2 outTexCoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;
out vec4 mlightviewVertexPos;
out mat4 outModelViewMatrix;
out float outSelected;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;
uniform int numCols;
uniform int numRows;
uniform float selected;

void main() {
    outSelected = selected;
    vec4 initPos = vec4(position, 1.0);
    vec4 initNormal = vec4(vertexNormal, 0.0);

    vec4 mvPos = modelViewMatrix * initPos;
    gl_Position = projectionMatrix * mvPos;

    // Support for texture atlas, update texture coordinates
    float x = (texCoord.x / numCols + texOffset.x);
    float y = (texCoord.y / numRows + texOffset.y);
    outTexCoord = vec2(x, y);

    mvVertexNormal = normalize(modelViewMatrix * initNormal).xyz;
    mvVertexPos = mvPos.xyz;
    mlightviewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * initPos;
    outModelViewMatrix = modelViewMatrix;
}