#version 150

uniform sampler2D DiffuseSampler;
uniform float Slider;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec3 color = texture(DiffuseSampler, uv).rgb;

    float s = clamp(Slider, 0.0, 1.0);

    vec2 center = vec2(0.5, 0.5);
    float dist = distance(center, uv) * 1.414213;

    float OuterVig = 1.0;
    float InnerVig = 1.0 - s;
    float range = OuterVig - InnerVig;
    float vig;
    if (range < 0.0001) {
        vig = 1.0;
    } else {
        vig = clamp((OuterVig - dist) / range, 0.0, 1.0);
    }

    color *= vig;

    float darken = s * 0.5;
    color *= (1.0 - darken);

    fragColor = vec4(color, 1.0);
}