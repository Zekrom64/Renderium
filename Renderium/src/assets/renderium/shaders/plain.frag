layout(location = 4)
in vec3 fragNormal;
layout(location = 5)
in vec2 fragTexCoord;
layout(location = 6)
in vec4 fragColor;

layout(location = 0)
out vec4 outColor;

void main() {
	outColor = texture(uTexture, fragTexCoord) * fragColor;
}
