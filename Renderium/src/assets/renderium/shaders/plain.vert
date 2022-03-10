layout(location = 0)
in vec3 inPosition;
layout(location = 1)
in vec3 inNormal;
layout(location = 2)
in vec2 inTexCoord;
layout(location = 3)
in vec4 inColor;

layout(location = 4)
out vec3 fragNormal;
layout(location = 5)
out vec2 fragTexCoord;
layout(location = 6)
out vec4 fragColor;

void main() {
	gl_Position = uGlobals.mTransform * vec4(inPosition, 1.0);

	fragNormal = inNormal;
	fragTexCoord = inTexCoord;
	fragColor = inColor;
}
