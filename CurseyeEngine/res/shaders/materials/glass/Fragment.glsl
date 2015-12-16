#version 330

in vec3 normal1;
in vec2 texCoord1;
in vec3 position1;


struct DirectionalLight
{
	vec3 direction;
	vec3 color;
	vec3 ambient;
	float intensity;
};

struct Material
{
	vec3 color;
	float shininess;
	float emission;
};
	
uniform vec3 eyePosition;
uniform DirectionalLight directionalLight;
uniform Material material;



float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition)
{
	vec3 reflectionVector = normalize(reflect(-direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float reflection = dot(vertexToEye, reflectionVector);
	
	return pow(reflection, material.shininess) * material.emission;
}

void main()
{	
	vec3 diffuseLight;
	vec3 specularLight;
	float diffuse;
	float specular;

	diffuse = diffuse(directionalLight.direction, normal1, directionalLight.intensity);
	
	if (diffuse == 0.0)
		specular = 0.0;
	else
		specular = specular(directionalLight.direction, normal1, eyePosition, position1);
	
	diffuseLight = directionalLight.color * diffuse;
	specularLight = directionalLight.color * specular;
	
	vec3 rgb = material.color * (directionalLight.ambient + diffuseLight) + specularLight;
	
	gl_FragColor = vec4(rgb,0.3);
}