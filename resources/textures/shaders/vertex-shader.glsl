#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 vertexColors;
layout (location = 2) in vec2 aTexture;

uniform mat4 matrix;
out vec4 fuckingColors;
out vec2 outTexture;

void main()
{
    gl_Position = matrix * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    fuckingColors = vertexColors;
    outTexture = aTexture;
}
