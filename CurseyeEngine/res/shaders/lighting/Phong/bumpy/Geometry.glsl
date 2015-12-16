#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoord1[];
in vec3 normal1[];

out vec2 texCoord2;
out vec3 position2;
out vec3 normal2;

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

flat out vec3 tangent;
uniform Material material;
uniform mat4 viewProjectionMatrix;
uniform vec4 clipplane;
uniform vec4 frustumPlanes[6];
uniform int displacement;

float displacement0, displacement1, displacement2;
vec4 displace0, displace1, displace2;

void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;

    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    float dU1 = texCoord1[1].x - texCoord1[0].x;
    float dV1 = texCoord1[1].y - texCoord1[0].y;
    float dU2 = texCoord1[2].x - texCoord1[0].x;
    float dV2 = texCoord1[2].y - texCoord1[0].y;

    float f = 1.0 / (dU1 * dV2 - dU2 * dV1);

    vec3 t;

    t.x = f * (dV2 * e1.x - dV1 * e2.x);
    t.y = f * (dV2 * e1.y - dV1 * e2.y);
    t.z = f * (dV2 * e1.z - dV1 * e2.z);
	
	tangent = normalize(t);
}

void main()
{	
	calcTangent();
	
	if (displacement == 1){
		displacement0 = texture(material.displacemap, texCoord1[0]).r * material.displaceScale;
		displacement1 = texture(material.displacemap, texCoord1[1]).r * material.displaceScale;
		displacement2 = texture(material.displacemap, texCoord1[2]).r * material.displaceScale;
	
		displace0 = vec4(normal1[0] * displacement0, 0);
		displace1 = vec4(normal1[1] * displacement1, 0);
		displace2 = vec4(normal1[2] * displacement2, 0);
	}
	
	vec4 position00 = gl_in[0].gl_Position + displace0;
    gl_Position = viewProjectionMatrix * position00;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position00 ,clipplane);
	texCoord2 = texCoord1[0];
	position2 = (position00).xyz;
	normal2 = normal1[0];
    EmitVertex();
	
	vec4 position01 = gl_in[1].gl_Position + displace1;
	gl_Position = viewProjectionMatrix * position01;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position01 ,clipplane);
	texCoord2 = texCoord1[1];
	position2 = (position01).xyz;
	normal2 = normal1[1];
    EmitVertex();

	vec4 position02 = gl_in[2].gl_Position + displace2;
	gl_Position = viewProjectionMatrix * position02;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position02 ,clipplane);
	texCoord2 = texCoord1[2];
	position2 = (position02).xyz;
	normal2 = normal1[2];
    EmitVertex();
	
    EndPrimitive();
}