#version 150

uniform sampler2D DiffuseSampler;
uniform float Slider;

in vec2 texCoord;

out vec4 fragColor;

void main(void)
{
    float OuterVig = 1.0f; // Position for the Outer vignette

    float InnerVig = 1.0f; // Position for the inner Vignette Ring

    InnerVig = mix(1.0f, -1.0f, Slider);
    InnerVig = 0.0f;

    float blackOverlay = clamp(1.0f + InnerVig, 0.0f, 1.0f);

    vec2 uv = gl_FragCoord.xy;
    vec3 color = texture2D(DiffuseSampler, uv).rgb;
    vec2 center = vec2(0.5f,0.5f); // Center of Screen

    float dist  = distance(center,uv )*1.414213f; // Distance  between center and the current Uv. Multiplyed by 1.414213 to fit in the range of 0.0 to 1.0

    float vig = clamp((OuterVig-dist) / (OuterVig-InnerVig), 0.0f, 1.0f); // Generate the Vignette with Clamp which go from outer Viggnet ring to inner vignette ring with smooth steps

    color *= vig;

    color *= blackOverlay;

    fragColor = vec4(color, 1.0);
}