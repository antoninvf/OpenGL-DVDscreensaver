#version 330 core

out vec4 FragColor;

in vec4 fuckingColors;
in vec2 outTexture;

uniform sampler2D ourTexture;

void main()
{
    FragColor = texture(ourTexture, outTexture);
    //FragColor = fuckingColors;
}