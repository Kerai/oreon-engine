package engine.renderpipeline.shaderPrograms.ocean;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.Camera;
import engine.core.ResourceLoader;
import engine.gameObject.GameObject;
import engine.math.Matrix4f;
import engine.models.data.Material;
import engine.renderer.water.Ocean;
import engine.renderpipeline.Shader;

public class OceanGrid extends Shader{

private static OceanGrid instance = null;
	

	public static OceanGrid getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new OceanGrid();
	    }
	      return instance;
	}
	
	protected OceanGrid()
	{
		super();
		
		addVertexShader(ResourceLoader.loadShader("ocean/grid/Vertex.glsl"));
		addTessellationControlShader(ResourceLoader.loadShader("ocean/grid/Tessellation Control.glsl"));
		addTessellationEvaluationShader(ResourceLoader.loadShader("ocean/grid/Tessellation Evaluation.glsl"));
		addGeometryShader(ResourceLoader.loadShader("ocean/grid/Geometry.glsl"));
		addFragmentShader(ResourceLoader.loadShader("ocean/grid/Fragment.glsl"));
		compileShader();
		
		addUniform("projectionViewMatrix");
		addUniform("worldMatrix");
		addUniform("eyePosition");
		addUniform("color");
		
		addUniform("displacementScale");
		addUniform("choppiness");
		addUniform("texDetail");
		addUniform("tessFactor");
		addUniform("tessSlope");
		addUniform("tessShift");
		addUniform("Dy");
		addUniform("Dx");
		addUniform("Dz");
		
		for (int i=0; i<6; i++)
		{
			addUniform("frustumPlanes[" + i +"]");
		}
	}
	
	public void sendUniforms(Matrix4f worldMatrix, Matrix4f projectionMatrix, Matrix4f modelViewProjectionMatrix)
	{
		setUniform("projectionViewMatrix", projectionMatrix);
		setUniform("worldMatrix", worldMatrix);
		setUniform("eyePosition", Camera.getInstance().getPosition());
		
		for (int i=0; i<6; i++)
		{
			setUniform("frustumPlanes[" + i +"]", Camera.getInstance().getFrustumPlanes()[i]);
		}
	}
	
	public void sendUniforms(GameObject object)
	{
		Ocean ocean = (Ocean) object;
		setUniformf("displacementScale", ocean.getDisplacementScale());
		setUniformf("choppiness", ocean.getChoppiness());
		setUniformi("texDetail", ocean.getTexDetail());
		setUniformi("tessFactor", ocean.getTessellationFactor());
		setUniformf("tessSlope", ocean.getTessellationSlope());
		setUniformf("tessShift", ocean.getTessellationShift());
		
		glActiveTexture(GL_TEXTURE0);
		ocean.getFFT().getDy().bind();
		setUniformi("Dy", 0);
		glActiveTexture(GL_TEXTURE1);
		ocean.getFFT().getDx().bind();
		setUniformi("Dx", 1);
		glActiveTexture(GL_TEXTURE2);
		ocean.getFFT().getDz().bind();
		setUniformi("Dz", 2);
	}
	
	public void sendUniforms(Material material)
	{	
		setUniform("color", material.getColor());
	}
}
