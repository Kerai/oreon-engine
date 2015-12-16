#version 430
#define M_PI 3.1415926535897932384626433832795

in vec3 positionF;
in vec2 texCoordF;
flat in vec3 tangent;

struct DirectionalLight
{
	float intensity;
	vec3 ambient;
	vec3 direction;
	vec3 color;
};


uniform mat4 modelViewProjectionMatrix;
uniform DirectionalLight sunlight;
uniform sampler2D waterReflection;
uniform sampler2D waterRefraction;
uniform sampler2D dudv;
uniform sampler2D normalmap;
uniform float distortion;
uniform vec3 eyePosition;
uniform float kReflection;
uniform float kRefraction;
uniform int windowWidth;
uniform int windowHeight;
uniform int texDetail;
uniform float emission;
uniform float shininess;


const vec4 refractionColor =vec4(0.02,0.03,0.06,1.0f);
const vec4 reflectionColor =vec4(0.4,0.6,0.9,1.0f);
const float zFar = 10000;
const vec4 fogColor = vec4(0.1,0.15,0.25,0);

const float R = 0.0403207622; 
float SigmaSqX;
float SigmaSqY;
vec3 vertexToEye;


float diffuse(vec3 normal)
{
	return clamp(dot(normal, -sunlight.direction) * sunlight.intensity,0.2,1);
}

float specular(vec3 normal)
{
	normal.y *= 0.25;
	normal = normalize(normal);
	vec3 reflectionVector = normalize(reflect(sunlight.direction, normal));
	
	float specular = max(0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, shininess) * emission;
}


float fresnelApproximated(vec3 normal)
{
    vec3 halfDirection = normalize(normal + vertexToEye );
    
    float cosine = dot( halfDirection, vertexToEye );
    float product = max( cosine, 0.0 );
    float factor = pow( product, 1.0 );
    
    return 1-factor;
}


float fresnel(vec3 normal, vec3 tx, vec3 ty)
{
	float cosThetaV = dot(vertexToEye, normal);
	float phiV = atan(dot(vertexToEye, ty), dot(vertexToEye, tx));
	float sigmaV = sqrt(SigmaSqX * pow(cos(phiV),2) + SigmaSqY * pow(sin(phiV),2));
	
	return clamp(R + (1 - R) * pow(1-cosThetaV,5 * exp(-2.69*sigmaV)) / (1+22.7 * pow(sigmaV,1.5)),0,1);
}

float erfc(float x)
{
	return 2.0 * exp(-x * x) / (2.319 * x + sqrt(4.0 + 1.52 * x * x));
}

float Lambda(float phi, float theta)
{
	float v = 1 / sqrt(2*(SigmaSqX * pow(cos(phi),2) + SigmaSqY * pow(sin(phi),2)) * tan(theta));
    return max(0.0, (exp(-v * v) - v * sqrt(M_PI) * erfc(v)) / (2.0 * v * sqrt(M_PI)));
}

float reflectedSunRadiance(vec3 normal, vec3 tx, vec3 ty)
{
    vec3 h = normalize(-sunlight.direction + vertexToEye);
    float zetaX = - normal.x/normal.y;
    float zetaY = - normal.z/normal.y;
	float cosThetaV = dot(vertexToEye, normal);
	float phiV = atan(dot(vertexToEye, ty), dot(vertexToEye, tx));
	float phiI = atan(dot(-sunlight.direction, ty), dot(-sunlight.direction, tx));

    float p = exp(-0.5 * (zetaX * zetaX / SigmaSqX + zetaY * zetaY / SigmaSqY))/ (2.0 * M_PI * sqrt(SigmaSqX * SigmaSqY));

    float thetaV = acos(phiV);
    float thetaL = acos(phiI);

    float fresnel = R + (1-R) * pow(1.0 - dot(vertexToEye, h), 5.0);

    return (p  * fresnel) / 4 * pow(dot(h,normal),4) * cosThetaV * (1.0 + Lambda(phiV, thetaV) + Lambda(phiI, thetaL));
}

 
void main(void)
{
	vertexToEye = normalize(eyePosition - positionF);
	float dist = length(eyePosition - positionF);
	
	// normal
	vec3 normal = 2 * texture(normalmap, texCoordF).rbg - 1;
	float wn = max(1,dist * 0.002);
	
	normal.y *= wn;
	normal = normalize(normal);
	
	// BRDF lighting, high performance
	// SigmaSqX = min(0.1,0.00005 * dist);
	// SigmaSqY = min(0.1,0.00005 * dist);
	// vec3 bitangent = normalize(cross(tangent, normal));
	// float F = fresnel(normal, tangent, bitangent);
	
	// Fresnel Term
	float F = fresnelApproximated(normal);
	
	// projCoord //
	vec3 dudvCoord = normalize((2 * texture(dudv, texCoordF + distortion).rbg) - 1);
	vec2 projCoord = vec2(gl_FragCoord.x/windowWidth, gl_FragCoord.y/windowHeight);
 
    // Reflection //
	vec2 reflecCoords = projCoord.xy + dudvCoord.rb * kReflection;
	reflecCoords = clamp(reflecCoords, kReflection, 1-kReflection);
    vec4 reflectionColor = mix(texture(waterReflection, reflecCoords),reflectionColor,  0.2);
    reflectionColor *= F;
 
    // Refraction //
	vec2 refracCoords = projCoord.xy + dudvCoord.rb * kRefraction;
	refracCoords = clamp(refracCoords, kRefraction, 1-kRefraction);
	
    vec4 refractionColor = mix(texture(waterRefraction, refracCoords), refractionColor, 0.2); 
	refractionColor *= 1-F;
	
	float diffuse = diffuse(normal);
	float specular = specular(normal);
	vec4 specularLight = vec4(sunlight.color * specular,0);
	
	vec4 fragColor = (reflectionColor + refractionColor) * diffuse + specularLight; 
	
	gl_FragColor = fragColor;
}